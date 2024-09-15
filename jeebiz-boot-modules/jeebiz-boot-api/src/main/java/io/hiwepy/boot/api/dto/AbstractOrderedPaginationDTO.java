/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dto;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain = true)
public abstract class AbstractOrderedPaginationDTO extends AbstractPaginationDTO {

    /**
     * 排序信息
     */
    @ApiModelProperty(notes = "排序信息")
    private List<OrderItem> orders;

}
