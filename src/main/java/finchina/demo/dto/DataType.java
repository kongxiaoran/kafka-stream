package finchina.demo.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import finchina.demo.util.Constance;
import finchina.demo.util.DateUtils;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: kongxr
 * @Date: 2022-10-20 9:48
 * @Description:
 */
@Getter
public enum DataType {

    NOTICE_BOND("债券公告") { //债券公告

        @Override
        public boolean resultBuild(JSONObject doc, MainNewsBean newsBean) {
            // guid 为null直接过滤  原代码逻辑
            // if (StringUtils.isEmpty(bean.getId())) {
            //                iterator.remove();
            //                continue;
            //            }
            String guid = doc.getString("Guid");
            if (StringUtils.isEmpty(guid)) {
//                System.out.println("过滤(guid=null):"+ JSON.toJSONString(doc));
                return false;
            }
            Integer contentType = doc.getInteger("NW1302_011");
            if (null != contentType && 1 != contentType) {
//                System.out.println("过滤(contentType=null && 1 != contentType):"+ JSON.toJSONString(doc));
                return false;
            }
            if (null != contentType) {
                newsBean.setContentType(String.valueOf(contentType));
            }
            newsBean.setId(guid);
            newsBean.setKeyId(doc.getLong("ID"));
            newsBean.setGuid(guid);
            newsBean.setType(Constance.INFO_NOTICE_BOND);
            newsBean.setSourceTable("tNW1302_NEW");
            Long infoId = doc.getLong("BondID");
            if (null != infoId) {
                newsBean.setInfoId(String.valueOf(infoId));
                newsBean.setBondId(infoId);
            }
            newsBean.setExclude(0);
            newsBean.setTitle(doc.getString("NW1302_001"));
            newsBean.setSource(doc.getString("source"));
            newsBean.setContent(doc.getString("content"));
//            newsBean.setLinkurl(doc.getString("NW1306_005"));
            newsBean.setEtime(DateUtils.format(doc.getDate("PUBISHDATE"), Constance.yyyyMMddHHmmss));
            newsBean.setEtime_t1(DateUtils.format(doc.getDate("EntryDT"), Constance.yyyyMMddHHmmss));
            newsBean.setFlag(doc.getInteger("Flag"));
//            newsBean.keyByIdHash(Constance.INFO_NOTICE_BOND, newsBean.getId());

            List<Organization> list = new ArrayList<>();
            Organization org = new Organization();
            org.setKid(Constance.INFO_NOTICE_BOND + "-" + doc.getInteger("ID"));
            Integer risk = doc.getInteger("NW1302_005");
            if (null != risk) {
                org.setOrg_risk(String.valueOf(risk));
            }
            org.setTrcode(doc.getString("TRCode"));
            org.setItcode(doc.getString("COMPANYCODE"));
            org.setSourceTable("tNW1302_NEW");
            list.add(org);
            newsBean.setList(list);
            return true;
        }
    };
    private final String dataType;

    DataType(String dataType) {
        this.dataType = dataType;
    }

    public abstract boolean resultBuild(JSONObject doc, MainNewsBean newsBean);

}
