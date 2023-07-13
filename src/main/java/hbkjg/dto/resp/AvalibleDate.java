package hbkjg.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fengxi
 * @className AvalibleDate
 * @description
 * @date 2023年07月13日 14:57
 */
@NoArgsConstructor
@Data
public class AvalibleDate {

    @JsonProperty("productDate")
    private String productDate;

    @JsonProperty("contractPrice")
    private Integer contractPrice;

    @JsonProperty("retailPrice")
    private Integer retailPrice;

    @JsonProperty("price")
    private Integer price;

    @JsonProperty("stock")
    private Integer stock;

    @JsonProperty("judgmentStock")
    private Integer judgmentStock;

    @JsonProperty("noConfig")
    private Integer noConfig;

    /**
     * 1-不可预约
     */
    @JsonProperty("enableStatus")
    private Integer enableStatus;
}
