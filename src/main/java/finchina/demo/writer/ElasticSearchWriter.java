package finchina.demo.writer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.gw.finchina.old.common.util.PropertyUtil;
import finchina.demo.dto.MainNewsBean;
import finchina.demo.dto.Organization;

import finchina.demo.util.EsScriptUtil;
import finchina.demo.writer.dto.WriterInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.Script;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component("elasticSearchWriterNew")
public class ElasticSearchWriter {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    String indexName = "kafkastream_tnw1302_new";

    public long invoke(List<MainNewsBean> mainList, WriterInfo writerInfo) {
        if (CollectionUtils.isEmpty(mainList)) {
            return 0;
        }
        try {
            return insert(mainList, writerInfo.getIsMain(), writerInfo.getTaskName(), writerInfo.getField());
        } catch (Exception e) {
            try {
                //重试一次
                return insert(mainList, writerInfo.getIsMain(), writerInfo.getTaskName(), writerInfo.getField());
            } catch (Exception ex) {
                log.error("{} 数据写入ES失败，异常原因：{}", writerInfo.getTaskName(), ex.getMessage());
                //错误数据写入本地文件
//                CommonService.errorDataWrite2File("ES_" + writerInfo.getFileName(), mainList);
                return 0;
            }
        }
    }


    /**
     * 新闻主表数据批量写入
     */
    public long insert(List<MainNewsBean> mainList, Boolean isMain, String taskName, String field) {
        try {
            long start = System.currentTimeMillis();
            BulkRequest request = buildRequest(mainList, isMain, taskName, field);
            BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                log.error("{} 主表写入失败，失败原因：{}", taskName, response.buildFailureMessage());
                throw new IllegalArgumentException("ES主表写入失败，异常信息：" + response.buildFailureMessage());
            }
            return System.currentTimeMillis() - start;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("es 写入异常，异常原因：" + e.getMessage());
        }
    }

    /**
     * 写入请求批量构建
     */
    public BulkRequest buildRequest(List<MainNewsBean> mainList, Boolean isMain, String taskName, String field) {
        //可能是重试，重试的情况重新生成新的BulkRequest
        BulkRequest request = new BulkRequest();
        for (MainNewsBean bean : mainList) {
            addRequest(request, bean, isMain, taskName, field);
        }
        return request;
    }


    private void addRequest(BulkRequest request, MainNewsBean entity, Boolean isMain, String taskName, String field) {
        if (StringUtils.isEmpty(entity.getId())) {
            log.error("id is missing,index :{} ,entity:{} ", indexName, JSON.toJSONString(entity));
            return;
        }
        if (isMain) {
            request.add(basicOperate(entity));
        } else {
            if (StringUtils.isBlank(field)) {
                throw new IllegalArgumentException("Field is missing");
            }
            childOperate(request, entity, taskName, field);
        }
    }

    /**
     * 对子表进行操作
     * 1、子表不写入id和flag字段，子表不需要
     *
     * @date 2021/11/17 08:35
     */
    private void childOperate(BulkRequest request, MainNewsBean entity, String taskName, String field) {
        //当子表先来的时候需要，保留主表的id，更新时间、isHidden 字段
        Map<String, Object> basicMap = new HashMap<>();
        //针对公告单独处理
        if (taskName.startsWith("notice") || taskName.equals("riskEvent") || taskName.equals("news_institution_app")) {
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
            filter.getIncludes().addAll(Arrays.asList("flag", "exclude", "type", "sourceTable", "updateTime", "updateTimeNew", "etime"));//es中需添加字段
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(entity, filter));
            basicMap.putAll(jsonObject);
            basicMap.put("isHidden", 0);
        } else {
            basicMap.put("id", entity.getId());
            basicMap.put("isHidden", 1);
        }
        String insertSource = JSON.toJSONString(basicMap, SerializerFeature.WriteMapNullValue);
        List<Organization> list = entity.getList();
        for(Organization org : list){
            IndexRequest indexRequest = new IndexRequest().index(indexName)
                    .id(entity.getId())
                    .source(insertSource, XContentType.JSON);
            //删除子表
            if (entity.getFlag() == 1) {
                Map<String, String> map = new HashMap<>();
                map.put("kid", entity.getKid());
                UpdateRequest updateRequest =  new UpdateRequest()
                        .index(indexName)
                        .id(entity.getId())
                        .upsert(indexRequest)
                        .script(EsScriptUtil.getListDeleteItem("kid", map, field));
                request.add(updateRequest);
            } else {//插入或更新子表
                //针对公告单独处理
                if (taskName.startsWith("notice") || taskName.equals("riskEvent") || taskName.equals("news_institution_app")) {
                    Map<String, Object> map = new HashMap<>(JSON.parseObject(JSON.toJSONString(org)));
                    // 创建更新请求，指定index，type,id,如果indexRequest 有值 （存在该数据）则用doc指定的内容更新indexRequest中指定的source，如果不存在该数据，则插入一条indexRequest指定的source数据
                    //公告区分来源
                    Script script;
                    if ("notice_1306".equals(taskName) || "notice_kafka".equals(taskName)) {
                        script = EsScriptUtil.getUpdateOrInsertWithDeleteFor1306("kid", map, field);
                    } else if ("notice_stock".equals(taskName) || "notice_bond".equals(taskName)) {
                        script = EsScriptUtil.getUpdateOrInsertWithDeleteFor1301_new("kid", map, field, entity.getId());
                    } else {
                        script = EsScriptUtil.getUpdateOrInsert("kid", map, field);
                    }
                    UpdateRequest updateRequest =  new UpdateRequest().index(indexName)
                            .id(entity.getId())
                            .upsert(indexRequest)
                            .scriptedUpsert(true)
                            .script(script);
                    request.add(updateRequest);
                }  else {
                    SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
                    if ("list".equals(field)) {
                        filter.getExcludes().addAll(Arrays.asList("id", "type", "etime", "updateTime", "updateTimeNew"));//子表不需要id,flag字段,只保留子表需要的字段
                    } else {
                        filter.getExcludes().addAll(Arrays.asList("flag", "id", "type", "etime", "updateTime", "updateTimeNew"));//子表不需要id,flag字段,只保留子表需要的字段
                    }
                    JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(entity, filter));
                    Map<String, Object> map = new HashMap<>(jsonObject);
                    // 创建更新请求，指定index，type,id,如果indexRequest 有值 （存在该数据）则用doc指定的内容更新indexRequest中指定的source，如果不存在该数据，则插入一条indexRequest指定的source数据
                    UpdateRequest updateRequest =  new UpdateRequest().index(indexName)
                            .id(entity.getId())
                            .upsert(indexRequest)
                            .scriptedUpsert(true)
                            .script(EsScriptUtil.getUpdateOrInsert("kid", map, field));
                    request.add(updateRequest);
                }
            }
        }

    }

    /**
     * @date 2021/11/17 08:35
     * @description 对主表进行操作
     */
    private UpdateRequest basicOperate(MainNewsBean entity) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        List<Organization> list = entity.getList();
        filter.getIncludes().addAll(Arrays.asList("flag", "exclude", "type", "sourceTable", "updateTime", "updateTimeNew", "etime"));//es中需添加字段
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(entity, filter));
        int hidden = 1;
        if(CollectionUtils.isEmpty(entity.getList())){
            hidden = 0;
        } else {
            jsonObject.put("list", JSON.parseArray(JSON.toJSONString(list)));
        }
        if (Objects.isNull(jsonObject.get("isHidden"))) {
            jsonObject.put("isHidden", hidden);
        }

        //插入包括createTime 和 updateTime
        String insertSource = JSONObject.toJSONString(jsonObject);
        //更新包括updateTime
        String updateSource = JSONObject.toJSONString(jsonObject);

        IndexRequest indexRequest = new IndexRequest().index(indexName)
                .id(entity.getId())
                .source(insertSource, XContentType.JSON);
        //如果是flag=1并且对应的kid也一致的话，则为无效主表数据
        if ( entity.getFlag() == 1) {
            //1、是否存在，如果不存在则插入upsert -> 对应的doc为flag=1，2、如果存在，则会执行更新操作，也就是执行脚本的内容，则比较kid是否一致，如果一致，则更新flag为1，否则不做操作
            return new UpdateRequest()
                    .index(indexName)
                    .id(entity.getId())
                    .upsert(indexRequest)
                    .script(EsScriptUtil.getCompareAndFlag(entity.getKid()));
        } else {
            // 创建更新请求，指定index，type,id,如果indexRequest 有值 （存在该数据）则用doc指定的内容更新indexRequest中指定的source，如果不存在该数据，则插入一条indexRequest指定的source数据
            return new UpdateRequest().index(indexName)
                    .id(entity.getId())
                    .doc(updateSource, XContentType.JSON)
                    .upsert(indexRequest);
        }

    }
}
