package hbkjg.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author fengxi
 * @className TouristInfo
 * @description
 * @date 2023年07月13日 15:10
 */
@Data
public class TouristInfo {
    /**
     * 身份证号
     */
    @JsonProperty("cardId")
    private String cardId;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("name")
    private String name;
    /**
     * 1-身份证
     */
    @JsonProperty("cardType")
    private String cardType = "1";
}
