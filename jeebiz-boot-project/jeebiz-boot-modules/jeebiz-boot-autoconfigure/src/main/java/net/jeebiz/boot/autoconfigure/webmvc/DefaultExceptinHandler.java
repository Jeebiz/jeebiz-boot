/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure.webmvc;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintDefinitionException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.GroupDefinitionException;
import javax.validation.UnexpectedTypeException;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.datasource.DataSourceException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.plugin.PluginException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.biz.web.multipart.MaxUploadSizePerFileExceededException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingMatrixVariableException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.jeebiz.boot.api.ApiCode;
import net.jeebiz.boot.api.ApiRestResponse;
import net.jeebiz.boot.api.exception.BizCheckedException;
import net.jeebiz.boot.api.exception.BizIOException;
import net.jeebiz.boot.api.exception.BizRuntimeException;

/**
 * 异常增强，以JSON的形式返回给客服端
 * 异常增强类型：NullPointerException,RunTimeException,ClassCastException,
 * NoSuchMethodException,IOException,IndexOutOfBoundsException
 */
@ControllerAdvice
public class DefaultExceptinHandler extends ExceptinHandler {
	
	// --- 4xx Client Error ---
	
	/**
	 * 404 (Not Found)
	 */
	@ExceptionHandler({ NoHandlerFoundException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> noHandlerFoundException(NoHandlerFoundException ex) {
		this.logException(ex);
		String message = String.format("没有找到请求地址 [%s],请求方式 [%s]对应的处理对象.", ex.getRequestURL(), ex.getHttpMethod());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_NOT_FOUND.toResponse(message), HttpStatus.NOT_FOUND);
	}
	
	/**
	 * 405 (Method Not Allowed)
	 */
	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
		this.logException(ex);
		String message = String.format("不支持的请求方法, 仅支持 [%s].", ex.getMessage(), StringUtils.join(ex.getSupportedMethods()));
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_METHOD_NOT_ALLOWED.toResponse(message), HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	/**
	 * 406 (Not Acceptable) 
	 */
	@ExceptionHandler({ HttpMediaTypeNotAcceptableException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> httpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
		this.logException(ex);
		String[] supportedMediaTypes = new String[ex.getSupportedMediaTypes().size()];
		for (int i = 0; i < ex.getSupportedMediaTypes().size(); i++) {
			MediaType mediaType = ex.getSupportedMediaTypes().get(i);
			supportedMediaTypes[i] = mediaType.toString();
		}
		String message = String.format("不匹配的媒体类型, 仅匹配 [%s].", ex.getMessage(), StringUtils.join(supportedMediaTypes));
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_NOT_ACCEPTABLE.toResponse(message), HttpStatus.NOT_ACCEPTABLE);
	}

	/**
	 * 415 (Unsupported Media Type) 
	 */
	@ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
		this.logException(ex);
		String message = String.format("不支持的媒体类型, 仅支持 [%s].", ex.getMessage(), StringUtils.join(ex.getSupportedMediaTypes()));
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_UNSUPPORTED_MEDIA_TYPE.toResponse(message), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
	
	/**
	 * 400 (Bad Request)
	 * https://www.jianshu.com/p/4df0cac308dc
	 */
	@ExceptionHandler({ MissingMatrixVariableException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> missingMatrixVariableException(MissingMatrixVariableException ex) {
		this.logException(ex);
		String message = String.format("缺少矩阵变量: [%s].", ex.getVariableName());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_MISSING_MATRIX_VARIABLE.toResponse(message), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingPathVariableException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> missingPathVariableException(MissingPathVariableException ex) {
		this.logException(ex);
		String message = String.format("缺少URI模板变量: [%s].", ex.getVariableName());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_MISSING_PATH_VARIABLE.toResponse(message), HttpStatus.BAD_REQUEST);
	}  
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingRequestCookieException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> missingRequestCookieException(MissingRequestCookieException ex) {
		this.logException(ex);
		String message = String.format("缺少Cookie变量: [%s].", ex.getCookieName());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_MISSING_REQUEST_COOKIE.toResponse(message), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingRequestHeaderException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> missingRequestHeaderException(MissingRequestHeaderException ex) {
		this.logException(ex);
		String message = String.format("缺少请求头: [%s].", ex.getHeaderName());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_MISSING_REQUEST_HEADER.toResponse(message), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingServletRequestParameterException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
		this.logException(ex);
		String message = String.format("缺少参数: [%s]，类型为 [%s].", ex.getParameterName(), ex.getParameterType());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_MISSING_REQUEST_PARAM.toResponse(message), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingServletRequestPartException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> missingServletRequestPartException(MissingServletRequestPartException ex) {
		this.logException(ex);
		String message = String.format("缺少请求对象: [%s].", ex.getRequestPartName());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_MISSING_REQUEST_PART.toResponse(message), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ UnsatisfiedServletRequestParameterException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> unsatisfiedServletRequestParameterException(UnsatisfiedServletRequestParameterException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_UNSATISFIED_PARAM.toResponse(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ ServletRequestBindingException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> servletRequestBindingException(ServletRequestBindingException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_BINDING_ERROR.toResponse(), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ JsonParseException.class, JsonProcessingException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> jsonProcessingException(JsonProcessingException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_PARSING_ERROR.toResponse(), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_PARSING_ERROR.toResponse(), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ ConstraintViolationException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> constraintViolationException(ConstraintViolationException ex) {
		this.logException(ex);
		
		Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations(); 
		Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator(); 
		List<String> msgList = new ArrayList<>(); 
		while (iterator.hasNext()) {
			ConstraintViolation<?> cvl = iterator.next(); 
			msgList.add(StringUtils.defaultString(cvl.getMessage(), cvl.getMessageTemplate())); 
		}
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_METHOD_ARGUMENT_NOT_VALID.toResponse(msgList), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		this.logException(ex);
		
		BindingResult result = ex.getBindingResult();
		if( result.getErrorCount() > 0) {
			
			List<Map<String,String>> errorList = Lists.newArrayList();
			for (FieldError error : result.getFieldErrors()) {
				Map<String,String> errorMap = Maps.newHashMap();
				errorMap.put("field", error.getField());
				errorMap.put("msg", error.getDefaultMessage());
				LOG.error(error.getField() + ":"+ error.getDefaultMessage());
				errorList.add(errorMap);
			}
			
			return new ResponseEntity<ApiRestResponse>(ApiCode.SC_METHOD_ARGUMENT_NOT_VALID.toResponse(errorList), HttpStatus.BAD_REQUEST);
			
		} else{
			
			ObjectError error = result.getGlobalError();
			return new ResponseEntity<ApiRestResponse>(ApiCode.SC_METHOD_ARGUMENT_NOT_VALID.toResponse(error.getDefaultMessage()) , HttpStatus.BAD_REQUEST);
			
		}
		
	}
	
	/**
	 * 400 (Bad Request)
     * @see javax.validation.Valid
     * @see org.springframework.validation.Validator
     * @see org.springframework.validation.DataBinder
     */
	@ExceptionHandler({ BindException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> bindException(BindException ex) {
		this.logException(ex);
		
		BindingResult result = ex.getBindingResult();
		if( result.getErrorCount() > 0) {
			
			List<Map<String,String>> errorList = Lists.newArrayList();
			for (FieldError error : result.getFieldErrors()) {
				Map<String,String> errorMap = Maps.newHashMap();
				errorMap.put("field", error.getField());
				errorMap.put("msg", error.getDefaultMessage());
				LOG.error(error.getField() + ":"+ error.getDefaultMessage());
				errorList.add(errorMap);
			}
			
			return new ResponseEntity<ApiRestResponse>(ApiCode.SC_METHOD_ARGUMENT_NOT_VALID.toResponse(errorList), HttpStatus.BAD_REQUEST);
			
		} else{
			
			ObjectError error = result.getGlobalError();
			return new ResponseEntity<ApiRestResponse>(ApiCode.SC_METHOD_ARGUMENT_NOT_VALID.toResponse(error.getDefaultMessage()) , HttpStatus.BAD_REQUEST);
			
			
		}
		
	}

	// --- 5xx Server Error ---
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ConstraintDeclarationException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> constraintDeclarationException(ConstraintDeclarationException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("约束声明不合法"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ConstraintDefinitionException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> constraintDefinitionException(ConstraintDefinitionException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("约束定义不合法"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ GroupDefinitionException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> groupDefinitionException(GroupDefinitionException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("约束组定义不合法"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ UnexpectedTypeException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> unexpectedTypeException(UnexpectedTypeException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("参数校验异常：参数指定了错误的约束验证器"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ValidationException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> validationException(ValidationException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("参数校验异常"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ TypeMismatchException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> typeMismatchException(TypeMismatchException ex) {
		this.logException(ex);
		String message = String.format("Bean 属性 [%s]类型不匹配. 类型应该是 [%s].", ex.getPropertyName(), ex.getRequiredType());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(message), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({MethodArgumentTypeMismatchException.class})
	@ResponseBody
	public ResponseEntity<ApiRestResponse> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		this.logException(ex);
		String message = String.format("参数类型不匹配，参数[%s]类型应该是 [%s].", ex.getName(), ex.getRequiredType().getName());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(message), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ MethodArgumentConversionNotSupportedException.class})
	@ResponseBody
	public ResponseEntity<ApiRestResponse> methodArgumentConversionNotSupportedException(MethodArgumentConversionNotSupportedException ex) {
		this.logException(ex);
		String message = String.format("参数类型转换不支持，参数[%s]类型应该是 [%s].", ex.getName(), ex.getRequiredType().getName());
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(message), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ConversionNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> conversionNotSupportedException(ConversionNotSupportedException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({HttpMessageConversionException.class, HttpMessageNotWritableException.class})
	@ResponseBody
	public ResponseEntity<ApiRestResponse> httpMessageConversionException(HttpMessageConversionException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> runtimeException(RuntimeException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_RUNTIME_EXCEPTION.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(NullPointerException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> nullPointerException(NullPointerException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_NULL_POINTER_EXCEPTION.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(ClassCastException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> classCastException(ClassCastException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_CLASS_CAST_EXCEPTION.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(IOException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> iOException(IOException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_IO_EXCEPTION.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(NoSuchMethodException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> noSuchMethodException(NoSuchMethodException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_NO_SUCH_METHOD_EXCEPTION.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(IndexOutOfBoundsException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> indexOutOfBoundsException(IndexOutOfBoundsException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INDEX_OUT_OF_BOUNDS_EXCEPTION.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> illegalArgumentException(IllegalArgumentException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_ILLEGAL_ARGUMENT_EXCEPTION.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> maxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_ILLEGAL_ARGUMENT_EXCEPTION.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(MaxUploadSizePerFileExceededException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> maxUploadSizePerFileExceededException(MaxUploadSizePerFileExceededException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_ILLEGAL_ARGUMENT_EXCEPTION.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**---------------------业务异常----------------------------*/
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(BizRuntimeException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> bizRuntimeException(BizRuntimeException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(BizCheckedException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> bizCheckedException(BizCheckedException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(BizIOException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse> bizIOException(BizIOException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**---------------------Mybatis 异常----------------------------*/
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ BindingException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> mybatisBindingException(BindingException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("MyBatis:绑定异常"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ CacheException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> mybatisCacheException(CacheException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("MyBatis:缓存异常"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ DataSourceException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> mybatisDataSourceException(DataSourceException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("MyBatis:数据源异常"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ PluginException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> mybatisPluginException(PluginException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("MyBatis:插件异常"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ResultMapException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> mybatisResultMapException(ResultMapException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("MyBatis:结果集异常"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ TooManyResultsException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> mybatisTooManyResultsException(TooManyResultsException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("MyBatis:结果集异常,返回了多条数据"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ PersistenceException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse> mybatisPersistenceException(PersistenceException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("MyBatis:内部异常"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**---------------------数据库异常----------------------------*/
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(SQLSyntaxErrorException.class)
	public ResponseEntity<ApiRestResponse> sqlSyntaxErrorException(SQLSyntaxErrorException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("SQL 语法错误."), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ApiRestResponse> dataAccessException(DataAccessException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("数据源访问异常"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<ApiRestResponse> sqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiRestResponse.error("SQL 语法错误."), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 全局异常捕捉处理
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiRestResponse> defaultErrorHandler(HttpServletRequest request, Exception ex) throws Exception {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}