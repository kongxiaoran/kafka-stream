package finchina.demo.writer;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.gw.finchina.old.common.util.PropertyUtil;
import com.mongodb.bulk.BulkWriteResult;
import finchina.demo.dto.MainNewsBean;
import finchina.demo.service.CommonService;
import finchina.demo.writer.dto.MongoBulkRequest;
import finchina.demo.writer.dto.WriterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component("mongoDBWriterNew")
public class MongoDBWriter {


    @Resource
    private MongoTemplate mongoTemplate;

    String collectName = PropertyUtil.getProperty("mongodb.sink.collectName");
//    String collectName = "1306_news_doc";

    public long invoke(List<MainNewsBean> mainList, WriterInfo writerInfo) {
        if (CollectionUtils.isEmpty(mainList)) {
            return 0;
        }
        try {
            return sendAfterCheck(mainList, writerInfo.getIsMain(), writerInfo.getTaskName(), writerInfo.getField());
        } catch (Exception e) {
            try {
                Thread.sleep(200);
                //重试一次
                return sendAfterCheck(mainList, writerInfo.getIsMain(), writerInfo.getTaskName(), writerInfo.getField());
            } catch (Exception ex){
                log.error("{} 数据写入MG失败，异常原因：{}",  writerInfo.getTaskName(), e.getMessage());
                //错误数据写入本地文件
//                CommonService.errorDataWrite2File("MG_" + writerInfo.getFileName(), mainList);
                return 0;
            }
        }
    }


    public long sendAfterCheck(List<MainNewsBean> mongoValues, Boolean isMain, String taskName, String field) {
        //根据请求参数创建mongo的请求对象
        long s = System.currentTimeMillis();
        MongoBulkRequest request = build(mongoValues, isMain, taskName, field);
        log.info("{} 数据写入MG逻辑处理耗时：{} ms， 当前批次数据量：{}，操作请求数：{}", taskName, System.currentTimeMillis() - s, mongoValues.size(), request.getUpsertList().size());
        long start = System.currentTimeMillis();
        if (request.isNotEmpty()) {
            BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, collectName);
            if (!CollectionUtils.isEmpty(request.getUpsertList())) {
                bulkOperations.upsert(request.getUpsertList());
            }
            BulkWriteResult writeResult = bulkOperations.execute();
            if (!writeResult.wasAcknowledged()) {
                throw new IllegalArgumentException("mongodb 写入返回异常");
            }
        }
        return System.currentTimeMillis() - start;
    }

    private MongoBulkRequest build(List<MainNewsBean> mongoValues, Boolean isMain, String taskName, String field) {
        List<Pair<Query, Update>> upsertList = new ArrayList<>();
        for(MainNewsBean bean : mongoValues){
            bean.setNull();
            JSONObject baseBean = JSON.parseObject(JSON.toJSONString(bean));
            //主表数据处理逻辑
            Query query = new Query(new Criteria("_id").is(bean.getId()));
            Update update = new Update();
            if(isMain){
                baseBean.remove("list");
                //upsert 整条数据 bulkOperations
                JSONObject jsonObject = (JSONObject) JSON.toJSON(baseBean);
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    if (ObjectUtil.isNotEmpty(entry.getValue())) {
                        update.set(entry.getKey(), entry.getValue());
                    }
                }
                upsertList.add(Pair.of(query, update));
            } else {
                //先删除，
                //在插入,mongoTemplate.bulkOps保证了顺序执行
                //针对公告单独处理
                if (taskName.startsWith("notice") || taskName.equals("riskEvent") || taskName.equals("news_institution_app")) {
                    //内层
                    JSONArray list = baseBean.getJSONArray("list");
                    if (!CollectionUtils.isEmpty(list)) {
                        int size = list.size();
                        Update update2 = new Update();
                        //外层
                        initUpdate(update2, baseBean);
                        if(size == 1){
                            //内层
                            update2.push(field, list.getJSONObject(0));
                        } else {
                            // list包含多个
                            for(int i = 0; i < size; i++){
                                Update update4 = new Update();
                                update4.push(field, list.getJSONObject(i));
                                upsertList.add(Pair.of(query, update4));
                            }
                        }
                        upsertList.add(Pair.of(query, update2));
                    }
                    //股票公告需要删除部分数据
                    if ("notice_stock".equals(taskName) || "notice_bond".equals(taskName)) {
                        //删除id和kid相同的子文档
                        Update update3 = new Update();
                        JSONObject deleteChild = new JSONObject();
                        deleteChild.put("kid", baseBean.getString("id"));
                        update3.pull(field, deleteChild);
                        upsertList.add(Pair.of(query, update3));
                    }
                } else {
                    //再插入
                    Update update2 = new Update();
                    baseBean.remove("id"); //保存入库的时候不需要id
                    SimplePropertyPreFilter filterChild = new SimplePropertyPreFilter();
                    filterChild.getExcludes().addAll(Arrays.asList("flag", "updateTime", "updateTimeNew", "etime", "flag_t1", "flag_t2", "flag_t3", "tmp_id", "etime_t1", "tmStamp", "type", "childTmStamp1", "childTmStamp2"));//子表不需要的字段,只保留子表需要的字段
                    baseBean = JSON.parseObject(JSONObject.toJSONString(baseBean, filterChild));
                    update2.push(field, baseBean);
                    upsertList.add(Pair.of(query, update2));
                }
            }
        }
        MongoBulkRequest mongoBulkRequest = new MongoBulkRequest();
        mongoBulkRequest.setUpsertList(upsertList);
        return mongoBulkRequest;
    }

    private void initUpdate(Update update2, JSONObject baseBean) {
        Set<String> keySet = baseBean.keySet();
        for (String key : keySet) {
            Object obj = baseBean.get(key);
            if (ObjectUtil.isNotEmpty(obj)) {
                if (!"list".equals(key)) {
                    if ("file".equals(key)) {
                        update2.set(key, baseBean.getJSONArray(key));
                    } else {
                        update2.set(key, baseBean.getString(key));
                    }
                }
            }
        }
    }
}
