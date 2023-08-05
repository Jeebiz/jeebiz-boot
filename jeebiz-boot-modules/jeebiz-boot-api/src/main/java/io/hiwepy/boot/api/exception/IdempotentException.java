package io.hiwepy.boot.api.exception;

import io.hiwepy.boot.api.ApiCode;
import org.springframework.core.NestedRuntimeException;

@SuppressWarnings("serial")
public class IdempotentException extends NestedRuntimeException {

    /**
     * 错误码
     */
    private int code;
    /**
     * 国际化Key
     */
    private String i18n;

    public IdempotentException(int code) {
        super("");
        this.code = code;
    }

    public IdempotentException(String msg) {
        super(msg);
        this.code = ApiCode.SC_FAIL.getCode();
    }

    public IdempotentException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public IdempotentException(ApiCode code, String i18n) {
        super(code.getReason());
        this.code = code.getCode();
        this.i18n = i18n;
    }

    public IdempotentException(int code, String i18n, String defMsg) {
        super(defMsg);
        this.code = code;
        this.i18n = i18n;
    }

    public IdempotentException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public IdempotentException(int code, String i18n, String defMsg, Throwable cause) {
        super(defMsg, cause);
        this.code = code;
        this.i18n = i18n;
    }

    public int getCode() {
        return code;
    }

    public String getI18n() {
        return i18n;
    }

}
