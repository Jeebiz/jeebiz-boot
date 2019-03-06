package net.jeebiz.boot.authz.rbac0.web.vo;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AuthzUserDetailVo", description = "用户详细信息参数Vo")
public class AuthzUserDetailVo {

	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "id", dataType = "String", notes = "用户ID")
	private String id;
	/**
	 * 用户别名（昵称）
	 */
	@ApiModelProperty(value = "alias", required = true, dataType = "String", notes = "用户昵称")
	private String alias;
	/**
	 * 用户名
	 */
	@ApiModelProperty(value = "username", required = true, dataType = "String", notes = "用户名")
	@NotBlank(message = "用户名必填")
	private String username;
	/**
	 * 用户密码
	 */
	private String password;
	/**
	 * 用户密码盐：用于密码加解密
	 */
	private String salt;
	/**
	 * 用户秘钥：用于用户JWT加解密
	 */
	private String secret;
	/**
	 * 用户头像：图片路径或图标样式
	 */
	private String avatar;
	/**
	 * 手机号码
	 */
	@ApiModelProperty(value = "phone", required = true, dataType = "String", notes = "手机号码")
	@NotBlank(message = "手机号码必填")
	private String phone;
	/**
	 * 电子邮箱
	 */
	@ApiModelProperty(value = "email", required = true, dataType = "String", notes = "电子邮箱")
	@NotBlank(message = "电子邮箱必填")
	private String email;
	/**
	 * 用户备注
	 */
	private String remark;
	/**
	 * 用户状态(0:不可用|1:正常|2:锁定)
	 */
	private String status;
	/**
	 * 初始化时间
	 */
	private String time24;
	/**
	 * 用户详情ID
	 */
	private String dId;
	/**
	 * 年龄
	 */
	private String age;
	/**
	 * 出生日期
	 */
	private String birthday;
	/**
	 * 用户编码
	 */
	private String code;
	/**
	 * 性别：（male：男，female：女）
	 */
	private String gender;
	/**
	 * 身份证号码
	 */
	private String idcard;
	/**
	 * 角色ID（可能多个组合，如：1,2）
	 */
	private String roleId;
	/**
	 * 角色名称（可能多个组合，如：角色1,角色2）
	 */
	private String roleName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTime24() {
		return time24;
	}

	public void setTime24(String time24) {
		this.time24 = time24;
	}
	
	public String getdId() {
		return dId;
	}

	public void setdId(String dId) {
		this.dId = dId;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
