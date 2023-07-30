/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.exception;

import io.hiwepy.boot.api.ApiCode;
import io.hiwepy.boot.api.CustomApiCode;
import org.springframework.core.NestedRuntimeException;

@SuppressWarnings("serial")
public class BizRuntimeException extends NestedRuntimeException {

    /**
     * 错误码
     */
    private int code;
    /**
     * 国际化Key
     */
    private String i18n;

    public BizRuntimeException(int code) {
        super("");
        this.code = code;
    }

    public BizRuntimeException(String msg) {
        super(msg);
        this.code = ApiCode.SC_FAIL.getCode();
    }

    public BizRuntimeException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public BizRuntimeException(ApiCode code, String i18n) {
        super(code.getReason());
        this.code = code.getCode();
        this.i18n = i18n;
    }

    public BizRuntimeException(int code, String i18n, String defMsg) {
        super(defMsg);
        this.code = code;
        this.i18n = i18n;
    }

    public BizRuntimeException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public BizRuntimeException(int code, String i18n, String defMsg, Throwable cause) {
        super(defMsg, cause);
        this.code = code;
        this.i18n = i18n;
    }

    public BizRuntimeException(CustomApiCode code) {
        super(code.getReason());
        this.code = code.getCode();
    }

    public int getCode() {
        return code;
    }

    public String getI18n() {
        return i18n;
    }

}
