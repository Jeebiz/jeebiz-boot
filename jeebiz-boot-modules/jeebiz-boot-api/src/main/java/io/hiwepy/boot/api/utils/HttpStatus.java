/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.utils;

/**
 * 标准Http状态码
 * @author wandl
 * @See org.springframework.http.HttpStatus
 */
public interface HttpStatus {

    // --- 1xx Informational ---
	// HTTP: Status 1xx(临时响应) ->表示临时响应并需要请求者继续执行操作的状态代码。


    /** {@code 100 Continue} (HTTP/1.1 - RFC 2616) */
	//HTTP: Status 100(继续)	  -> 请求者应当继续提出请求。 服务器返回此代码表示已收到请求的第一部分，正在等待其余部分。
    public static final int SC_CONTINUE = 100;
    /** {@code 101 Switching Protocols} (HTTP/1.1 - RFC 2616)*/
    //HTTP: Status 101(切换协议)   -> 请求者已要求服务器切换协议，服务器已确认并准备切换。
    public static final int SC_SWITCHING_PROTOCOLS = 101;
    /** {@code 102 Processing} (WebDAV - RFC 2518) */
    public static final int SC_PROCESSING = 102;

    // --- 2xx Success ---
    //HTTP Status 2xx(成功)   ->表示成功处理了请求的状态代码;

    /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 200(成功)    -> 服务器已成功处理了请求。 通常，这表示服务器提供了请求的网页。
    public static final int SC_OK = 200;
    /** {@code 201 Created} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 201(已创建)    -> 请求成功并且服务器创建了新的资源。
    public static final int SC_CREATED = 201;
    /** {@code 202 Accepted} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 202(已接受)    -> 服务器已接受请求，但尚未处理。
    public static final int SC_ACCEPTED = 202;
    /** {@code 203 Non Authoritative Information} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 203(非授权信息)    -> 服务器已成功处理了请求，但返回的信息可能来自另一来源。
    public static final int SC_NON_AUTHORITATIVE_INFORMATION = 203;
    /** {@code 204 No Content} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 204(无内容)    -> 服务器成功处理了请求，但没有返回任何内容。
    public static final int SC_NO_CONTENT = 204;
    /** {@code 205 Reset Content} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 205(重置内容)    -> 服务器成功处理了请求，但没有返回任何内容。
    public static final int SC_RESET_CONTENT = 205;
    /** {@code 206 Partial Content} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 206(部分内容)    -> 服务器成功处理了部分 GET 请求。
    public static final int SC_PARTIAL_CONTENT = 206;
    /**
     * {@code 207 Multi-Status} (WebDAV - RFC 2518)
     * or
     * {@code 207 Partial Update OK} (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
     */
    public static final int SC_MULTI_STATUS = 207;

    // --- 3xx Redirection ---
    //HTTP Status 3xx（重定向）    ->这要完成请求，需要进一步操作。通常，这些状态码用来重定向。

    /** {@code 300 Mutliple Choices} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 300（多种选择）     ->针对请求，服务器可执行多种操作。服务器可根据请求者 (user agent) 选择一项操作，或提供操作列表供请求者选择。
    public static final int SC_MULTIPLE_CHOICES = 300;
    /** {@code 301 Moved Permanently} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 301（永久移动）     ->请求的网页已永久移动到新位置。服务器返回此响应（对 GET 或 HEAD 请求的响应）时，会自动将请求者转到新位置。您应使用此代码告诉 Googlebot 某个网页或网站已永久移动到新位置。
    public static final int SC_MOVED_PERMANENTLY = 301;
    /** {@code 302 Moved Temporarily} (Sometimes {@code Found}) (HTTP/1.0 - RFC 1945) */
    /* HTTP Status 302（临时移动）
	   ->服务器目前从不同位置的网页响应请求，但请求者应继续使用原有位置来响应以后的请求。此代码与响应 GET 和 HEAD 请求的 301 代码类似，
	   	 会自动将请求者转到不同的位置，但您不应使用此代码来告诉 Googlebot 某个网页或网站已经移动，因为 Googlebot 会继续抓取原有位置并编制索引。
    */
    public static final int SC_MOVED_TEMPORARILY = 302;
    /** {@code 303 See Other} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 303（查看其他位置）    -> 请求者应当对不同的位置使用单独的 GET 请求来检索响应时，服务器返回此代码。对于除 HEAD 之外的所有请求，服务器会自动转到其他位置。
    public static final int SC_SEE_OTHER = 303;
    /** {@code 304 Not Modified} (HTTP/1.0 - RFC 1945) */
    /*HTTP Status 304（没有修改）
	  ->自从上次请求后，请求的网页未修改过。服务器返回此响应时，不会返回网页内容。如果网页自请求者上次请求后再也没有更 改过，
	  	您应将服务器配置为返回此响应（称为 If-Modified-Since HTTP 标头）。服务器可以告诉 Googlebot 自从上次抓取后网页没有变更，进而节省带宽和开销。
     */
    public static final int SC_NOT_MODIFIED = 304;
    /** {@code 305 Use Proxy} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 305（使用代理）    -> 请求者只能使用代理访问请求的网页。如果服务器返回此响应，还表示请求者应使用代理。
    public static final int SC_USE_PROXY = 305;
    /** {@code 307 Temporary Redirect} (HTTP/1.1 - RFC 2616) */
    /*HTTP Status 307（使用代理）
	  -> 服务器目前从不同位置的网页响应请求，但请求者应继续使用原有位置来响应以后的请求。
	  此代码与响应 GET 和 HEAD 请求的 <a href=answer.py?answer=>301</a> 代码类似，会自动将请求者转到不同的位置，
	  但您不应使用此代码来告诉 Googlebot 某个页面或网站已经移动，因为 Googlebot 会继续抓取原有位置并编制索引。
     */
    public static final int SC_TEMPORARY_REDIRECT = 307;

