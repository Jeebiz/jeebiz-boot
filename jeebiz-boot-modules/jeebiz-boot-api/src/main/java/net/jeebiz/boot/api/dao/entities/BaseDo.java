package net.jeebiz.boot.api.dao.entities;


import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

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

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifyer() {
		return modifyer;
	}

	public void setModifyer(String modifyer) {
		this.modifyer = modifyer;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

}
