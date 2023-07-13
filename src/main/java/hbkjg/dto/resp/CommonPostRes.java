package hbkjg.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fengxi
 * @className CommonRes
 * @description
 * @date 2023年07月13日 14:10
 */

/**
 * {
 * 	"code": "202000000",
 * 	"msg": "success",
 * 	"data": {
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
 * }
 * @param <T>
 */
@NoArgsConstructor
@Data
public class CommonPostRes<T> {


    @JsonProperty("code")
    private String code;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("data")
    private T data;

}
