/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dto;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public abstract class AbstractOrderedPaginationDTO extends AbstractPaginationDTO {

    /**
     * 排序信息
     */
    @ApiModelProperty(notes = "排序信息")
    private List<OrderItem> orders;

}
