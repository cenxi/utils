package hbkjg.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fengxi
 * @className ContactResp
 * @description
 * @date 2023年07月13日 14:30
 */
@NoArgsConstructor
@Data
public class ContactPageResp {


    @JsonProperty("pageNum")
    private Integer pageNum;
    @JsonProperty("pageSize")
    private Integer pageSize;
    @JsonProperty("size")
    private Integer size;
    @JsonProperty("startRow")
    private Integer startRow;
    @JsonProperty("endRow")
    private Integer endRow;
    @JsonProperty("total")
    private Integer total;
    @JsonProperty("pages")
    private Integer pages;

    @JsonProperty("list")
    private List<ContactResp> list;
    @JsonProperty("prePage")

    private Integer prePage;
    @JsonProperty("nextPage")
    private Integer nextPage;
    @JsonProperty("isFirstPage")
    private Boolean isFirstPage;
    @JsonProperty("isLastPage")
    private Boolean isLastPage;
    @JsonProperty("hasPreviousPage")
    private Boolean hasPreviousPage;
    @JsonProperty("hasNextPage")
    private Boolean hasNextPage;
    @JsonProperty("navigatePages")
    private Integer navigatePages;
    @JsonProperty("navigatepageNums")
    private List<Integer> navigatepageNums;
    @JsonProperty("navigateFirstPage")
    private Integer navigateFirstPage;
    @JsonProperty("navigateLastPage")
    private Integer navigateLastPage;
    @JsonProperty("firstPage")
    private Integer firstPage;
    @JsonProperty("lastPage")
    private Integer lastPage;

}