    // --- 4xx Client Error ---
    //HTTP Status 4xx(请求错误)   ->这些状态代码表示请求可能出错，妨碍了服务器的处理。

    /** {@code 400 Bad Request} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 400（错误请求）    ->服务器不理解请求的语法。
    public static final int SC_BAD_REQUEST = 400;
    /** {@code 401 Unauthorized} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 401（未授权）     ->请求要求身份验证。 对于需要登录的网页，服务器可能返回此响应。
    public static final int SC_UNAUTHORIZED = 401;
    /** {@code 402 Payment Required} (HTTP/1.1 - RFC 2616) */
    public static final int SC_PAYMENT_REQUIRED = 402;
    /** {@code 403 Forbidden} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 403（禁止）    -> 服务器拒绝请求。
    public static final int SC_FORBIDDEN = 403;
    /** {@code 404 Not Found} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 404（未找到）     ->服务器找不到请求的网页。
    public static final int SC_NOT_FOUND = 404;
    /** {@code 405 Method Not Allowed} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 405（方法禁用）    ->禁用请求中指定的方法。
    public static final int SC_METHOD_NOT_ALLOWED = 405;
    /** {@code 406 Not Acceptable} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 406（不接受）     ->无法使用请求的内容特性响应请求的网页。
    public static final int SC_NOT_ACCEPTABLE = 406;
    /** {@code 407 Proxy Authentication Required} (HTTP/1.1 - RFC 2616)*/
    //HTTP Status 407（需要代理授权）     ->此状态代码与 401（未授权）类似，但指定请求者应当授权使用代理。
    public static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
    /** {@code 408 Request Timeout} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 408（请求超时）    ->服务器等候请求时发生超时。
    public static final int SC_REQUEST_TIMEOUT = 408;
    /** {@code 409 Conflict} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 409（冲突）     ->服务器在完成请求时发生冲突。 服务器必须在响应中包含有关冲突的信息。
    public static final int SC_CONFLICT = 409;
    /** {@code 410 Gone} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 410（已删除）    -> 如果请求的资源已永久删除，服务器就会返回此响应。
    public static final int SC_GONE = 410;
    /** {@code 411 Length Required} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 411（需要有效长度）     ->服务器不接受不含有效内容长度标头字段的请求。
    public static final int SC_LENGTH_REQUIRED = 411;
    /** {@code 412 Precondition Failed} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 412（未满足前提条件）     ->服务器未满足请求者在请求中设置的其中一个前提条件。
    public static final int SC_PRECONDITION_FAILED = 412;
    /** {@code 413 Request Entity Too Large} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 413（请求实体过大）     ->服务器无法处理请求，因为请求实体过大，超出服务器的处理能力。
    public static final int SC_REQUEST_TOO_LONG = 413;
    /** {@code 414 Request-URI Too Long} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 414（请求的 URI 过长） 请求的 URI（通常为网址）过长，服务器无法处理。
    public static final int SC_REQUEST_URI_TOO_LONG = 414;
    /** {@code 415 Unsupported Media Type} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 415（不支持的媒体类型）    ->请求的格式不受请求页面的支持。
    public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;
    /** {@code 416 Requested Range Not Satisfiable} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 416（请求范围不符合要求）     ->如果页面无法提供请求的范围，则服务器会返回此状态代码。
    public static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
    /** {@code 417 Expectation Failed} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 417（未满足期望值）     ->服务器未满足”期望”请求标头字段的要求。
    public static final int SC_EXPECTATION_FAILED = 417;

    /**
     * Static constant for a 418 error.
     * {@code 418 Unprocessable Entity} (WebDAV drafts?)
     * or {@code 418 Reauthentication Required} (HTTP/1.1 drafts?)
     */
    // not used
    // public static final int SC_UNPROCESSABLE_ENTITY = 418;

