package hbkjg.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author fengxi
 * @className ContactInfo
 * @description
 * @date 2023年07月13日 15:10
 */
@Data
public class ContactInfo {
    /**
     * 身份证号
     */
    @JsonProperty("cardId")
    private String cardId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("phone")
    private String phone;

}
