package io.hiwepy.boot.api.dao.entities;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
public class BaseDo extends BaseBean {

    /**
     * 是否删除 0未删除 1已删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;
    /**
     * 创建人id
     */
    @TableField(value = "creator")
    private String creator;
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新人id
     */
    @TableField(value = "modifyer")
    private String modifyer;
    /**
     * 更新时间
     */
    @TableField(value = "modify_time", fill = FieldFill.INSERT_UPDATE)
    private Date modifyTime;

}
