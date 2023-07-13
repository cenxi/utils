package hbkjg.dto.req;

/**
 * @author fengxi
 * @className OrderReq
 * @description
 * @date 2023年07月13日 15:05
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * {
 *     "payPrice": 0,
 *     "targetTime": "2023-07-14",
 *     "productId": "2267",
 *     "touristInfo": [
 *         {
 *             "cardId": "420204199209254913",
 *             "phone": "18971682819",
 *             "name": "冯曦",
 *             "cardType": "1"
 *         }
 *     ],
 *     "contactInfo": {
 *         "cardId": "420204199209254913",
 *         "name": "冯曦",
 *         "phone": "18971682819"
 *     },
 *     "quantity": 1,
 *     "type": 1
 * }
 */
@NoArgsConstructor
@Data
public class OrderReq {

    /**
     * 金额
     */
    @JsonProperty("payPrice")
    private Integer payPrice = 0;

    /**
     * 入馆时间
     * 格式:2023-07-14
     */
    @JsonProperty("targetTime")
    private String targetTime;

    /**
     * 上午-2266
     * 下午-2267
     * 通过产品列表接口查询id值
     */
    @JsonProperty("productId")
    private String productId;

    @JsonProperty("touristInfo")
    private List<TouristInfo> touristInfo;

    @JsonProperty("contactInfo")
    private ContactInfo contactInfo;

    /**
     * 订票数量和touristInfo.size保持一致
     * 单次订票数量不超过5个
     */
    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("type")
    private Integer type=1;

}
