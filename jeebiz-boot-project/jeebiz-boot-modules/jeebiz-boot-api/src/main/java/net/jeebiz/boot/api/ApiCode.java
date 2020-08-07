/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api;

import net.jeebiz.boot.api.utils.Constants;

/**
 * Enumeration of Api Code.
 */
public enum ApiCode {
	
	// --- 4xx Client Error ---
	
    //HTTP Status 4xx(客户端错误，请求包含语法错误或无法完成请求)   ->这些状态代码表示请求可能出错，妨碍了服务器的处理。 

	/**
	 * 异常：HTTP Status 400（错误请求）    -> 服务器不理解请求的语法。</br>
	 * TypeMismatchException                   400 (Bad Request)</br>
	 * HttpMessageNotReadableException         400 (Bad Request)</br>
	 * MissingServletRequestParameterException 400 (Bad Request)</br>
	 */
	SC_BAD_REQUEST("400", Constants.RT_FAIL, "错误请求"),
	/**
	 * HTTP Status 401（未授权）     -> 请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应
	 */
	SC_UNAUTHORIZED("401", Constants.RT_FAIL, "请求要求身份验证"),
	/**
	 * HTTP Status 403（禁止）    -> 服务器理解请求客户端的请求，但是拒绝执行此请求。
	 */
	SC_FORBIDDEN("403",  Constants.RT_FAIL, "请求被拒绝"),
	/**
	 * 异常：HTTP Status 404（未找到）     -> 服务器找不到请求的资源（网页）。通过此代码，网站设计人员可设置"您所请求的资源无法找到"的个性页面
	 * NoSuchRequestHandlingMethodException    404 (Not Found) 
	 * NoHandlerFoundException 404 (Not Found) 
	 */
	SC_NOT_FOUND("404", Constants.RT_FAIL, "请求的资源或接口不存在"),
	/**
	 * 异常：HTTP Status 405（方法禁用）    ->禁用请求中指定的方法。
	 * HttpRequestMethodNotSupportedException  405 (Method Not Allowed)
	 */
	SC_METHOD_NOT_ALLOWED("405", Constants.RT_FAIL, "客户端请求中的方法被禁止"),
	/**
	 * 异常：HTTP Status 406（不接受）     ->服务器无法根据客户端请求的内容特性完成请求。
	 * HttpMediaTypeNotAcceptableException     406 (Not Acceptable)
	 */
	SC_NOT_ACCEPTABLE("406", Constants.RT_FAIL, "服务器无法根据客户端请求的内容特性完成请求"),
	/**
	 * 异常：HTTP Status 407（需要代理授权）     ->此状态代码与 401（未授权）类似，但指定请求者应当授权使用代理。
	 */
	SC_PROXY_AUTHENTICATION_REQUIRED("407", Constants.RT_FAIL, "要求进行代理身份验证"),
	/**
	 * 异常：HTTP Status 408（请求超时）    ->服务器等待客户端发送的请求时间过长，超时。
	 */
	SC_REQUEST_TIMEOUT("408", Constants.RT_FAIL, "服务器等候请求时发生超时"),
	/**
	 * 异常：HTTP Status 409（冲突）     ->服务器在完成请求时发生冲突。 服务器必须在响应中包含有关冲突的信息。
	 */
	SC_CONFLICT("409", Constants.RT_FAIL, "服务器找不到请求的地址"),
	/**
	 * 异常：HTTP Status 410（已删除）    -> 如果请求的资源已永久删除，服务器就会返回此响应。
	 */
	SC_GONE("410", Constants.RT_FAIL, "服务器找不到请求的地址" ),
	/**
	 * 异常：HTTP Status 411（需要有效长度）     ->服务器无法处理客户端发送的不带Content-Length的请求信息。
	 */
	SC_LENGTH_REQUIRED("411", Constants.RT_FAIL, "服务器拒绝接受不带Content-Length请求头的客户端请求"),
	/**
	 * 异常：HTTP Status 412（未满足前提条件）     ->服务器未满足请求者在请求中设置的其中一个前提条件。
	 */
	SC_PRECONDITION_FAILED("412", Constants.RT_FAIL, "客户端请求信息的先决条件错误"),
	/**
	 * 异常：HTTP Status 413（请求实体过大）     ->服务器无法处理请求，因为请求实体过大，超出服务器的处理能力。为防止客户端的连续请求，服务器可能会关闭连接。如果只是服务器暂时无法处理，则会包含一个Retry-After的响应信息。
	 */
	SC_REQUEST_TOO_LONG("413", Constants.RT_FAIL, "服务器无法处理请求，因为请求实体过大，超出服务器的处理能力" ),
	/**
	 * 异常：HTTP Status 415（不支持的媒体类型）    ->请求的格式不受请求页面的支持。
	 * HttpMediaTypeNotSupportedException      415 (Unsupported Media Type)
	 */
	SC_UNSUPPORTED_MEDIA_TYPE("415", Constants.RT_FAIL, "服务器无法处理请求附带的媒体格式"),
	/**
	 * 异常：HTTP Status 416（请求范围不符合要求）     ->如果页面无法提供请求的范围，则服务器会返回此状态代码。
	 */
	SC_REQUESTED_RANGE_NOT_SATISFIABLE("416", Constants.RT_FAIL, "客户端请求的范围无效"),
	/**
	 * 异常：HTTP Status 417（未满足期望值）     ->服务器未满足”Expect”请求标头字段的要求。
	 */
	SC_EXPECTATION_FAILED("417", Constants.RT_FAIL, "服务器无法满足Expect的请求头信息"),
	/**
	 * 异常：HTTP Status 422（无法处理的请求实体） ->请求格式正确，但是由于含有语义错误，无法响应。
	 */
	SC_UNPROCESSABLE_ENTITY("422", Constants.RT_FAIL, "无法处理的请求实体"),
	/**
	 * 异常：HTTP Status 423（当前资源被锁定）
	 */
	SC_LOCKED("423", Constants.RT_FAIL, "当前资源被锁定 "),
	/**
	 * 异常：HTTP Status 424（依赖导致的失败）->由于之前的某个请求发生的错误，导致当前请求失败，例如 PROPPATCH。
	 */
	SC_FAILED_DEPENDENCY("424", Constants.RT_FAIL, "依赖导致的失败"),
	/**
	 * 异常：HTTP Status 424（客户端应当切换到TLS/1.0）
	 */
	SC_UPGRADE_REQUIRED("424", Constants.RT_FAIL, "客户端应当切换到TLS/1.0"),
	/**
	 * 异常：HTTP Status 428（要求先决条件）    -> 先决条件是客户端发送 HTTP 请求时，如果想要请求能成功必须满足一些预设的条件。
	 */
	SC_PRECONDITION_REQUIRED("428", Constants.RT_FAIL, "要求先决条件"),
	/**
	 * 异常：HTTP Status 429（太多请求）    -> 当你需要限制客户端请求某个服务数量时，该状态码就很有用，也就是请求速度限制。
	 */
	SC_TOO_MANY_REQUESTS("429", Constants.RT_FAIL, "太多请求"),
	/**
	 * 异常：HTTP Status 431（请求头字段太大）    -> 某些情况下，客户端发送 HTTP 请求头会变得很大，那么服务器可发送 431 Request Header Fields Too Large 来指明该问题。
	 */
	SC_REQUEST_HEADER_FIELDS_TOO_LARGE("431", Constants.RT_FAIL, "请求头字段太大" ),
	/**
	 * 异常：HTTP Status 451（因法律原因不可用）    ->
	 */
	SC_UNAVAILABLE_FOR_LEGAL_REASONS("451", Constants.RT_FAIL, "该请求因法律原因不可用"),
	
