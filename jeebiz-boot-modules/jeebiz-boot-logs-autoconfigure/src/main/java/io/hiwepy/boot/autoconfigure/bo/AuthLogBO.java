/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 认证授权日志信息BO
 */
@ApiModel(value = "AuthzLogBO", description = "认证授权日志信息BO")
@Getter
@Setter
@ToString
public class AuthLogBO {

    /**
     * 认证授权类型（login:登录认证、logout:会话注销）
     */
    @ApiModelProperty(name = "opt", value = "认证授权类型（login:登录认证、logout:会话注销）")
    private String opt;
    /**
     * 认证协议：CAS、HTTP、JWT、KISSO、LDAP、OAuth2、OpenID、SMAL等
     */
    @ApiModelProperty(name = "protocol", value = "认证协议：CAS、HTTP、JWT、KISSO、LDAP、OAuth2、OpenID、SMAL等")
    private String protocol;
    /**
     * 日志级别：（debug:调试、info:信息、warn:警告、error:错误、fetal:严重错误）
     */
    @ApiModelProperty(name = "level", value = "日志级别：（debug:调试、info:信息、warn:警告、error:错误、fetal:严重错误）")
    private String level;
    /**
     * 认证请求来源IP地址
     */
    @ApiModelProperty(name = "addr", value = "认证请求来源IP地址")
    private String addr;
    /**
     * 认证请求来源IP所在地点
     */
    @ApiModelProperty(name = "location", value = "认证请求来源IP所在地点")
    private String location;
    /**
     * 认证授权结果：（fail:失败、success:成功）
     */
    @ApiModelProperty(name = "status", value = "认证授权结果：（fail:失败、success:成功）")
    private String status;
    /**
     * 认证授权请求信息
     */
    @ApiModelProperty(name = "msg", value = "认证授权请求信息")
    private String msg;
    /**
     * 认证授权异常信息
     */
    @ApiModelProperty(name = "exception", value = "认证授权异常信息")
    private String exception;
    /**
     * 设备记录ID
     */
    @ApiModelProperty(name = "deviceId", value = "设备记录ID")
    private String deviceId;
    /**
     * 应用ID
     */
    @ApiModelProperty(name = "appId", value = "应用ID")
    private String appId;
    /**
     * 应用渠道编码
     */
    @ApiModelProperty(name = "appChannel", value = "应用渠道编码")
    private String appChannel;
    /**
     * 应用版本号
     */
    @ApiModelProperty(name = "appVersion", value = "应用版本号")
    private String appVersion;
    /**
     * 认证授权对象ID
     */
    @ApiModelProperty(name = "userId", value = "认证授权对象ID")
    private String userId;
    /**
     * 认证授权对象名称
     */
    @ApiModelProperty(name = "userName", value = "认证授权对象名称")
    private String userName;
    /**
     * 认证发生时间
     */
    @ApiModelProperty(name = "createTime", value = "认证发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}
