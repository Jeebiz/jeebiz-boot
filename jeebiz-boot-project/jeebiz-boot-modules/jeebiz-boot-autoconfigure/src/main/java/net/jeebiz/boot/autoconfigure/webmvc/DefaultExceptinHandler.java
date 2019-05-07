/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure.webmvc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import net.jeebiz.boot.api.exception.ErrorResponse;

/**
 * 异常增强，以JSON的形式返回给客服端
 * 异常增强类型：NullPointerException,RunTimeException,ClassCastException,
 * NoSuchMethodException,IOException,IndexOutOfBoundsException
 */
@ControllerAdvice
public class DefaultExceptinHandler extends ExceptinHandler {
	
	/**---------------------HTTP异常----------------------------*/
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ TypeMismatchException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> typeMismatchException(HttpServletRequest request,TypeMismatchException ex) {
		this.logException(ex);
		String message = String.format("参数[%s] 类型不匹配. 类型应该是 [%s].", ex.getPropertyName(), ex.getRequiredType());
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingServletRequestParameterException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
		this.logException(ex);
		String message = String.format("参数 [%s] 不能为空.", ex.getParameterName());
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		this.logException(ex);
		
		StringBuilder sb = new StringBuilder("参数验证失败 [")
			.append(ex.getParameter().getParameterIndex()).append("] in ")
			.append(ex.getParameter().getExecutable().toGenericString());
		if (ex.getBindingResult().getErrorCount() > 1) {
			sb.append(" with ").append(ex.getBindingResult().getErrorCount()).append(" errors");
		}
		sb.append(": ");
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			sb.append("[").append(error).append("] ");
		}
		
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(sb.toString(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({ MissingServletRequestPartException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> missingServletRequestPartException(MissingServletRequestPartException ex) {
		this.logException(ex);
		String message = String.format("请求部分 [%s] 不能为空.", ex.getRequestPartName());
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({ BindException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> bindException(BindException ex) {
		this.logException(ex);
		BindingResult result = ex.getBindingResult();
		if( result.getGlobalErrorCount() > 0) {
			ObjectError error = result.getGlobalError();
			return new ResponseEntity<ErrorResponse>(
					ErrorResponse.error(error.getDefaultMessage()), HttpStatus.OK);
		} else{
			return new ResponseEntity<ErrorResponse>(
					ErrorResponse.error(result.getFieldError().getDefaultMessage()), HttpStatus.OK);
		}
	}
	
	/**
	 * 404 (Not Found)
	 */
	@ExceptionHandler({ NoHandlerFoundException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> noHandlerFoundException(NoHandlerFoundException ex) {
		this.logException(ex);
		String message = String.format("没有找到请求地址 [%s],请求方式 [%s]对应的处理对象.", ex.getRequestURL(), ex.getHttpMethod());
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * 405 (Method Not Allowed)
	 */
	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		this.logException(ex);
		String message = String.format("不支持的请求方法, 仅支持 [%s].", ex.getMessage(), StringUtils.join(ex.getSupportedMethods()));
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message, HttpStatus.METHOD_NOT_ALLOWED), HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	/**
	 * 406 (Not Acceptable) 
	 */
	@ExceptionHandler({ HttpMediaTypeNotAcceptableException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> httpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
		this.logException(ex);
		String[] supportedMediaTypes = new String[ex.getSupportedMediaTypes().size()];
		for (int i = 0; i < ex.getSupportedMediaTypes().size(); i++) {
			MediaType mediaType = ex.getSupportedMediaTypes().get(i);
			supportedMediaTypes[i] = mediaType.toString();
		}
		String message = String.format("不匹配的媒体类型, 仅匹配 [%s].", ex.getMessage(), StringUtils.join(supportedMediaTypes));
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message, HttpStatus.NOT_ACCEPTABLE), HttpStatus.NOT_ACCEPTABLE);
	}
	
	/**
	 * 415 (Unsupported Media Type) 
	 */
	@ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
		this.logException(ex);
		String message = String.format("不支持的媒体类型, 仅支持 [%s].", ex.getMessage(), StringUtils.join(ex.getSupportedMediaTypes()));
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ HttpMessageNotWritableException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> httpMessageNotWritableException(HttpMessageNotWritableException ex) {
		this.logException(ex);
		String message = "服务器遇到错误，无法完成请求。";
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ConversionNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> conversionNotSupportedException(ConversionNotSupportedException ex) {
		this.logException(ex);
		String message = "服务器遇到错误，无法完成请求。";
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**---------------------逻辑异常----------------------------*/
	
	// 运行时异常
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public ResponseEntity<ErrorResponse> runtimeExceptionHandler(RuntimeException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// 空指针异常
	@ExceptionHandler(NullPointerException.class)
	@ResponseBody
	public ResponseEntity<ErrorResponse> nullPointerExceptionHandler(NullPointerException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// 类型转换异常
	@ExceptionHandler(ClassCastException.class)
	@ResponseBody
	public ResponseEntity<ErrorResponse> classCastExceptionHandler(ClassCastException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// IO异常
	@ExceptionHandler(IOException.class)
	@ResponseBody
	public ResponseEntity<ErrorResponse> iOExceptionHandler(IOException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// 未知方法异常
	@ExceptionHandler(NoSuchMethodException.class)
	@ResponseBody
	public ResponseEntity<ErrorResponse> noSuchMethodExceptionHandler(NoSuchMethodException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// 数组越界异常
	@ExceptionHandler(IndexOutOfBoundsException.class)
	@ResponseBody
	public ResponseEntity<ErrorResponse> indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	public ResponseEntity<ErrorResponse> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(UnsupportedEncodingException.class)
	@ResponseBody
	public ResponseEntity<ErrorResponse> unsupportedEncodingExceptionHandler(UnsupportedEncodingException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**---------------------Mybatis 异常----------------------------*/
	
	@ExceptionHandler({ BindingException.class })
	@ResponseBody
	public ResponseEntity<ErrorResponse> bindingException(BindingException ex) {
		this.logException(ex);
		String message = "服务器遇到错误，无法完成请求。";
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
	}
	
	/**---------------------数据库异常----------------------------*/
	
	@ExceptionHandler(SQLSyntaxErrorException.class)
	public ResponseEntity<ErrorResponse> sqlSyntaxErrorException(SQLSyntaxErrorException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error("SQL 语法错误.", ex.getErrorCode()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ErrorResponse> dataAccessException(DataAccessException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getRootCause().getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> sqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error("SQL 语法错误.", ex.getErrorCode()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 全局异常捕捉处理
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> defaultErrorHandler(HttpServletRequest request, Exception ex) throws Exception {
		this.logException(ex);
		return new ResponseEntity<ErrorResponse>(ErrorResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
