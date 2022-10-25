//package finchina.demo;
//
//import finchina.demo.mapper.CdcDTOMapper;
//import org.apache.kafka.common.serialization.Serde;
//import org.apache.kafka.common.serialization.Serdes;
//import org.apache.kafka.streams.StreamsBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Component;
//
///**
// * @Author: kongxr
// * @Date: 2022-10-12 15:13
// * @Description:
// */
////@Component
//public class TestProcessor {
//
//    private static final Serde<String> STRING_SERDE = Serdes.String();
//
//    @Value("${spring.data.mongodb.database}")
//    String database;
//
//    @Autowired
//    CdcDTOMapper cdcDTOMapper;
//
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
////    @Autowired
//    void buildPipeline(StreamsBuilder streamsBuilder) {
//
////        KStream<? extends Object, ? extends Object> messageStream = streamsBuilder
////                .stream("kafka-stream-input", Consumed.with(STRING_SERDE, STRING_SERDE))
////                .map(new TestFunctionMapper(cdcDTOMapper)).filter((o, o2) -> {
////                    if (o == null)
////                        return false;
////                    else
////                        return true;
////                });
////
////        KStream<Object, Object> resStream = messageStream.flatMap((KeyValueMapper<Object, Object, List<KeyValue<String,String>>>) (o, o2) -> {
////            Map<Long, String> dataMap = (Map<Long, String>) o2;
////            List<Long> ids = (List<Long>) o;
////            List<KeyValue<String, String>> persons = new ArrayList<>(ids.size());
////            ids.forEach(id -> {
////                persons.add(new KeyValue<String, String>(id.toString(), dataMap.get(id)));
////            });
////            return persons;
////        });
//
////        resStream.to("kafka-stream-output");
////        resStream.toTable().toStream().to("kafka-stream-output");
//
////        KTable<String, Long> wordCounts = messageStream
////                .mapValues((ValueMapper<String, String>) String::toLowerCase)
////                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
////                .groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
////                .count();
////
////        wordCounts.toStream().to("kafka-stream-output");
//    }
//
////    @Autowired
////    void buildPipeline1(StreamsBuilder streamsBuilder) {
////
////        KStream<? extends Object, ? extends Object> messageStream = streamsBuilder
////                .stream("kafka-stream-input", Consumed.with(STRING_SERDE, STRING_SERDE))
////                .map(new TestFunctionMapper(cdcDTOMapper)).filter((o, o2) -> {
////                    if (o == null)
////                        return false;
////                    else
////                        return true;
////                });
////
////        KStream<Object, Object> resStream = messageStream.flatMap((KeyValueMapper<Object, Object, List<KeyValue<String,String>>>) (o, o2) -> {
////            Map<Long, String> dataMap = (Map<Long, String>) o2;
////            List<Long> ids = (List<Long>) o;
////            List<KeyValue<String, String>> persons = new ArrayList<>(ids.size());
////            ids.forEach(id -> {
////                persons.add(new KeyValue<String, String>(id.toString(), dataMap.get(id)));
////            });
////            return persons;
////        });
////
////        resStream.toTable().toStream().to("kafka-stream-output");
////    }
//}