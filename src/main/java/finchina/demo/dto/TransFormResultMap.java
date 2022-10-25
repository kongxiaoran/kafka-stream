package finchina.demo.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class TransFormResultMap {
    private Map<String, String> itcodeToItcode2Map = new LinkedHashMap<>();

    private Map<String, String> trcodeToItcode2Map = new LinkedHashMap<>();
}
