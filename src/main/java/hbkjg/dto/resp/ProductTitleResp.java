package hbkjg.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * @author fengxi
 * @className ProductTitleResp
 * @description
 * @date 2023年07月13日 14:44
 */
@Data
public class ProductTitleResp {

    private List<ProductTitle> titleList;

    private Integer blockMark;

    private Integer todayShowStatus;

    private Integer tomorrowShowStatus;
}
