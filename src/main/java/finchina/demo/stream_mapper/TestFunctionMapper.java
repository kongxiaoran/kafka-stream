package finchina.demo.stream_mapper;


import com.alibaba.fastjson.JSON;
import finchina.demo.dto.Person;
import finchina.demo.mapper.CdcDTOMapper;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Author: kongxr
 * @Date: 2022-10-13 10:11
 * @Description:
 */
@Component
public class TestFunctionMapper implements KeyValueMapper {

    private ArrayList<Long> strings = new ArrayList<>();

    CdcDTOMapper cdcDTOMapper;

    public TestFunctionMapper(CdcDTOMapper cdcDTOMapper) {
        this.cdcDTOMapper = cdcDTOMapper;
    }

    Map<Long, String> getMap(List<Person> persons) {
        Map<Long, String> res = new HashMap<>();
        persons.forEach(value -> {
            res.put(value.getId(), JSON.toJSONString(value));
        });
        return res;
    }

    @Override
    public KeyValue apply(Object s, Object s2) {
        System.out.println("----------------" + s2);
        Optional.ofNullable(s).ifPresent(value -> {
            strings.add(Long.valueOf(value.toString()));
        });
        if (strings.size() % 5 == 0) {
            System.out.println("list size:" + strings.size());
            List<Person> persons = cdcDTOMapper.getList(strings);
            Map<Long, String> personMap = getMap(persons);
            System.out.println("查询结果:\n" + JSON.toJSONString(persons));
            ArrayList<Long> stringsX = (ArrayList<Long>) strings.clone();
            KeyValue<List<Long>, Map<Long, String>> res = new KeyValue<>(stringsX, personMap);
            strings.clear();
            return res;
        }
        return new KeyValue(null, null);
    }

//
//    @Override
//    public KeyValue apply(String s, String s2) {
//        System.out.println("----------------" + s2);
//        Optional.ofNullable(s).ifPresent(value -> {
//            strings.add(Long.valueOf(value.toString()));
//        });
//        if (strings.size() % 5 == 0) {
//            System.out.println("list size:" + strings.size());
//            List<Person> persons = cdcDTOMapper.getList(strings);
//            Map<Long, String> personMap = getMap(persons);
//            System.out.println("查询结果:\n" + JSON.toJSONString(persons));
//            ArrayList<Long> stringsX = (ArrayList<Long>) strings.clone();
//            KeyValue<List<Long>, Map<Long, String>> res = new KeyValue<>(stringsX, personMap);
//            strings.clear();
//            return res;
//        }
//        return new KeyValue(null, null);
//    }
}