	// --- 5xx Server Error ---
    //HTTP Status 5xx（服务器错误，服务器在处理请求的过程中发生了错误）    -> 这些状态代码表示服务器在尝试处理请求时发生内部错误。 这些错误可能是服务器本身的错误，而不是请求出错。
    
	/**
	 * 异常：HTTP Status 500（服务器内部错误）    ->服务器内部错误，无法完成请求。
	 * ConversionNotSupportedException         500 (Internal Server Error)
	 * HttpMessageNotWritableException         500 (Internal Server Error)
	 */
	SC_INTERNAL_SERVER_ERROR("500", Constants.RT_FAIL, "服务器内部错误，无法完成请求"),
	/**
	 * 异常：HTTP Status 501（尚未实施）     ->服务器不具备完成请求的功能。 例如，服务器无法识别请求方法时可能会返回此代码。
	 */
	SC_NOT_IMPLEMENTED("501", Constants.RT_FAIL, "服务器不支持请求的功能，无法完成请求"),
	/**
	 * 异常：HTTP Status 502（错误网关）     -> 作为网关或者代理工作的服务器尝试执行请求时，从上游服务器接收到无效的响应。
	 */
	SC_BAD_GATEWAY("502", Constants.RT_FAIL, "错误网关"),
	/**
	 * 异常：HTTP Status 503（服务不可用）    -> 由于临时的服务器维护或者过载，服务器当前无法处理请求。这个状况是临时的，并且将在一段时间以后恢复。如果能够预计延迟时间，那么响应中可以包含一个 Retry-After 头用以标明这个延迟时间。如果没有给出这个 Retry-After 信息，那么客户端应当以处理500响应的方式处理它。
	 * 									注意：503状态码的存在并不意味着服务器在过载的时候必须使用它。某些服务器只不过是希望拒绝客户端的连接。
	 */
	SC_SERVICE_UNAVAILABLE("503", Constants.RT_FAIL, "服务器目前无法使用（由于超载或停机维护）"),
	/**
	 * 异常：HTTP Status 504（网关访问超时）     -> 作为网关或者代理工作的服务器尝试执行请求时，未能及时从上游服务器（URI标识出的服务器，例如HTTP、FTP、LDAP）或者辅助服务器（例如DNS）收到响应。
　　	 *								        注意：某些代理服务器在DNS查询超时时会返回400或者500错误
	 */
	SC_GATEWAY_TIMEOUT("504", Constants.RT_FAIL, "网关访问超时"),
	/**
	 * 异常：HTTP Status 505（HTTP 版本不受支持）    -> 服务器不支持请求的HTTP协议的版本，无法完成处理。这暗示着服务器不能或不愿使用与客户端相同的版本。响应中应当包含一个描述了为何版本不被支持以及服务器支持哪些协议的实体。
	 */
	SC_HTTP_VERSION_NOT_SUPPORTED("505", Constants.RT_FAIL, "HTTP 版本不受支持"),
	/**
	 * 异常：HTTP Status 506（服务器内部配置错误）->  由《透明内容协商协议》（RFC 2295）扩展，代表服务器存在内部配置错误：被请求的协商变元资源被配置为在透明内容协商中使用自己，因此在一个协商处理中不是一个合适的重点。
	 */
	SC_VARIANT_ALSO_NEGOTIATES("506", Constants.RT_FAIL, "服务器内部配置错误" ),
	/**
	 * 异常：HTTP Status 507（服务器无法存储完成请求所必须的内容）-> 这个状况被认为是临时的。WebDAV (RFC 4918)
	 */
	SC_INSUFFICIENT_STORAGE("507", Constants.RT_FAIL, "服务器无法存储完成请求所必须的内容"),
	/**
	 * 异常：HTTP Status 508（存储空间不足）
	 */
	SC_LOOP_DETECTED("508", Constants.RT_FAIL, "服务器存储空间不足"),
	/**
	 * 异常：HTTP Status 509（服务器达到带宽限制）->这不是一个官方的状态码，但是仍被广泛使用。
	 */
	SC_BANDWIDTH_LIMIT_EXCEEDED("509", Constants.RT_FAIL, "服务器达到带宽限制"),
	/**
	 * 异常：HTTP Status 510（获取资源所需要的策略并没有没满足）
	 */
	SC_NOT_EXTENDED("510", Constants.RT_FAIL, "获取资源所需要的策略并没有没满足"),
	/**
	 * 异常：HTTP Status 511（要求网络认证）
	 */
	SC_NETWORK_AUTHENTICATION_REQUIRED("511", Constants.RT_FAIL, "要求网络认证"),

