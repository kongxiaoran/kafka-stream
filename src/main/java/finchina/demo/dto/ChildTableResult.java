package finchina.demo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ChildTableResult {


    //债券公告正文
    private Map<Long, BondContent> mapContent;
    //债券外链
    private Map<Long, String> mapLink;
    //来源
    private Map<Long, String> mapSource;

    //待转换的机构编码
    private Set<String> itCodes;

    private OrganizationResult organizationResult;
}
