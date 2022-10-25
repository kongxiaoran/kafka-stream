package finchina.demo.mapper.gg;

import finchina.demo.dto.CommonBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: kongxr
 * @Date: 2022-10-24 11:22
 * @Description:
 */
@Mapper
public interface NoticeMapper {

    /**
     * 债券公告子表查询
     *
     * @param idList 公告ID
     * @return 列表
     */
    List<CommonBean> select1312(@Param("idList")List<Long> idList);

    /**
     * 股票公告链接地址查询
     *
     * @param idList 公告ID
     * @return 列表
     */
    List<CommonBean> select1306Bond(@Param("idList") List<Long> idList);


}
