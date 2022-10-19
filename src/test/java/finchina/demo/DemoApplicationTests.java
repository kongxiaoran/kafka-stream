package finchina.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mongodb.bulk.BulkWriteResult;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {


    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Test
    void contextLoads() {

    }

    @Test
    void testMongoDB(){
        List<Pair<Query, Update>> upsertList = new ArrayList<>();
        Query query = new Query(new Criteria("_id").is("111"));
        Update update = new Update();
        update.set("name","kongxr");
        upsertList.add(Pair.of(query, update));
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "kafka_stream_tSR0001");
        bulkOperations.upsert(upsertList);
        BulkWriteResult writeResult = bulkOperations.execute();
        if (!writeResult.wasAcknowledged()) {
            //TODO 异常数据处理
            throw new IllegalArgumentException("mongodb 写入返回异常");
        }
    }

    @Test
    void testElasticSearch() throws IOException {

        JSONObject obj = new JSONObject();
        obj.put("id","222");
        obj.put("name","kongxr");
        String insertSource = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
        IndexRequest indexRequest = new IndexRequest().index("test_index")
                .id("222")
                .source(insertSource, XContentType.JSON);

        UpdateRequest upsert = new UpdateRequest().index("test_index")
                .id("222")
                .doc(insertSource, XContentType.JSON)
                .upsert(indexRequest);

        BulkRequest request = new BulkRequest();
        request.add(upsert);
        restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    }

}
