package net.jeebiz.boot.autoconfigure.webmvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.TypeMismatchException;
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

import net.jeebiz.boot.api.exception.HttpExceptions;
import net.jeebiz.boot.api.utils.ResultUtils;

/**
 * Http异常处理
 */
@ControllerAdvice
public class HttpExceptinHandler extends ExceptinHandler {

	/**---------------------HTTP异常----------------------------*/
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ TypeMismatchException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> typeMismatchException(HttpServletRequest request,TypeMismatchException ex) {
		
		this.logException(ex);
		
		Map<String, Object> detailMap = new HashMap<String, Object>();
		detailMap.put("message", String.format("[%s] type mismatch. The type should be [%s].", ex.getPropertyName(), ex.getRequiredType()));
		
		return HttpExceptions.SC_BAD_REQUEST.toResponseEntity(ex, detailMap);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
		this.logException(ex);
		return HttpExceptions.SC_BAD_REQUEST.toResponseEntity(ex);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingServletRequestParameterException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
		
		this.logException(ex);
		
		Map<String, Object> detailMap = new HashMap<String, Object>();
		detailMap.put("message", String.format("Parameter [%s] Can't be empty.", ex.getParameterName()));
		
		return HttpExceptions.SC_BAD_REQUEST.toResponseEntity(ex, detailMap);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		this.logException(ex);
		return HttpExceptions.SC_BAD_REQUEST.toResponseEntity(ex);
	}
	
	@ExceptionHandler({ MissingServletRequestPartException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> missingServletRequestPartException(MissingServletRequestPartException ex) {
		
		this.logException(ex);
		
		Map<String, Object> detailMap = new HashMap<String, Object>();
		detailMap.put("message", String.format("RequestPart [%s] Can't be empty.", ex.getRequestPartName()));
		
		return HttpExceptions.SC_BAD_REQUEST.toResponseEntity(ex, detailMap);	
	}
	
	/**
	 * 405 (Method Not Allowed)
	 */
	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", HttpStatus.METHOD_NOT_ALLOWED);
		rtMap.put("message", String.format("%s, Support only [%s].", ex.getMessage(), StringUtils.join(ex.getSupportedMethods())));

		this.logException(ex, rtMap);
		
		return new ResponseEntity<Map<String, Object>>(rtMap, HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	/**
	 * 406 (Not Acceptable) 
	 */
	@ExceptionHandler({ HttpMediaTypeNotAcceptableException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> httpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
		ex.printStackTrace();

		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", ex.getMessage());
		
		String[] supportedMediaTypes = new String[ex.getSupportedMediaTypes().size()];
		for (int i = 0; i < ex.getSupportedMediaTypes().size(); i++) {
			MediaType mediaType = ex.getSupportedMediaTypes().get(i);
			supportedMediaTypes[i] = mediaType.toString();
		}
		rtMap.put("message", String.format("%s, Support only [%s].", ex.getMessage(), StringUtils.join(supportedMediaTypes)));
		
		return new ResponseEntity<Map<String, Object>>(rtMap, HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * 415 (Unsupported Media Type) 
	 */
	@ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
		ex.printStackTrace();

		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", STATUS_ERROR);
		rtMap.put("SupportedMediaTypes", ex.getSupportedMediaTypes());
		rtMap.put("message", "HTTP Status 415（不支持的媒体类型）    ->请求的格式不受请求页面的支持。");

		return new ResponseEntity<Map<String, Object>>(rtMap, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
	
	@ExceptionHandler({ NoHandlerFoundException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> noHandlerFoundException(NoHandlerFoundException ex) {
		ex.printStackTrace();

		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", STATUS_ERROR);
		rtMap.put("message", "HTTP Status 415（不支持的媒体类型）    ->请求的格式不受请求页面的支持。");

		return new ResponseEntity<Map<String, Object>>(rtMap, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
	
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ HttpMessageNotWritableException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> httpMessageNotWritableException(HttpMessageNotWritableException ex) {

		ex.printStackTrace();

		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
		rtMap.put("message", "服务器遇到错误，无法完成请求。");

		return new ResponseEntity<Map<String, Object>>(rtMap, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ConversionNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> conversionNotSupportedException(ConversionNotSupportedException ex) {
		ex.printStackTrace();

		Map<String, Object> rtMap = rtMap(HttpStatus.INTERNAL_SERVER_ERROR, ex);
		rtMap.put("message", "SQL 语法错误.");

		/*
		 * ex.getPropertyName() ex.getRequiredType()
		 * 
		 * ex.getErrorCode()
		 * 
		 * Map<String, Object> body = ResultUtils.statusMap(STATUS_ERROR, "缺少必要参数,参数名称为"
		 * + ex.getParameterName());
		 */
		return new ResponseEntity<Map<String, Object>>(rtMap, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	
	protected <T extends PropertyAccessException> Map<String, Object> rtMap(HttpStatus status, T ex) {
		Map<String, Object> rtMap = new HashMap<String, Object>();

		Map<String, Object> exMap = new HashMap<String, Object>(2);

		exMap.put("code", status.toString());
		exMap.put("reason", ex.getErrorCode());
		rtMap.put("status", STATUS_ERROR);
		rtMap.put("message", status.getReasonPhrase());
		rtMap.put("exception", exMap);

		return rtMap;
	}
	
	/**---------------------验证异常----------------------------*/
	
	@ExceptionHandler({ BindingException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> bindingException(BindingException ex) {
		this.logException(ex);
		return new ResponseEntity<Map<String, Object>>(ResultUtils.statusMap(STATUS_ERROR, ex.getMessage()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ BindException.class })
	@ResponseBody
	public ResponseEntity<Map<String, Object>> bindException(BindException ex) {
		this.logException(ex);
		BindingResult result = ex.getBindingResult();
		if( result.getGlobalErrorCount() > 0) {
			ObjectError error = result.getGlobalError();
			return new ResponseEntity<Map<String, Object>>(
					ResultUtils.statusMap(STATUS_ERROR, error.getDefaultMessage()), HttpStatus.OK);
		} else{
			return new ResponseEntity<Map<String, Object>>(
					ResultUtils.statusMap(STATUS_ERROR, result.getFieldError().getDefaultMessage()), HttpStatus.OK);
		}
	}
	
}
