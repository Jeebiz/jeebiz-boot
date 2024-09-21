package io.hiwepy.boot.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

public abstract class BasePaginationQueryParam extends BaseQueryParam {

    /**
     * 当前页码
     */
    @ApiModelProperty(example = "1", value = "当前页码")
    @Min(value = 1, message = "最小页码不能小于1")
    private int pageNo = 1;

    /**
     * 每页记录数
     */
    @ApiModelProperty(example = "15", value = "每页记录数")
    @Min(value = 2, message = "每页至少2条数据")
    private int limit = 15;


    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
