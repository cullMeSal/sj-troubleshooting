package sj.sj_troubleshooting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserQueryResultDTO implements Serializable {
    private static final long serialVersionUID = 124L;
    private Long id;
    private String username;
    private String email;

    private Integer pageNumber;
    private Integer pageSize;
    private Integer totalRecord;

    public UserQueryResultDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    public UserQueryResultDTO(Integer pageNumber, Integer pageSize, Integer totalRecord) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalRecord = totalRecord;
    }
}