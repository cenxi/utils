package hbkjg.dto.resp;

import lombok.Data;

import java.util.List;

/**
 * @author fengxi
 * @className ProductTitle
 * @description
 * @date 2023年07月13日 14:43
 */
@Data
public class ProductTitle {

    String titleName;

    String soldNumText;

    List<ProductRes> productList;
}
