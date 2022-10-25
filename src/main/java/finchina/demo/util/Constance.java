package finchina.demo.util;


import finchina.demo.dto.DataType;

import java.util.*;

public class Constance {


    public static final String DATA_REDIS_PRE = "_redisType_1!";
    public static final String COMMON_PREFIX = "_prefix_";
    public static final String SUFFIX = "_first";
    public static int PAGE_SIZE = 2000;


    public static int intervalMs = 300000;

    public static int BATCH_SIZE = 1000;

    public static String UPDATE_TIME_NEW = finchina.demo.util.Constance.initial_update_time_new + finchina.demo.util.Constance.SUFFIX;

    public static final String NEWS_KEY = "NewsCode";

    public static final String BOND_KEY = "BondID";

    public static final String FORMATTER_SSS = ".SSS";
    public static final String FORMATTER_HHmmssSSS = " HH:mm:ss.SSS";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public static final String yyyy_MM_dd_HHmmss = "yyyy-MM-dd HH:mm:ss";

    public static final String yyyy_MM_dd = "yyyy-MM-dd";

    public static final String FORMATTER_HHmmss = "HHmmss.000";

    public static final String yyyyMMddHHmmss_SSS = "yyyyMMddHHmmss.SSS";
    public static final String yyyy_MM_dd_HHmmss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String DATASOURCE2 = "2";
    public static final String PREFIX = DATA_REDIS_PRE + COMMON_PREFIX;
    public static final String MODULE = "_flinkImport#";
    public static final String initial_update_time_new = PREFIX + DATASOURCE2 + MODULE + "update_time_new";

    public static final String risk_event = "1";
    public static final String new_related_source = "2";
    public static final String new_through_source = "8";
    public static final String institution_app = "9";
    public static final String research_source = "5";
    public static final String notice_HK = "6";
    public static final String notice_main = "4";
    // 空白格
    public static final String WHITESPACE = " ";



    public static final List<String> tCRNW0003_1_relation_Type = new ArrayList<>();
    public static final List<Integer> tCRNW1003_2_relation_Type = new ArrayList<>();

    /**
     * 诚信无标题处理Map
     */
    public static final Map<String, String> noTitleType1Map = new HashMap<String, String>();
    public static final Map<String, String> noTitleType2Map = new HashMap<String, String>();

    public static final Map<String, String> NODE_MAP = new HashMap<String, String>();

    //公告重要性（存的是值=0的分类）
    public static final List<String> noticeImportance = new ArrayList<>();
    //公告正负面（存的是值=1的分类）
    public static final List<String> noticeNegative = new ArrayList<>();

    public static final Map<String, DataType> stringDataTypeMapping = new HashMap<>();

