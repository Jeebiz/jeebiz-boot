/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.autoconfigure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(SequenceProperties.PREFIX)
@Data
public class SequenceProperties {

	public static final String PREFIX = "sequence";

	/**
	 * 数据中心ID,数据范围为0~255
	 */
	private Long dataCenterId;
	
	/**
	 * 工作机器ID,数据范围为0~3
	 */
	private Long workerId;

	/**
	 * true表示解决高并发下获取时间戳的性能问题
	 */
	private boolean clock;

	/**
	 * 允许时间回拨的毫秒量,建议5ms
	 */
	private Long timeOffset = 5L;

	/**
	 * true表示使用毫秒内的随机序列(超过范围则取余)
	 */
	private boolean randomSequence;
 

}
