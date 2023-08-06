package io.hiwepy.boot.sample.dao.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.hiwepy.boot.api.dao.entities.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * Demo示例表
 * </p>
 *
 * @author wandl
 * @since 2023-08-06
 */
@Data
@Accessors(chain = true)
@TableName("t_demo")
public class DemoEntity extends BaseEntity<DemoEntity> {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 简述
     */
    @TableField("intro")
    private String intro;

    /**
     * 显示顺序
     */
    @TableField("order_by")
    private Integer orderBy;

    /**
     * 状态（0:禁用|1:可用）
     */
    @TableField("`status`")
    private Integer status;


}
