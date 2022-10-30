package finchina.demo;

import com.alibaba.fastjson.JSON;
import finchina.demo.function.NoticeFunction;
import finchina.demo.function.SerializeFunction;
import finchina.demo.function.WriteFunction;
import finchina.demo.service.gg.NoticeService;
import finchina.demo.util.Constance;
import finchina.demo.writer.ElasticSearchWriter;
import finchina.demo.writer.MongoDBWriter;
import finchina.demo.writer.dto.WriterInfo;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.bson.types.BSONTimestamp;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author: kongxr
 * @Date: 2022-10-20 8:53
 * @Description:
 */
@Component
public class NoticeImportProcessor {

    private static final Serde<String> STRING_SERDE = Serdes.String();

    @Value("${spring.data.mongodb.database}")
    String database;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    MongoDBWriter mongoDBWriter;

    @Autowired
    ElasticSearchWriter elasticSearchWriter;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {


        KStream kafkaStream = streamsBuilder
                .stream("10.10.17.112.dbo.tNW1302_NEW", Consumed.with(STRING_SERDE, STRING_SERDE))
                .map(new SerializeFunction()).filter((key, value) -> {
                    if (key == null)
                        return false;
                    else
                        return true;
                });
        kafkaStream.map(new NoticeFunction(noticeService)).filter((key, value) -> {
            if (key == null)
                return false;
            else
                return true;
        }).map(new WriteFunction(elasticSearchWriter,mongoDBWriter));


//        new WriteFunction(elasticSearchWriter,mongoDBWriter)

//        KStream<Object, Object> resStream = messageStream.flatMap((KeyValueMapper<Object, Object, List<KeyValue<String,String>>>) (o, o2) -> {
//            Map<Long, String> dataMap = (Map<Long, String>) o2;
//            List<Long> ids = (List<Long>) o;
//            List<KeyValue<String, String>> persons = new ArrayList<>(ids.size());
//            ids.forEach(id -> {
//                persons.add(new KeyValue<String, String>(id.toString(), dataMap.get(id)));
//            });
//            return persons;
//        });

//        resStream.to("kafka-stream-output");
//        resStream.toTable().toStream().to("kafka-stream-output");

//        KTable<String, Long> wordCounts = messageStream
//                .mapValues((ValueMapper<String, String>) String::toLowerCase)
//                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
//                .groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
//                .count();
//
//        wordCounts.toStream().to("kafka-stream-output");
    }
}
