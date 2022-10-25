package finchina.demo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NewsSinkBean {
    private long totalDuration;

    private long queryDuration;

    private int size;

    private long updateTimeDuration;

    private List<MainNewsBean> mainList;

    private List<Organization> orgList;

    private Map<String, List<Organization>> newsOrgMap;

}
