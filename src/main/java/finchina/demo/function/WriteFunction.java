package finchina.demo.function;

import cn.hutool.core.thread.GlobalThreadPool;
import com.alibaba.fastjson.JSON;
import finchina.demo.dto.MainNewsBean;
import finchina.demo.dto.NewsSinkBean;
import finchina.demo.util.Constance;
import finchina.demo.writer.ElasticSearchWriter;
import finchina.demo.writer.MongoDBWriter;
import finchina.demo.writer.dto.WriterInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: kongxr
 * @Date: 2022-10-25 10:22
 *
 * @Description:
 */
@Slf4j
@Component
public class WriteFunction implements KeyValueMapper<String, NewsSinkBean, KeyValue> {

    final WriterInfo writerInfo;

    transient ElasticSearchWriter elasticSearchWriter;

    transient MongoDBWriter mongoDBWriter;

    public WriteFunction(ElasticSearchWriter elasticSearchWriter,MongoDBWriter mongoDBWriter) {
        writerInfo = new WriterInfo(Constance.INFO_NOTICE_BOND, "notice.txt", false, "list");
        this.elasticSearchWriter = elasticSearchWriter;
        this.mongoDBWriter = mongoDBWriter;
    }


    @Override
    public KeyValue apply(String s, NewsSinkBean sinkBean) {
        System.out.println(JSON.toJSONString(sinkBean));
        if(s == null){
            return new KeyValue(null,null);
        }
        try {
            long s1 = System.currentTimeMillis();
            List<MainNewsBean> mainList = sinkBean.getMainList();
            CompletableFuture<Long> mgFuture = CompletableFuture.supplyAsync(()-> mongoDBWriter.invoke(mainList, writerInfo), GlobalThreadPool.getExecutor());
            CompletableFuture<Long> esFuture = CompletableFuture.supplyAsync(()-> elasticSearchWriter.invoke(mainList, writerInfo), GlobalThreadPool.getExecutor());
            CompletableFuture.allOf(mgFuture, esFuture).get();
            long sinkDuration = System.currentTimeMillis() - s1;
            log.info("{} 数据处理总耗时：{} ms， 数据查询耗时：{} ms， 主表数据量：{}，ES-写入耗时：{} ms； MG-写入耗时：{} ms； 总流程耗时：{} ms", writerInfo.getTaskName(), sinkBean.getTotalDuration(),
                    sinkBean.getQueryDuration(), mainList.size(), esFuture.get(), mgFuture.get(), sinkDuration);
        } catch (Exception e){

        }

        return new KeyValue(null,null);
    }

    public void writeExecute(){

    }

}
