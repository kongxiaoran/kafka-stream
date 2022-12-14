package finchina.demo.service;

import cn.hutool.core.thread.GlobalThreadPool;
import com.gw.finchina.old.common.util.StringUtil;
import com.gw.finchina.old.common.util.Transformer;
import finchina.demo.dto.Organization;
import finchina.demo.dto.TransFormResultMap;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: kongxr
 * @Date: 2022-10-20 15:42
 * @Description:
 */
public class CommonService {

    public static TransFormResultMap codeTransForm(List<String> itCodes, List<String> trCodes) {
        TransFormResultMap resultMap = new TransFormResultMap();

//        CompletableFuture<Void> itCodeFuture = CompletableFuture.runAsync(() -> {
//            if (null != itCodes) {
//                Map<String, String> itcodeToItcode2Map = Transformer.transformsForMap("ITCODE8TO10", itCodes);
//                resultMap.setItcodeToItcode2Map(itcodeToItcode2Map);
//            }
//        }, GlobalThreadPool.getExecutor());
//
//        CompletableFuture<Void> trCodeFuture = CompletableFuture.runAsync(() -> {
//            if (null != trCodes) {
//                Map<String, String> trcodeToItcode2Map = Transformer.transformsForMap("TRCODE2ITCODE", trCodes);
//                resultMap.setTrcodeToItcode2Map(trcodeToItcode2Map);
//            }
//        }, GlobalThreadPool.getExecutor());
//
//
//        CompletableFuture.allOf(itCodeFuture, trCodeFuture).join();

        Map<String, String> itcodeToItcode2Map = Transformer.transformsForMap("ITCODE8TO10", itCodes);
        resultMap.setItcodeToItcode2Map(itcodeToItcode2Map);

        Map<String, String> trcodeToItcode2Map = Transformer.transformsForMap("TRCODE2ITCODE", trCodes);
        resultMap.setTrcodeToItcode2Map(trcodeToItcode2Map);

        return resultMap;
    }

    public static String itCode2Get(Map<String, String> itcodeToItcode2Map, Map<String, String> trcodeToItcode2Map, Organization org) {
        String trcode = org.getTrcode();
        String itcode = org.getItcode();
        String itCode2 = "";
        // ???????????????10???????????????
        if (!StringUtil.strIsNullOrEmpty(trcode) && !CollectionUtils.isEmpty(trcodeToItcode2Map)) {
            itCode2 = trcodeToItcode2Map.getOrDefault(trcode, null);
        }
        // ??????8??????10???????????????
        if (!StringUtil.strIsNullOrEmpty(itcode) && !CollectionUtils.isEmpty(itcodeToItcode2Map)) {
            itCode2 = itcodeToItcode2Map.getOrDefault(itcode, null);
        }
        //??????????????????????????????  ???????????????????????????????????????
        if (StringUtil.strIsNullOrEmpty(itCode2)) {
            return null;
        }
        org.setItcode2(itCode2);
        return itCode2;
    }


}
