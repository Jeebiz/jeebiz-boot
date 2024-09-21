package io.hiwepy.boot.api.param;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;

public abstract class BaseQueryParam {

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

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    /**
     * 搜索关键字
     */
    @ApiModelProperty(value = "搜索关键字")
    private String keywords;

}
