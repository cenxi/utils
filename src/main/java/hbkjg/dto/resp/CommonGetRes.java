package hbkjg.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fengxi
 * @className CommonGetRes
 * @description
 * @date 2023年07月13日 14:40
 */
@NoArgsConstructor
@Data
public class CommonGetRes<T> {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("exInfo")
    private Object exInfo;

    @JsonProperty("result")
    private T result;

    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("traceId")
    private Object traceId;

}
