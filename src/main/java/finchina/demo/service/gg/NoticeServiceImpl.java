package finchina.demo.service.gg;

import cn.hutool.core.thread.GlobalThreadPool;
import finchina.demo.dto.BondContent;
import finchina.demo.dto.ChildTableResult;
import finchina.demo.dto.CommonBean;
import finchina.demo.mapper.gg.NoticeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {


    @Resource
    private NoticeMapper noticeMapper;



    /**
     * 获取债券公告正文
     *
     * @param bondIdList 债券ID
     * @return 列表
     */
    public Map<Long, BondContent> selectBondContent(List<Long> bondIdList) {
        long s1 = System.currentTimeMillis();
        Map<Long, BondContent> map = new HashMap<>();
        List<CommonBean> list = noticeMapper.select1312(bondIdList);
        log.info("获取债券公告正文查询耗时：{} ms", System.currentTimeMillis() - s1);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(t -> {
                BondContent con = new BondContent();
                con.setContent(t.getValue2());
                con.setSource(t.getValue());
                map.put(t.getKeyV(), con);
            });
        }
        return map;
    }

    /**
     * 债券公告链接获取
     */
    public Map<Long, String> selectBondLinkUrl(List<Long> idList) {
        long s1 = System.currentTimeMillis();
        List<CommonBean> list = noticeMapper.select1306Bond(idList);
        log.info("债券公告链接获取耗时：{} ms", System.currentTimeMillis() - s1);
        Map<Long, String> map = new HashMap<>();
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(t -> {
                map.put(t.getKeyV(), t.getValue());
            });
        }
        return map;
    }

    public ChildTableResult childTableResultGet(List<Long> idList) {
        ChildTableResult result = new ChildTableResult();
        //股票公告来源获取
        CompletableFuture<Map<Long, BondContent>> future1312 = CompletableFuture.supplyAsync(()-> selectBondContent(idList), GlobalThreadPool.getExecutor()).exceptionally(t->{
            throw new IllegalArgumentException("股票公告来源获取异常，异常信息：" + t.getMessage() + "，查询数据：" + idList);
        });
        //股票公告链接
        CompletableFuture<Map<Long, String>> future1306Bond= CompletableFuture.supplyAsync(()-> selectBondLinkUrl(idList), GlobalThreadPool.getExecutor()).exceptionally(t->{
            throw new IllegalArgumentException("股票公告链接获取异常，异常信息：" + t.getMessage() + "，查询数据：" + idList);
        });

        try {
            CompletableFuture.allOf(future1312, future1306Bond).get();
            result.setMapLink(future1306Bond.get());
            result.setMapContent(future1312.get());
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return result;
    }
}