    static {

        NODE_MAP.put("10.10.18.39","1");
        NODE_MAP.put("10.10.18.40","2");

        noTitleType1Map.put("监管关注", "%s被%s监管关注");
        noTitleType1Map.put("监管(约见)谈话", "%s被%s监管(约见)谈话");
        noTitleType1Map.put("警示", "%s被%s警示");
        noTitleType1Map.put("责令改正", "%s被%s责令改正");
        noTitleType1Map.put("要求提交书面承诺", "%s被%s要求提交书面承诺");
        noTitleType1Map.put("通报批评", "%s被%s通报批评");
        noTitleType1Map.put("致歉说明", "%s被%s要求致歉说明");
        noTitleType1Map.put("公开谴责", "%s被%s公开谴责");
        noTitleType1Map.put("市场禁入", "%s被%s处罚，要求市场禁入");
        noTitleType1Map.put("罚款", "%s被%s罚款");
        noTitleType1Map.put("拘留", "%s被%s拘留");
        noTitleType1Map.put("稽查", "%s被%s稽查");
        noTitleType1Map.put("立案调查", "%s被%s立案调查 ");
        noTitleType1Map.put("自查违规", "%s被%s要求自查违规");
        noTitleType1Map.put("没收违法所得", "%s被%s没收违法所得");
        noTitleType1Map.put("列入经营异常名录", "%s被%s列入经营异常名录");
        noTitleType1Map.put("纳入被执行人", "%s被%s纳入被执行人");
        noTitleType1Map.put("公示为失联机构", "%s被%s公示为失联机构");
        noTitleType1Map.put("公示为异常机构", "%s被%s公示为异常机构");
        noTitleType1Map.put("列入不良行为记录", "%s被%s列入不良行为记录");
        noTitleType1Map.put("移送司法机关", "%s被%s移送司法机关");
        noTitleType2Map.put("纳入失信被执行人", "%s被纳入失信被执行人");
        noTitleType2Map.put("暂停或取消相关资格", "%s被暂停或取消相关资格");
        noTitleType2Map.put("黑名单", "%s被纳入黑名单");

        tCRNW0003_1_relation_Type.add("1");
        tCRNW0003_1_relation_Type.add("2");
        tCRNW0003_1_relation_Type.add("3");
        tCRNW0003_1_relation_Type.add("5");

        tCRNW1003_2_relation_Type.add(1);
        tCRNW1003_2_relation_Type.add(2);
        tCRNW1003_2_relation_Type.add(23);
        tCRNW1003_2_relation_Type.add(24);
        tCRNW1003_2_relation_Type.add(25);
        tCRNW1003_2_relation_Type.add(26);
        tCRNW1003_2_relation_Type.add(27);


        noticeImportance.add("8225");
        noticeImportance.add("8288");
        noticeImportance.add("2065");
        noticeImportance.add("2075");
        noticeImportance.add("2063");
        noticeImportance.add("2055");
        noticeImportance.add("130");
        noticeImportance.add("1550");
        noticeImportance.add("1569");
        noticeImportance.add("1573");
        noticeImportance.add("1589");
        noticeImportance.add("1596");
        noticeImportance.add("1598");
        noticeImportance.add("1602");
        noticeImportance.add("1614");
        noticeImportance.add("1615");
        noticeImportance.add("1619");
        noticeImportance.add("1620");
        noticeImportance.add("1621");
        noticeImportance.add("1622");
        noticeImportance.add("1624");
        noticeImportance.add("1627");
        noticeImportance.add("1629");
        noticeImportance.add("1630");
        noticeImportance.add("1632");
        noticeImportance.add("1633");
        noticeImportance.add("1634");
        noticeImportance.add("1635");
        noticeImportance.add("1636");
        noticeImportance.add("1638");
        noticeImportance.add("1639");
        noticeImportance.add("1640");
        noticeImportance.add("1641");
        noticeImportance.add("1644");
        noticeImportance.add("1645");
        noticeImportance.add("1646");
        noticeImportance.add("1647");
        noticeImportance.add("1648");
        noticeImportance.add("1649");
        noticeImportance.add("1650");
        noticeImportance.add("1651");
        noticeImportance.add("1653");
        noticeImportance.add("1654");
        noticeImportance.add("1664");
        noticeImportance.add("1665");
        noticeImportance.add("1666");
        noticeImportance.add("1667");
        noticeImportance.add("1675");
        noticeImportance.add("1679");
        noticeImportance.add("1691");
        noticeImportance.add("1692");
        noticeImportance.add("1693");
        noticeImportance.add("1695");
        noticeImportance.add("1696");
        noticeImportance.add("1699");
        noticeImportance.add("1700");
        noticeImportance.add("1702");
        noticeImportance.add("1703");
        noticeImportance.add("1704");
        noticeImportance.add("1708");
        noticeImportance.add("1709");
        noticeImportance.add("1711");
        noticeImportance.add("1712");
        noticeImportance.add("1713");
        noticeImportance.add("1714");
        noticeImportance.add("1715");
        noticeImportance.add("1727");
        noticeImportance.add("1761");
        noticeImportance.add("1773");
        noticeImportance.add("1774");
        noticeImportance.add("1776");
        noticeImportance.add("1777");
        noticeImportance.add("1778");
        noticeImportance.add("1780");
        noticeImportance.add("1783");
        noticeImportance.add("1786");
        noticeImportance.add("1787");
        noticeImportance.add("1788");
        noticeImportance.add("1789");
        noticeImportance.add("1790");
        noticeImportance.add("1791");
        noticeImportance.add("1792");
        noticeImportance.add("1795");
        noticeImportance.add("1796");
        noticeImportance.add("8344");
        noticeImportance.add("8375");
        noticeImportance.add("8349");
        noticeImportance.add("2060");
        noticeImportance.add("8346");
        noticeImportance.add("8176");
        noticeImportance.add("8277");
        noticeImportance.add("8089");
        noticeImportance.add("8326");
        noticeImportance.add("8536");
        noticeImportance.add("8343");
        noticeImportance.add("8294");
        noticeImportance.add("8292");
        noticeImportance.add("8293");
        noticeImportance.add("2068");
        noticeImportance.add("8296");
        noticeImportance.add("8895");
        noticeImportance.add("8896");
        noticeImportance.add("8897");
        noticeImportance.add("8898");
        noticeImportance.add("8899");
        noticeImportance.add("8900");
        noticeImportance.add("8926");
        noticeImportance.add("8907");
        noticeImportance.add("8903");
        noticeImportance.add("8906");
        noticeImportance.add("8902");
        noticeImportance.add("8905");


        noticeNegative.add("8323");
        noticeNegative.add("8352");
        noticeNegative.add("8245");
        noticeNegative.add("8299");
        noticeNegative.add("8190");
        noticeNegative.add("8246");
        noticeNegative.add("8247");
        noticeNegative.add("8248");
        noticeNegative.add("8251");
        noticeNegative.add("8354");
        noticeNegative.add("8325");
        noticeNegative.add("8254");
        noticeNegative.add("8154");
        noticeNegative.add("8180");
        noticeNegative.add("8255");
        noticeNegative.add("8258");
        noticeNegative.add("8155");
        noticeNegative.add("8329");
        noticeNegative.add("8357");
        noticeNegative.add("8157");
        noticeNegative.add("8260");
        noticeNegative.add("8159");
        noticeNegative.add("8261");
        noticeNegative.add("8263");
        noticeNegative.add("8162");
        noticeNegative.add("8264");
        noticeNegative.add("8265");
        noticeNegative.add("8161");
        noticeNegative.add("8169");
        noticeNegative.add("8331");
        noticeNegative.add("8184");
        noticeNegative.add("8160");
        noticeNegative.add("8347");
        noticeNegative.add("8185");
        noticeNegative.add("8268");
        noticeNegative.add("8271");
        noticeNegative.add("8371");
        noticeNegative.add("8278");
        noticeNegative.add("8369");
        noticeNegative.add("8341");
        noticeNegative.add("8370");
        noticeNegative.add("8281");
        noticeNegative.add("8158");
        noticeNegative.add("8257");
        noticeNegative.add("8183");
        noticeNegative.add("8334");
        noticeNegative.add("8362");
        noticeNegative.add("8170");
        noticeNegative.add("8283");
        noticeNegative.add("8188");
        noticeNegative.add("8284");
        noticeNegative.add("8372");
        noticeNegative.add("8282");
        noticeNegative.add("8373");
        noticeNegative.add("8336");
        noticeNegative.add("8364");
        noticeNegative.add("8337");
        noticeNegative.add("8365");
        noticeNegative.add("8338");
        noticeNegative.add("8366");
        noticeNegative.add("8339");
        noticeNegative.add("8340");
        noticeNegative.add("8368");
        noticeNegative.add("8285");
        noticeNegative.add("7749");
        noticeNegative.add("8150");
        noticeNegative.add("8151");
        noticeNegative.add("8111");
        noticeNegative.add("8350");
        noticeNegative.add("8351");
        noticeNegative.add("8395");
        noticeNegative.add("8153");
        noticeNegative.add("8152");
        noticeNegative.add("2072");
        noticeNegative.add("1553");
        noticeNegative.add("1554");
        noticeNegative.add("1555");
        noticeNegative.add("1577");
        noticeNegative.add("1579");
        noticeNegative.add("1584");
        noticeNegative.add("1586");
        noticeNegative.add("1594");
        noticeNegative.add("1597");
        noticeNegative.add("1608");
        noticeNegative.add("1611");
        noticeNegative.add("1672");
        noticeNegative.add("1673");
        noticeNegative.add("1674");
        noticeNegative.add("1772");
        noticeNegative.add("8273");
        noticeNegative.add("8330");
        noticeNegative.add("8358");
        noticeNegative.add("8332");
        noticeNegative.add("8359");
        noticeNegative.add("9012");
        noticeNegative.add("8328");
        noticeNegative.add("8333");
        noticeNegative.add("8187");
        noticeNegative.add("8361");

        stringDataTypeMapping.put("tNW1302_NEW",DataType.NOTICE_BOND);
    }

