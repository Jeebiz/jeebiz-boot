/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.sample.exception;

import io.hiwepy.boot.autoconfigure.webmvc.ExceptinHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.context.NestedMessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class BizExceptionHandler extends ExceptinHandler {

	@Autowired
	protected NestedMessageSource messageSource;

	/**
	 * 500 (降级熔断)
	@ExceptionHandler({ ClientException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> netflixClientException(ClientException ex) {
		this.logException(ex);
		return new ResponseEntity<>( BizExceptionCode.SYSTEM_DEPEND_UPGRADING.asResponse(messageSource), HttpStatus.OK);
	}
	 */

}