	// --- Custom Server Error ---
	
	SC_SUCCESS("0", Constants.RT_SUCCESS, "请求成功"),
	
	SC_ACCESS_DENIED("10110", Constants.RT_FAIL, "不允许访问（功能未授权）"),
	
	SC_FAIL("10111", Constants.RT_FAIL,"请求失败"),
	
	SC_EMPTY("10112", Constants.RT_FAIL,"数据为空" ),

	/**
	 * TypeMismatchException	400 (Bad Request)
	 */
	SC_TYPE_MISMATCH("10113", Constants.RT_FAIL, "参数类型不匹配"),
	/**
	 * MissingMatrixVariableException	400 (Bad Request)
	 */
	SC_MISSING_MATRIX_VARIABLE("10114", Constants.RT_FAIL, "缺少矩阵变量"),
	/**
	 * MissingPathVariableException	400 (Bad Request)
	 */
	SC_MISSING_PATH_VARIABLE("10115", Constants.RT_FAIL, "缺少URI模板变量"),
	/**
	 * MissingRequestCookieException	400 (Bad Request)
	 */
	SC_MISSING_REQUEST_COOKIE("10116", Constants.RT_FAIL, "缺少Cookie变量"),
	/**
	 * MissingRequestHeaderException	400 (Bad Request)
	 */
	SC_MISSING_REQUEST_HEADER("10117", Constants.RT_FAIL, "缺少请求头"),
	/**
	 * MissingServletRequestParameterException	400 (Bad Request)
	 */
	SC_MISSING_REQUEST_PARAM("10118", Constants.RT_FAIL, "缺少参数"),
	/**
	 * MissingServletRequestPartException	400 (Bad Request)
	 */
	SC_MISSING_REQUEST_PART("10119", Constants.RT_FAIL, "缺少请求对象"),
	/**
	 * UnsatisfiedServletRequestParameterException	400 (Bad Request)
	 */
	SC_UNSATISFIED_PARAM("10120", Constants.RT_FAIL, "参数规则不满足"),
	/**
	 * ServletRequestBindingException	400 (Bad Request)
	 */
	SC_BINDING_ERROR("10121", Constants.RT_FAIL, "参数绑定错误"),
	/**
	 * JsonProcessingException	400 (Bad Request)
	 * HttpMessageNotReadableException	400 (Bad Request)
	 */
	SC_PARSING_ERROR("10122", Constants.RT_FAIL, "参数解析错误"),
	/**
	 * MethodArgumentNotValidException	400 (Bad Request)
	 * BindException					400 (Bad Request)								      
	 */
	SC_METHOD_ARGUMENT_NOT_VALID("10123", Constants.RT_FAIL, "参数验证失败"),
	
	
	/**
	 * RuntimeException		500 (Internal Server Error)							      
	 */
	SC_RUNTIME_EXCEPTION("10201", Constants.RT_ERROR, "服务器：运行时异常"),
	/**
	 * NullPointerException		500 (Internal Server Error)					      
	 */
	SC_NULL_POINTER_EXCEPTION("10202", Constants.RT_ERROR, "服务器：空值异常"),
	/**
	 * ClassCastException		500 (Internal Server Error)						      
	 */
	SC_CLASS_CAST_EXCEPTION("10203", Constants.RT_ERROR, "服务器：数据类型转换异常"),
	/**
	 * IOException		500 (Internal Server Error)								      
	 */
	SC_IO_EXCEPTION ("10204", Constants.RT_ERROR, "服务器：IO异常"),
	/**
	 * NoSuchMethodException		500 (Internal Server Error)					      
	 */
	SC_NO_SUCH_METHOD_EXCEPTION("10205", Constants.RT_ERROR, "服务器：未知方法异常"),
	/**
	 * IllegalArgumentException		500 (Internal Server Error)					      
	 */
	SC_ILLEGAL_ARGUMENT_EXCEPTION("10206", Constants.RT_ERROR, "服务器：非法参数异常"),
	/**
	 * IndexOutOfBoundsException	500 (Internal Server Error)				      
	 */
	SC_INDEX_OUT_OF_BOUNDS_EXCEPTION("10207", Constants.RT_ERROR, "服务器：数组越界异常"),
	/**
	 * NetworkException		500 (Internal Server Error)							      
	 */
	SC_NETWORK_EXCEPTION("10208", Constants.RT_ERROR, "服务器：网络异常");
  
	private final String code;
	private final String status;
	private final String reason;
	
    private ApiCode(String code, String status, String reason) {
        this.code = code;
        this.status = status;
        this.reason = reason;
    }

    @Override
    public String toString() {
    	return code;
    }

	public String getCode() {
		return code;
	}

	public String getStatus() {
		return status;
	}

	public String getReason() {
		return reason;
	}
	
	public <T> ApiRestResponse<T> toResponse() {
		return ApiRestResponse.of(this);
	}
    
	public <T> ApiRestResponse<T> toResponse(final String message) {
		return ApiRestResponse.of(this, message, null);
	}
	
	public <T> ApiRestResponse<T> toResponse(final T data) {
		return ApiRestResponse.of(this, data);
	}
    
}