    /**
     * Static constant for a 419 error.
     * {@code 419 Insufficient Space on Resource}
     * (WebDAV - draft-ietf-webdav-protocol-05?)
     * or {@code 419 Proxy Reauthentication Required}
     * (HTTP/1.1 drafts?)
     */
    public static final int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;
    /**
     * Static constant for a 420 error.
     * {@code 420 Method Failure}
     * (WebDAV - draft-ietf-webdav-protocol-05?)
     */
    public static final int SC_METHOD_FAILURE = 420;
    /** {@code 422 Unprocessable Entity} (WebDAV - RFC 2518) */
    public static final int SC_UNPROCESSABLE_ENTITY = 422;
    /** {@code 423 Locked} (WebDAV - RFC 2518) */
    //锁定的错误
    public static final int SC_LOCKED = 423;
    /** {@code 424 Failed Dependency} (WebDAV - RFC 2518) */
    public static final int SC_FAILED_DEPENDENCY = 424;
    /** {@code 426 Upgrade Required} */
    public static final int SC_UPGRADE_REQUIRED = 426;
    /** {@code 428（要求先决条件）} */
    public static final int SC_PRECONDITION_REQUIRED = 428;
    /** {@code 429（太多请求）} */
    public static final int SC_TOO_MANY_REQUESTS = 429;
    /** {@code 431（请求头字段太大）} */
    public static final int SC_REQUEST_HEADER_FIELDS_TOO_LARGE = 431;
    /** {@code 451（因法律原因不可用）} */
    public static final int SC_UNAVAILABLE_FOR_LEGAL_REASONS = 451;

    // --- 5xx Server Error ---
    //HTTP Status 5xx（服务器错误）    ->这些状态代码表示服务器在尝试处理请求时发生内部错误。 这些错误可能是服务器本身的错误，而不是请求出错。

    /** {@code 500 Server Error} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 500（服务器内部错误）    ->服务器遇到错误，无法完成请求。
    public static final int SC_INTERNAL_SERVER_ERROR = 500;
    /** {@code 501 Not Implemented} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 501（尚未实施）     ->服务器不具备完成请求的功能。 例如，服务器无法识别请求方法时可能会返回此代码。
    public static final int SC_NOT_IMPLEMENTED = 501;
    /** {@code 502 Bad Gateway} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 502（错误网关）     ->服务器作为网关或代理，从上游服务器收到无效响应。
    public static final int SC_BAD_GATEWAY = 502;
    /** {@code 503 Service Unavailable} (HTTP/1.0 - RFC 1945) */
    //HTTP Status 503（服务不可用）    -> 服务器目前无法使用（由于超载或停机维护）。 通常，这只是暂时状态。
    public static final int SC_SERVICE_UNAVAILABLE = 503;
    /** {@code 504 Gateway Timeout} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 504（网关超时）     ->服务器作为网关或代理，但是没有及时从上游服务器收到请求。
    public static final int SC_GATEWAY_TIMEOUT = 504;
    /** {@code 505 HTTP Version Not Supported} (HTTP/1.1 - RFC 2616) */
    //HTTP Status 505（HTTP 版本不受支持）    -> 服务器不支持请求中所用的 HTTP 协议版本。
    public static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
    // HTTP Status 506（服务器内部配置错误）-> 由《透明内容协商协议》（RFC 2295）扩展，代表服务器存在内部配置错误：被请求的协商变元资源被配置为在透明内容协商中使用自己，因此在一个协商处理中不是一个合适的重点。
    public static final int SC_VARIANT_ALSO_NEGOTIATES = 506;
    /** {@code 507 Insufficient Storage} (WebDAV - RFC 2518) */
    public static final int SC_INSUFFICIENT_STORAGE = 507;
    // HTTP Status 508（存储空间不足）
    public static final int SC_LOOP_DETECTED = 508;
    // HTTP Status 509（服务器达到带宽限制）->这不是一个官方的状态码，但是仍被广泛使用。
    public static final int SC_BANDWIDTH_LIMIT_EXCEEDED = 509;
    // HTTP Status 510（获取资源所需要的策略并没有没满足）
    public static final int SC_NOT_EXTENDED = 510;
    // HTTP Status 511（要求网络认证）
    public static final int SC_NETWORK_AUTHENTICATION_REQUIRED = 511;

}
