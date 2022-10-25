package finchina.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @Author: kongxr
 * @Date: 2022-10-20 9:06
 * @Description:
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Organization {
    private Long newsCode;
    private String mainId;
    private String kid;
    private String itcode;
    private String itcode2;
    //TODO
    private String trcode;
    private String co;
    private String cname;
    //主副机构
    private String mainOrg;
    //指标代码
    private String org_risk;
    //重要性
    private Integer importance;

    private Integer importanceABS;
    //机构正负属性
    private Integer negative;

    private String relation_type;

    private Integer flag;

    private String groupCode;

    private String groupName;

    private String negative_itcode2;

    private String sourceTable;

    //分类
    private List<String> categoryList;

    private String level1;
    private String level2;
    private String level3;
    private String lastLevel;

    private String publish_inds;

    private String type;

    public void setImportance(int importance) {
        this.importance = importance;
        this.importanceABS = importance < 0 ? importance * -1 : importance;
    }

}
