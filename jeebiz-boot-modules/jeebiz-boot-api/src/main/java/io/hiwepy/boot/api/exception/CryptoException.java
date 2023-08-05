package io.hiwepy.boot.api.exception;

import io.hiwepy.boot.api.ApiCode;

@SuppressWarnings("serial")
public class CryptoException extends BizRuntimeException {

    public CryptoException(ApiCode code, String i18n) {
        super(code, i18n);
    }

    public CryptoException(int code, String i18n, String defMsg) {
        super(code, i18n, defMsg);
    }

    public CryptoException(int code, String msg, Throwable cause) {
        super(code, msg, cause);
    }

}
