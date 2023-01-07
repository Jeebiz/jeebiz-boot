package io.hiwepy.boot.api.message;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.awt.TrayIcon.MessageType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class MessagePayload implements Serializable  {

	/**
	 * 消息类型
	 */
	protected MessageType type;
	/**
	 * 应用Key
	 */
	private String appKey;
	/**
	 * 消息ID
	 */
	protected String uuid;
	/**
	 * 消息头内容
	 */
	protected Map<String, String> header = new HashMap<String, String>();
	
	/**
	 * 消息体内容
	 */
	@ApiModelProperty(value = "body", required = true, dataType = "String", notes = "消息内容")
	@NotBlank(message = "消息内容必填")
	private String body;
	
	protected Map<String, String> bodyMap = new HashMap<String, String>();

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getBodyMap() {
		return bodyMap;
	}

	public void setBodyMap(Map<String, String> bodyMap) {
		this.bodyMap = bodyMap;
	}
	
}
