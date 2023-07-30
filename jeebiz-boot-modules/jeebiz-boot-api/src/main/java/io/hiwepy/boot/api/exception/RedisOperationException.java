package io.hiwepy.boot.api.exception;

import io.hiwepy.boot.api.ApiCode;

@SuppressWarnings("serial")
public class RedisOperationException extends BizRuntimeException {

    public RedisOperationException(ApiCode code, String i18n) {
        super(code, i18n);
    }

    public RedisOperationException(int code, String i18n, String defMsg) {
        super(code, i18n, defMsg);
    }

    public RedisOperationException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }

    public RedisOperationException(String msg) {
        super(msg);
    }

}
