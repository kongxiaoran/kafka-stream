package finchina.demo.writer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MongoBulkRequest {
    //更新或者插入
    private List<Pair<Query, Update>> upsertList;
    //删除
    private List<Query> removes;

    public boolean isNotEmpty() {
        return !upsertList.isEmpty() || !removes.isEmpty();
    }
}
