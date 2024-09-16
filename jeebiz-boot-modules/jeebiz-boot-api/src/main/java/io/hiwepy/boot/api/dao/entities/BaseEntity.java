/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dao.entities;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@SuppressWarnings("serial")
public class BaseEntity<T> implements Cloneable, Serializable {

    /**
     * 是否删除（0：未删除，1：已删除）
     */
    @TableField(value = "is_deleted")
    @TableLogic
    private Integer isDeleted;
    /**
     * 创建人ID
     */
    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /**
     * 更新人ID
     */
    @TableField(value = "modifyer", fill = FieldFill.UPDATE)
    private String modifyer;
    /**
     * 更新时间
     */
    @TableField(value = "modify_time", fill = FieldFill.UPDATE)
    private LocalDateTime modifyTime;
    /**
     * 开始时间
     */
    @JsonIgnore
    @TableField(exist = false)
    private LocalDateTime beginTime;
    /**
     * 结束时间
     */
    @JsonIgnore
    @TableField(exist = false)
    private LocalDateTime endTime;
    /**
     * 关键词搜索
     */
    @JsonIgnore
    @TableField(exist = false)
    private String keywords;

}
