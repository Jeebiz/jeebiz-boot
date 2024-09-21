/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 业务操作日志信息BO
 */
@ApiModel(value = "BizLogBO", description = "业务操作日志信息BO")
@Getter
@Setter
@ToString
public class BizLogBO {

    /**
     * 功能模块
     */
    @ApiModelProperty(name = "module", value = "功能模块名称")
    private String module;
    /**
     * 业务名称
     */
    @ApiModelProperty(name = "business", value = "业务名称")
    private String business;
    /**
     * 操作类型
     */
    @ApiModelProperty(name = "opt", value = "操作类型")
    private String opt;
    /**
     * 日志级别：（debug:调试、info:信息、warn:警告、error:错误、fetal:严重错误）
     */
    @ApiModelProperty(name = "level", value = "日志级别：（debug:调试、info:信息、warn:警告、error:错误、fetal:严重错误）")
    private String level;
    /**
     * 功能操作请求来源IP
     */
    @ApiModelProperty(name = "addr", value = " 功能操作请求来源IP")
    private String addr;
    /**
     * 功能操作请求来源IP地址所在地
     */
    @ApiModelProperty(name = "location", value = "功能操作请求来源IP地址所在地")
    private String location;
    /**
     * 操作描述
     */
    @ApiModelProperty(name = "msg", value = "操作描述")
    private String msg;
    /**
     * 功能操作异常信息
     */
    @ApiModelProperty(name = "exception", value = "功能操作异常信息")
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
     * 操作人ID
     */
    @ApiModelProperty(name = "userId", value = "操作人ID")
    private String userId;
    /**
     * 操作人名称
     */
    @ApiModelProperty(name = "userName", value = "操作人名称")
    private String userName;
    /**
     * 功能操作发生时间
     */
    @ApiModelProperty(name = "userName", value = "功能操作发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}
