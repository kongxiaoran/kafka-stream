package finchina.demo.dto;

import lombok.Data;

/**
 * @Author: kongxr
 * @Date: 2022-10-20 9:06
 * @Description:
 */
@Data
public class NewsFile {
    private String kid;
    private String fileUrl;
    private String fileType;
    private String openType;
    private String mainId;
    private Integer filePages;
    private String fileSize;
}
