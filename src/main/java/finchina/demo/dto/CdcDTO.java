package finchina.demo.dto;

/**
 * @Author: kongxr
 * @Date: 2022-08-01 8:23
 * @Description:
 */
public class CdcDTO {

    private Long id;

    private String topic;

    private String value;

    private String op;

    private String data;

    private String time;

    public CdcDTO() {
    }

    public CdcDTO(String topic, String value, String op, String data, String time) {
        this.topic = topic;
        this.value = value;
        this.op = op;
        this.data = data;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
