package finchina.demo.function;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import finchina.demo.dto.MainNewsBean;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.stereotype.Component;

import static finchina.demo.util.Constance.stringDataTypeMapping;

/**
 * @Author: kongxr
 * @Date: 2022-10-20 8:57
 * @Description:
 */
@Component
public class SerializeFunction implements KeyValueMapper<String, String, KeyValue<String, MainNewsBean>> {


    @Override
    public KeyValue apply(String key, String value) {
        JSONObject dataPackage = JSON.parseObject(value);
        String table = dataPackage.getJSONObject("source").getString("table");
        JSONObject dataBean = dataPackage.getJSONObject("after");
        switch (dataPackage.getString("op")) {
            case "r":
                return serializeInit(key, dataBean, table);
        }
        return new KeyValue(null,null);

    }

    public KeyValue<String,MainNewsBean> serializeInit(String key, JSONObject object, String dataType) {
        MainNewsBean mainNewsBean = new MainNewsBean();
        boolean isSuccess = stringDataTypeMapping.get(dataType).resultBuild(object, mainNewsBean);

        if (isSuccess) {
            return new KeyValue(key,mainNewsBean);
        }
        return new KeyValue(null,null);
    }


}
