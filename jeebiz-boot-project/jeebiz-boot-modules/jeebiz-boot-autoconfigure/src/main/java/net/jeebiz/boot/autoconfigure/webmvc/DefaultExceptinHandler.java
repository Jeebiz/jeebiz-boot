package net.jeebiz.boot.autoconfigure.webmvc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.jeebiz.boot.api.exception.HttpExceptions;
import net.jeebiz.boot.api.exception.NormalExceptions;
import net.jeebiz.boot.api.utils.ResultUtils;

/**
 * 异常增强，以JSON的形式返回给客服端
 * 异常增强类型：NullPointerException,RunTimeException,ClassCastException,
 * NoSuchMethodException,IOException,IndexOutOfBoundsException
 */
@ControllerAdvice
public class DefaultExceptinHandler extends ExceptinHandler {
	
	/**---------------------逻辑异常----------------------------*/
	
	// 运行时异常
	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> runtimeExceptionHandler(RuntimeException ex) {
		this.logException(ex);
		return NormalExceptions.SC_RUNTIME_EXCEPTION.toResponseEntity(ex);
	}
	
	// 空指针异常
	@ExceptionHandler(NullPointerException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> nullPointerExceptionHandler(NullPointerException ex) {
		this.logException(ex);
		return NormalExceptions.SC_NULL_POINTER_EXCEPTION.toResponseEntity(ex);
	}

	// 类型转换异常
	@ExceptionHandler(ClassCastException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> classCastExceptionHandler(ClassCastException ex) {
		this.logException(ex);
		return NormalExceptions.SC_CLASS_CAST_EXCEPTION.toResponseEntity(ex);
	}

	// IO异常
	@ExceptionHandler(IOException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> iOExceptionHandler(IOException ex) {
		this.logException(ex);
		return NormalExceptions.SC_IO_EXCEPTION.toResponseEntity(ex);
	}

	// 未知方法异常
	@ExceptionHandler(NoSuchMethodException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> noSuchMethodExceptionHandler(NoSuchMethodException ex) {
		this.logException(ex);
		return NormalExceptions.SC_NO_SUCH_METHOD_EXCEPTION.toResponseEntity(ex);
	}

	// 数组越界异常
	@ExceptionHandler(IndexOutOfBoundsException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException ex) {
		this.logException(ex);
		return NormalExceptions.SC_INDEX_OUT_OF_BOUNDS_EXCEPTION.toResponseEntity(ex);
	}

	/**---------------------数据库异常----------------------------*/
	
	@ExceptionHandler(SQLSyntaxErrorException.class)
	public ResponseEntity<Map<String, Object>> sqlSyntaxErrorException(SQLSyntaxErrorException ex) {
		this.logException(ex);
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", STATUS_ERROR);
		rtMap.put("code", String.valueOf(ex.getErrorCode()));
		rtMap.put("state ", ex.getSQLState());
		rtMap.put("message", "SQL 语法错误.");
		return new ResponseEntity<Map<String, Object>>(rtMap, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<Map<String, Object>> dataAccessException(DataAccessException ex) {
		ex.printStackTrace();
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", STATUS_ERROR);
		rtMap.put("message", ex.getRootCause().getMessage());
		return new ResponseEntity<Map<String, Object>>(rtMap, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<Map<String, Object>> sqlIntegrityConstraintViolationException(SQLSyntaxErrorException ex) {
		ex.printStackTrace();
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", STATUS_ERROR);
		rtMap.put("code", String.valueOf(ex.getErrorCode()));
		rtMap.put("state ", ex.getSQLState());
		rtMap.put("message", "SQL 语法错误.");
		return new ResponseEntity<Map<String, Object>>(rtMap, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({ IllegalArgumentException.class, UnsupportedEncodingException.class })
	public ResponseEntity<Map<String, Object>> jwtTokenErr() {
		
		return new ResponseEntity<Map<String, Object>>(ResultUtils.statusMap(STATUS_ERROR, ""),
				HttpStatus.BAD_REQUEST);
		
		//return new JsonResult(ResultCode.UNKNOWN_ERROR, ResultCode.UNKNOWN_ERROR.msg());
	}
	
	/**
	 * 全局异常捕捉处理
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = Exception.class)
	public Object defaultErrorHandler(HttpServletRequest request, Exception ex) throws Exception {
		this.logException(ex);
		if(isAjaxRequest(request)) {
			return HttpExceptions.SC_INTERNAL_SERVER_ERROR.toResponseEntity(ex);
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("exception", ex);
		mav.addObject("url", request.getRequestURL());
		mav.setViewName("html/def/error");
		return mav;
	}
	
}