    // 资讯类别-预警新闻
    public static final String INFO_NEWS = "news";

    public static final String INFO_NOTICE_1306 = "notice_1306";

    public static final String INFO_RISK = "riskEvent";
    // 资讯类别-API公告通道
    public static final String INFO_NOTICE_API = "notice_api";
    // 资讯类别-股票最新公告
    public static final String INFO_NOTICE_ORG = "notice_org";
    // 资讯类别-股票公告
    public static final String INFO_NOTICE_STOCK = "notice_stock";
    // 资讯类别-债券公告
    public static final String INFO_NOTICE_BOND = "notice_bond";
    // 资讯类别-债券公告linkurl
    public static final String INFO_NOTICE_BOND_LINKURL = "notice_bond_linkurl";
    // 资讯类别-港股公告
    public static final String INFO_NOTICE_HK = "notice_hk";
    // 资讯类别-公告附件
    public static final String INFO_NOTICE_FILE = "notice_file";
    // 资讯类别-诚信
    public static final String INFO_CREADIT = "creadit";
    // 资讯类别-涉诉
    public static final String INFO_LITIGATE = "litigate";
    // 资讯类别-APP机构事件（按机构）
    public static final String INFO_COMPANYEVENT = "companyEvent";
    // 资讯类别-APP机构事件（按代码）
    public static final String INFO_COEVENT = "coEvent";
    // 资讯类别-研究报告
    public static final String INFO_RESEARCHREPORT = "researchReport";

    public static final String INCR_COMMON_TYPE = "0";
    public static final String INCR_AI_TYPE = "1";
    public static final String INCR_JB_TYPE = "2";
    public static final String INCR_TS_TYPE = "3";
    public static final String INCR_1306_TYPE = "4";
    public static final String INCR_STOCK_TYPE = "5";
    public static final String INCR_BOND_TYPE = "6";
    public static final String INCR_REPORT_TYPE = "8";
    public static final String INCR_RISK_TYPE = "9";

}
