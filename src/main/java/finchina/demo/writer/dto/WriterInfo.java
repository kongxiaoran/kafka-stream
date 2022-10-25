package finchina.demo.writer.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WriterInfo implements Serializable {
    private String taskName;
    private String fileName;
    private Boolean isMain;
    private String field;
    private String dataType;

    public WriterInfo() {
    }

    public WriterInfo(String taskName, String fileName, Boolean isMain, String field) {
        this.taskName = taskName;
        this.fileName = fileName;
        this.isMain = isMain;
        this.field = field;
    }

    public WriterInfo(String taskName, String fileName, String dataType) {
        this.taskName = taskName;
        this.fileName = fileName;
        this.dataType = dataType;
    }
}
