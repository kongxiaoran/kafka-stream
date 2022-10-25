package finchina.demo.service.gg;


import finchina.demo.dto.ChildTableResult;

import java.util.List;

public interface NoticeService {



    ChildTableResult childTableResultGet(List<Long> idList);
}
