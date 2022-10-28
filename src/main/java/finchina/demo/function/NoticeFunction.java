package finchina.demo.function;

import finchina.demo.dto.*;
import finchina.demo.service.CommonService;
import finchina.demo.service.gg.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author: kongxr
 * @Date: 2022-10-20 14:18
 * @Description:
 */
@Slf4j
@Component
public class NoticeFunction implements KeyValueMapper<String, MainNewsBean, KeyValue> {

    private List<Long> batchIds = new ArrayList<>();
    private Map<String, MainNewsBean> batchMain = new HashMap<>();
    private Map<String, List<Organization>> batchOrg = new HashMap<>();

    private Map<String, String> trCodeMap = new HashMap<>();
    private Map<String, String> itCodeMap = new HashMap<>();
    private Set<Long> bondIdList = new HashSet<>();

    final NoticeService noticeService;

    public NoticeFunction(NoticeService noticeService) {
        this.noticeService = noticeService;
    }


    @Override
    public KeyValue apply(String s, MainNewsBean mainNewsBean) {

        long start = System.currentTimeMillis();

        String id = mainNewsBean.getId();
        MainNewsBean old = batchMain.get(id);
        if (null != old) {
            // id相同的情况下 只保留一条最新的数据 避免写入ES和MG出现异常
            if (mainNewsBean.getKeyId() > old.getKeyId()) {
                batchMain.put(id, mainNewsBean);
            }
        } else {
            batchMain.put(id, mainNewsBean);
        }
        //对应机构信息需要保留
        List<Organization> orgList = batchOrg.get(id);
        if (CollectionUtils.isEmpty(orgList)) {
            orgList = new ArrayList<>();
        }
        Organization org =  mainNewsBean.getList().get(0);
        if (!CollectionUtils.isEmpty(mainNewsBean.getList())) {
            orgList.add(org);
        }
        batchOrg.put(id, orgList);

        String trCode = org.getTrcode();
        String itCode = org.getItcode();
        //trcode转10位机构代码  co转trcode
        if (!StringUtils.isEmpty(trCode)) {
            trCodeMap.put(trCode, trCode);
        }
        if (!StringUtils.isEmpty(itCode)) {
            itCodeMap.put(itCode, itCode);
        }
        Long bondId = mainNewsBean.getBondId();
        if (!StringUtils.isEmpty(bondId)) {
            bondIdList.add(bondId);
        }

        long end = System.currentTimeMillis();
        if (batchMain.size() == 1000) {
            return resOut();
        }

        return new KeyValue(null, null);
    }

    public KeyValue resOut() {
        NewsSinkBean res = resultBuilder();

        batchMain.clear();
        batchOrg.clear();
        trCodeMap.clear();
        itCodeMap.clear();
        batchIds.clear();
        bondIdList.clear();

        return new KeyValue("res",res);
    }

    public NewsSinkBean resultBuilder() {
        NewsSinkBean sinkBean = new NewsSinkBean();
        try {
            long s1 = System.currentTimeMillis();
            // itCode 8位转10位机构代码
            List<String> itCodes = new ArrayList<>((Collection<? extends String>) itCodeMap.values());
            // trcode转10位机构代码
            List<String> trCodes = new ArrayList<>((Collection<? extends String>) trCodeMap.values());
            // 编码转换
            TransFormResultMap transMap = CommonService.codeTransForm(itCodes, trCodes);
            Map<String, String> itcodeToItcode2Map = transMap.getItcodeToItcode2Map();
            Map<String, String> trcodeToItcode2Map = transMap.getTrcodeToItcode2Map();

            List<Long> bondIds = new ArrayList<>((Collection<? extends Long>) bondIdList);
            // 子表数据获取
            ChildTableResult childTableResult = noticeService.childTableResultGet(bondIds);
            //债券外链
            Map<Long, String> mapLink = childTableResult.getMapLink();
            //债券公告正文
            Map<Long, BondContent> mapContent = childTableResult.getMapContent();
            long s2 = System.currentTimeMillis();
            List<MainNewsBean> list = new ArrayList<>();

            for(MainNewsBean bean : batchMain.values()){
                String id = bean.getId();
                List<Organization> orgList = batchOrg.get(id);
                if(!CollectionUtils.isEmpty(orgList)){
                    Iterator<Organization> orgIterator =  orgList.iterator();
                    while (orgIterator.hasNext()){
                        Organization org = orgIterator.next();
                        //获取10位机构编码
                        String itCode2 = CommonService.itCode2Get(itcodeToItcode2Map, trcodeToItcode2Map, org);
                        if (StringUtils.isEmpty(itCode2)){
                            orgIterator.remove();
                            continue;
                        }
                        //公告属性设置
//                        boolean isValid = CommonService.noticeAttributeSet(org.getOrg_risk(), bean, org, itCode2, Constance.notice_main, Constance.INFO_NOTICE_BOND);
//                        if(!isValid){
//                            orgIterator.remove();
//                        }
                    }
                }
                if(!CollectionUtils.isEmpty(orgList)){
                    //更新事件设置
//                    CommonService.updateTimeBuilder(marketDay, bean, type);
                    bean.setList(orgList);
                    Long bondId = bean.getBondId();
                    if(null != bondId){
                        bean.setLinkurl(mapLink.get(bondId));
                        BondContent content = mapContent.get(bondId);
                        if(null != content){
                            bean.setSource(content.getSource());
                            bean.setContent(content.getContent());
                        }
                    }
                    list.add(bean);
                }
            }
            sinkBean.setTotalDuration(System.currentTimeMillis() - s1);
            sinkBean.setQueryDuration(s2 - s1);
            sinkBean.setSize(list.size());
            sinkBean.setMainList(list);
        } catch (Exception e) {

        }
        return sinkBean;
    }

    /**
     * 获取股票公告 link信息
     * @param announmtIDList
     * @return
     */
//    private Map<Long, String> noticeLinkGet(List<Long> announmtIDList){
//        Map<Long, String> map = new HashMap<>();
//        // 从mg查询数据
//        List<JSONObject> list = mongoDBService.findNewsByMongoDB(announmtIDList, "tNW1306", Arrays.asList("NW1306_002", "Flag", "NW1306_005"), "NW1306_002");
//        if (!CollectionUtils.isEmpty(list)) {
//            list.forEach(t -> {
//                int flag = t.getInteger("Flag");
//                if (flag != 1) {
//                    map.put(t.getLong("NW1306_002"), t.getString("NW1306_005"));
//                }
//
//            });
//        }
//        return map;
//    }
}
