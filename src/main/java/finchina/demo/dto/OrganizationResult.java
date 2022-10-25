package finchina.demo.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class OrganizationResult {

    private List<Organization> orgList;
    private Set<String> itCodeList;

}
