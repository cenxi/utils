package hbkjg.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fengxi
 * @className UserReq
 * @description
 * @date 2023年07月13日 14:09
 */

/**
 * {
 *     "userId": "USER1344165823111106562"
 * }
 */
@NoArgsConstructor
@Data
public class UserReq {

    @JsonProperty("userId")
    private String userId;
}
