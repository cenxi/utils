package hbkjg.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fengxi
 * @className UserResp
 * @description
 * @date 2023年07月13日 14:10
 */

/**
 * {
 * 		"fid": "USER1344165823111106562",
 * 		"fcreateUser": "USER1344165823111106562",
 * 		"fcreateTime": "2020-08-08 11:06:12",
 * 		"fmodifyUser": null,
 * 		"fmodifyTime": null,
 * 		"fregisteType": 1,
 * 		"fcontactCode": "d2dd26a37a304054e422d7755d01443f",
 * 		"oldAccount": "s_PT-o78844__Ab0L_LnG-JbieVzggEQk",
 * 		"active": 1,
 * 		"disableTime": null,
 * 		"disableReason": null,
 * 		"email": null,
 * 		"fname": "B0C749E5421E60DEB41734DEAC019C15",
 * 		"fidNo": "987ECC0E18F2304F3A59E8053047A6D07BAD6EFFC37A274B0545DEA395CB7BE0",
 * 		"fmobile": "FEB04182FA3172F763F375471FB984CF"
 *        }
 */
@NoArgsConstructor
@Data
public class UserResp {


    @JsonProperty("fid")
    private String fid;

    @JsonProperty("fcreateUser")
    private String fcreateUser;

    @JsonProperty("fcreateTime")
    private String fcreateTime;

    @JsonProperty("fmodifyUser")
    private Object fmodifyUser;

    @JsonProperty("fmodifyTime")
    private Object fmodifyTime;

    @JsonProperty("fregisteType")
    private Integer fregisteType;

    @JsonProperty("fcontactCode")
    private String fcontactCode;

    @JsonProperty("oldAccount")
    private String oldAccount;

    @JsonProperty("active")
    private Integer active;

    @JsonProperty("disableTime")
    private Object disableTime;

    @JsonProperty("disableReason")
    private Object disableReason;

    @JsonProperty("email")
    private Object email;

    @JsonProperty("fname")
    private String fname;

    @JsonProperty("fidNo")
    private String fidNo;

    @JsonProperty("fmobile")
    private String fmobile;
}
