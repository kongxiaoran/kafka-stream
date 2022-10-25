package finchina.demo.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: kongxr
 * @Date: 2022-10-20 9:05
 * @Description:
 */
@Data
public class MainNewsBean {

    private String idType;
    private Long keyId;
    //主表ID
    private String mainId;
    private String infoId;
    private String id;
    private String kid;
    private String cname;
    private Long bondId;
    //新闻编码
    private Long newsCode;
    //标题
    private String title;
    //风险事件属性
    private String rollTitle;
    private String detailPage;

    private String content;
    //来源
    private String source;
    private String type = "news";
    //排版
    private String publish;
    //链接地址
    private String linkurl;
    //打包地址 作为屏蔽标志 一对一 关联表：tcrnw1018  取值字段：tcrnw1018_001   通过newsCode关联
    private String contentUrl;
    //摘要 一对一  关联表：tCRNW1002  取值字段：tCRNW1002_001   通过newsCode关联
    private String digest;
    //屏蔽 一对一  关联表：tCRNW0025  取值字段：(FLAG!=1)AND(CRNW0025_002=0) 为1 否则为0   通过newsCode关联
    //         IIF(FLAG!=1,CRNW1018_001,NULL) AS contentUrl,
    //        IIF((FLAG!=1)AND(CRNW1018_001 IS NOT NULL),0,1) AS exclude
    private Integer exclude;

    private int flag;

    private String sourceTable ;

    private String etime;

    private String etime_t1;

    private List<NewsFile> file;

    private List<Organization> list;

    private String guid;

    private String endDate;

    private String riskEventLevelIdentify;

    private String updateTime;

    private String updateTimeNew;

    private String contentType;

    private String co;

    private String publish_inds;

    private String itname;

    private String researcher;

    private String award;

    private String itcode2;

    private String itcode;

    private Long sourceId;

    public void setNull(){
        this.newsCode = null;
        this.etime = null;
        this.etime_t1 = null;
        this.keyId = null;
    }
}
