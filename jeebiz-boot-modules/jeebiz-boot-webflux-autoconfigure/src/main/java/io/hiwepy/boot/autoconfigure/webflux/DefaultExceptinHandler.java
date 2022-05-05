/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.webflux;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.context.NestedMessageSource;
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
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.hiwepy.boot.api.ApiCode;
import io.hiwepy.boot.api.ApiRestResponse;
import io.hiwepy.boot.api.exception.BizCheckedException;
import io.hiwepy.boot.api.exception.BizIOException;
import io.hiwepy.boot.api.exception.BizRuntimeException;
import io.hiwepy.boot.api.exception.IdempotentException;

/**
 * 异常增强，以JSON的形式返回给客服端
 * 异常增强类型：NullPointerException,RunTimeException,ClassCastException,
 * NoSuchMethodException,IOException,IndexOutOfBoundsException
 */
@ControllerAdvice
public class DefaultExceptinHandler extends ExceptinHandler {

	@Autowired
	private NestedMessageSource messageSource;

	// --- 4xx Client Error ---

	/**
	 * 405 (Method Not Allowed)
	 */
	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> httpRequestMethodNotSupportedException(ServerWebExchange exchange, HttpRequestMethodNotSupportedException ex) {
		this.logException(ex);
		String message = String.format("不支持的请求方法, 仅支持 [%s].", StringUtils.join(ex.getSupportedMethods()));
		ApiRestResponse<String> resp = ApiCode.SC_METHOD_NOT_ALLOWED.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 406 (Not Acceptable)
	 */
	@ExceptionHandler({ HttpMediaTypeNotAcceptableException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> httpMediaTypeNotAcceptableException(ServerWebExchange exchange, HttpMediaTypeNotAcceptableException ex) {
		this.logException(ex);
		String[] supportedMediaTypes = new String[ex.getSupportedMediaTypes().size()];
		for (int i = 0; i < ex.getSupportedMediaTypes().size(); i++) {
			MediaType mediaType = ex.getSupportedMediaTypes().get(i);
			supportedMediaTypes[i] = mediaType.toString();
		}
		String message = String.format("不匹配的媒体类型, 仅匹配 [%s].", StringUtils.join(supportedMediaTypes));
		ApiRestResponse<String> resp = ApiCode.SC_NOT_ACCEPTABLE.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 415 (Unsupported Media Type)
	 */
	@ExceptionHandler({ HttpMediaTypeNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> httpMediaTypeNotSupportedException(ServerWebExchange exchange, HttpMediaTypeNotSupportedException ex) {
		this.logException(ex);
		String message = String.format("不支持的媒体类型, 仅支持 [%s].", StringUtils.join(ex.getSupportedMediaTypes()));
		ApiRestResponse<String> resp = ApiCode.SC_UNSUPPORTED_MEDIA_TYPE.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 * https://www.jianshu.com/p/4df0cac308dc
	 */
	@ExceptionHandler({ MissingMatrixVariableException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> missingMatrixVariableException(ServerWebExchange exchange, MissingMatrixVariableException ex) {
		this.logException(ex);
		String message = String.format("缺少矩阵变量: [%s].", ex.getVariableName());
		ApiRestResponse<String> resp = ApiCode.SC_MISSING_MATRIX_VARIABLE.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingPathVariableException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> missingPathVariableException(ServerWebExchange exchange, MissingPathVariableException ex) {
		this.logException(ex);
		String message = String.format("缺少URI模板变量: [%s].", ex.getVariableName());
		ApiRestResponse<String> resp = ApiCode.SC_MISSING_PATH_VARIABLE.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingRequestCookieException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> missingRequestCookieException(ServerWebExchange exchange, MissingRequestCookieException ex) {
		this.logException(ex);
		String message = String.format("缺少Cookie变量: [%s].", ex.getCookieName());
		ApiRestResponse<String> resp = ApiCode.SC_MISSING_REQUEST_COOKIE.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingRequestHeaderException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> missingRequestHeaderException(ServerWebExchange exchange, MissingRequestHeaderException ex) {
		this.logException(ex);
		String message = String.format("缺少请求头: [%s].", ex.getHeaderName());
		ApiRestResponse<String> resp = ApiCode.SC_MISSING_REQUEST_HEADER.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingServletRequestParameterException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> missingServletRequestParameterException(ServerWebExchange exchange, MissingServletRequestParameterException ex) {
		this.logException(ex);
		String message = String.format("缺少参数: [%s]，类型为 [%s].", ex.getParameterName(), ex.getParameterType());
		ApiRestResponse<String> resp = ApiCode.SC_MISSING_REQUEST_PARAM.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MissingServletRequestPartException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> missingServletRequestPartException(ServerWebExchange exchange, MissingServletRequestPartException ex) {
		this.logException(ex);
		String message = String.format("缺少请求对象: [%s].", ex.getRequestPartName());
		ApiRestResponse<String> resp = ApiCode.SC_MISSING_REQUEST_PART.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ UnsatisfiedServletRequestParameterException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> unsatisfiedServletRequestParameterException(ServerWebExchange exchange, UnsatisfiedServletRequestParameterException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_UNSATISFIED_PARAM.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ ServletRequestBindingException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> servletRequestBindingException(ServerWebExchange exchange, ServletRequestBindingException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_BINDING_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ JsonParseException.class, JsonProcessingException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> jsonProcessingException(ServerWebExchange exchange, JsonProcessingException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_PARSING_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> httpMessageNotReadableException(ServerWebExchange exchange, HttpMessageNotReadableException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_PARSING_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ ConstraintViolationException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<List<String>>> constraintViolationException(ConstraintViolationException ex) {
		this.logException(ex);

		Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
		Iterator<ConstraintViolation<?>> iterator = constraintViolations.iterator();
		List<String> msgList = new ArrayList<>();
		while (iterator.hasNext()) {
			ConstraintViolation<?> cvl = iterator.next();
			msgList.add(StringUtils.defaultString(cvl.getMessage(), cvl.getMessageTemplate()));
		}

		String message = StringUtils.defaultString(ex.getMessage(), ApiCode.SC_METHOD_ARGUMENT_NOT_VALID.getReason());
		ApiRestResponse<List<String>> response = ApiRestResponse.of(ApiCode.SC_METHOD_ARGUMENT_NOT_VALID, message, msgList);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<?>> methodArgumentNotValidException(ServerWebExchange exchange, MethodArgumentNotValidException ex) {
		this.logException(ex);
		return this.bindException(exchange, ex, ex.getBindingResult());
	}

	/**
	 * 400 (Bad Request)
     * @see javax.validation.Valid
     * @see org.springframework.validation.Validator
     * @see org.springframework.validation.DataBinder
     */
	@ExceptionHandler({ BindException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<?>> bindException(ServerWebExchange exchange, BindException ex) {
		this.logException(ex);
		return this.bindException(exchange, ex, ex.getBindingResult());
	}


	@ExceptionHandler(WebExchangeBindException.class)
    @ResponseBody
    public ResponseEntity<ApiRestResponse<?>> webExchangeBindException(ServerWebExchange exchange, WebExchangeBindException ex) {
		this.logException(ex);
		return this.bindException(exchange, ex, ex.getBindingResult());
    }

	protected ResponseEntity<ApiRestResponse<?>> bindException(ServerWebExchange exchange, Exception ex, BindingResult result) {

		if( result.getErrorCount() > 0) {

			List<Map<String,String>> errorList = Lists.newArrayList();
			for (FieldError error : result.getFieldErrors()) {
				Map<String,String> errorMap = Maps.newHashMap();
				errorMap.put("field", error.getField());
				errorMap.put("msg", error.getDefaultMessage());
				LOG.error(error.getField() + ":"+ error.getDefaultMessage());
				errorList.add(errorMap);
			}

			return new ResponseEntity<ApiRestResponse<?>>(ApiCode.SC_METHOD_ARGUMENT_NOT_VALID.toResponse(errorList), HttpStatus.OK);

		} else{

			ObjectError error = result.getGlobalError();

			ApiRestResponse<String> resp = ApiCode.SC_METHOD_ARGUMENT_NOT_VALID.toResponse(this.getLocaleMessage(exchange, ex, error.getDefaultMessage()));
			return new ResponseEntity<ApiRestResponse<?>>(resp, HttpStatus.OK);

		}
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ TypeMismatchException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> typeMismatchException(ServerWebExchange exchange, TypeMismatchException ex) {
		this.logException(ex);
		String message = String.format("Bean 属性 [%s]类型不匹配. 类型应该是 [%s].", ex.getPropertyName(), ex.getRequiredType());
		ApiRestResponse<String> resp = ApiCode.SC_BAD_REQUEST.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({MethodArgumentTypeMismatchException.class})
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> methodArgumentTypeMismatchException(ServerWebExchange exchange, MethodArgumentTypeMismatchException ex) {
		this.logException(ex);
		String message = String.format("参数类型不匹配，参数[%s]类型应该是 [%s].", ex.getName(), ex.getRequiredType().getSimpleName());
		ApiRestResponse<String> resp = ApiCode.SC_BAD_REQUEST.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 400 (Bad Request)
	 */
	@ExceptionHandler({ MethodArgumentConversionNotSupportedException.class})
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> methodArgumentConversionNotSupportedException(ServerWebExchange exchange, MethodArgumentConversionNotSupportedException ex) {
		this.logException(ex);
		String message = String.format("参数类型转换不支持，参数[%s]类型应该是 [%s].", ex.getName(), ex.getRequiredType().getSimpleName());
		ApiRestResponse<String> resp = ApiCode.SC_BAD_REQUEST.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	// --- 5xx Server Error ---

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ConstraintDeclarationException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> constraintDeclarationException(ConstraintDeclarationException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse<String>>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("约束声明不合法"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ConstraintDefinitionException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> constraintDefinitionException(ServerWebExchange exchange, ConstraintDefinitionException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse<String>>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("约束定义不合法"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ GroupDefinitionException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> groupDefinitionException(ServerWebExchange exchange, GroupDefinitionException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse<String>>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("约束组定义不合法"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ UnexpectedTypeException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> unexpectedTypeException(ServerWebExchange exchange, UnexpectedTypeException ex) {
		this.logException(ex);
		return new ResponseEntity<ApiRestResponse<String>>(ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse("参数校验异常：参数指定了错误的约束验证器"), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ValidationException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> validationException(ServerWebExchange exchange, ValidationException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, "参数校验异常"));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ConversionNotSupportedException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> conversionNotSupportedException(ServerWebExchange exchange, ConversionNotSupportedException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({HttpMessageConversionException.class, HttpMessageNotWritableException.class})
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> httpMessageConversionException(ServerWebExchange exchange, HttpMessageConversionException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(NullPointerException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> nullPointerException(ServerWebExchange exchange, NullPointerException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(ClassCastException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> classCastException(ServerWebExchange exchange, ClassCastException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(IOException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> iOException(ServerWebExchange exchange, IOException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(NoSuchMethodException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> noSuchMethodException(ServerWebExchange exchange, NoSuchMethodException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(IndexOutOfBoundsException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> indexOutOfBoundsException(ServerWebExchange exchange, IndexOutOfBoundsException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> illegalArgumentException(ServerWebExchange exchange, IllegalArgumentException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> maxUploadSizeExceededException(ServerWebExchange exchange, MaxUploadSizeExceededException ex) {
		this.logException(ex);
		/*
		StringBuilder error = new StringBuilder();

		long maxUploadSize = ex.getMaxUploadSize();
		String actualSize = String.valueOf(cause.getActualSize());
		double parseDouble = Double.parseDouble(actualSize) / 1024 / 1024;
		BigDecimal b = new BigDecimal(parseDouble);
		double d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		error.append("最大上传文件为:" + ex.getMaxUploadSize() / 1024 / 1024).append("M;");
		error.append("实际文件大小为：").append(d).append("M");
        System.out.println(error.toString());

		error.append("上传文件出错");
		System.out.println(error.toString());
		*/
		String message = String.format("上传文件超过%s字节的最大上传大小（字节）", ex.getMaxUploadSize());
		ApiRestResponse<String> resp = ApiCode.SC_BAD_REQUEST.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(MaxUploadSizePerFileExceededException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> maxUploadSizePerFileExceededException(ServerWebExchange exchange, MaxUploadSizePerFileExceededException ex) {
		this.logException(ex);
		String message = String.format("单个文件超过%s字节的最大上传大小（字节）", ex.getMaxUploadSizePerFile());
		ApiRestResponse<String> resp = ApiCode.SC_BAD_REQUEST.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**---------------------业务异常----------------------------*/

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(BizRuntimeException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> bizRuntimeException(ServerWebExchange exchange, BizRuntimeException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiRestResponse.error(ex.getCode(), this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(BizCheckedException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> bizCheckedException(ServerWebExchange exchange, BizCheckedException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiRestResponse.error(ex.getCode(), this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(BizIOException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> bizIOException(ServerWebExchange exchange, BizIOException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiRestResponse.error(ex.getCode(), this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(IdempotentException.class)
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> idempotentException(ServerWebExchange exchange, IdempotentException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiRestResponse.error(ex.getCode(), this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.OK);
	}

	/**---------------------Mybatis 异常----------------------------*/

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ BindingException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> mybatisBindingException(ServerWebExchange exchange, BindingException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, "MyBatis:绑定异常"));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ CacheException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> mybatisCacheException(ServerWebExchange exchange, CacheException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, "MyBatis:缓存异常"));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ DataSourceException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> mybatisDataSourceException(ServerWebExchange exchange, DataSourceException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, "MyBatis:数据源异常"));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ PluginException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> mybatisPluginException(ServerWebExchange exchange, PluginException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, "MyBatis:插件异常"));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ ResultMapException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> mybatisResultMapException(ServerWebExchange exchange, ResultMapException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, "MyBatis:结果集异常"));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ TooManyResultsException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> mybatisTooManyResultsException(ServerWebExchange exchange, TooManyResultsException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, "MyBatis:结果集异常,返回了多条数据"));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler({ PersistenceException.class })
	@ResponseBody
	public ResponseEntity<ApiRestResponse<String>> mybatisPersistenceException(ServerWebExchange exchange, PersistenceException ex) {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, "MyBatis:内部异常"));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**---------------------JDBC异常----------------------------*/

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ApiRestResponse<String>> dataAccessException(ServerWebExchange exchange, DataAccessException ex) {
		this.logException(ex);
		Throwable cause = ex.getCause();
		//ExceptionUtils.getRootCause(ex.getCause())
		if(cause instanceof SQLSyntaxErrorException) {
			return sqlSyntaxErrorException(exchange, (SQLSyntaxErrorException) ex.getCause());
		} else if(cause instanceof SQLIntegrityConstraintViolationException) {
			return sqlIntegrityConstraintViolationException(exchange, (SQLIntegrityConstraintViolationException) ex.getCause());
		}
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, "数据源访问异常"));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<ApiRestResponse<String>> sqlException(ServerWebExchange exchange, SQLException ex) {
		this.logException(ex);
		String message = String.format("SQL-%s：JDBC异常[%s] [%s].", ex.getSQLState(), ex.getErrorCode(), ex.getMessage());
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(SQLTimeoutException.class)
	public ResponseEntity<ApiRestResponse<String>> sqlTimeoutException(ServerWebExchange exchange, SQLTimeoutException ex) {
		this.logException(ex);
		String message = String.format("SQL-%s：JDBC异常[%s] [%s].", ex.getSQLState(), ex.getErrorCode(), ex.getMessage());
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}


	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(BatchUpdateException.class)
	public ResponseEntity<ApiRestResponse<String>> sqlBatchUpdateException(ServerWebExchange exchange, BatchUpdateException ex) {
		this.logException(ex);
		String message = String.format("SQL-%s：批量更新失败 [%s] [%s].", ex.getSQLState(), ex.getErrorCode(), ex.getMessage());
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(SQLClientInfoException.class)
	public ResponseEntity<ApiRestResponse<String>> sqlClientInfoException(ServerWebExchange exchange, SQLClientInfoException ex) {
		this.logException(ex);
		String message = String.format("SQL-%s：JDBC客户端配置错误 [%s] [%s].", ex.getSQLState(), ex.getErrorCode(), ex.getMessage());
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(SQLSyntaxErrorException.class)
	public ResponseEntity<ApiRestResponse<String>> sqlSyntaxErrorException(ServerWebExchange exchange, SQLSyntaxErrorException ex) {
		this.logException(ex);
		String message = String.format("SQL-%s：SQL 语法错误 [%s] [%s].", ex.getSQLState(), ex.getErrorCode(), ex.getMessage());
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<ApiRestResponse<String>> sqlIntegrityConstraintViolationException(ServerWebExchange exchange, SQLIntegrityConstraintViolationException ex) {
		this.logException(ex);
		String message = String.format("SQL-%s：违反唯一约束条件 [%s] [%s].", ex.getSQLState(), ex.getErrorCode(), ex.getMessage());
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, message));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**---------------------默认全局异常----------------------------*/

	/**
	 * 全局异常捕捉处理
	 * 500 (Internal Server Error)
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiRestResponse<String>> defaultExceptionHandler(ServerWebExchange exchange, Exception ex) throws Exception {
		this.logException(ex);
		ApiRestResponse<String> resp = ApiCode.SC_INTERNAL_SERVER_ERROR.toResponse(this.getLocaleMessage(exchange, ex, ex.getMessage()));
		return new ResponseEntity<ApiRestResponse<String>>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 异常信息国际化
	 * @param ex
	 * @return
	 */
	protected String getLocaleMessage(ServerWebExchange exchange, Exception ex, String message) {

	    String i18nKey = null;
		if(ex instanceof BizCheckedException) {
			BizCheckedException bizEx = (BizCheckedException) ex;
			i18nKey = bizEx.getI18n();
		} else if(ex instanceof BizIOException) {
			BizIOException bizEx = (BizIOException) ex;
			i18nKey = bizEx.getI18n();
		} else if(ex instanceof BizCheckedException) {
			BizCheckedException bizEx = (BizCheckedException) ex;
			i18nKey = bizEx.getI18n();
		} else if(ex instanceof BizRuntimeException) {
			BizRuntimeException bizEx = (BizRuntimeException) ex;
			i18nKey = bizEx.getI18n();
		} else if(ex instanceof IdempotentException) {
			IdempotentException bizEx = (IdempotentException) ex;
			i18nKey = bizEx.getI18n();
		}

		if(StringUtils.isNoneBlank(i18nKey)) {
		    Locale locale = exchange.getLocaleContext().getLocale();
			return getMessageSource().getMessage(i18nKey, null, message, locale) ;
		}
		return message;
	}

	public NestedMessageSource getMessageSource() {
		return messageSource;
	}

}
