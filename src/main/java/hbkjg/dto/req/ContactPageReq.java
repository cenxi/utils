package hbkjg.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fengxi
 * @className ContactReq
 * @description
 * @date 2023年07月13日 14:29
 */

/**
 * {
 *     "userId": "USER1344165823111106562",
 *     "page": 1,
 *     "pageSize": 100
 * }
 */
@NoArgsConstructor
@Data
public class ContactPageReq {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("page")
    private Integer page = 1;

    @JsonProperty("pageSize")
    private Integer pageSize = 100;
}
