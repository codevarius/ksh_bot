package kshv.org.bot.core.services.loader.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "actions")
public class BotActionEntity {

    @JsonProperty(value = "file_id")
    private String fileId;

    @Id
    @JsonProperty(value = "file_unique_id")
    private String file_unique_id;

    @JsonProperty(value = "file_size")
    private Integer fileSize;

    @JsonProperty(value = "file_path")
    private String filePath;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFile_unique_id() {
        return file_unique_id;
    }

    public void setFile_unique_id(String file_unique_id) {
        this.file_unique_id = file_unique_id;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
