package net.jeebiz.boot.api.exception;


@SuppressWarnings("serial")
public class MessagePushException extends BizRuntimeException {

	public MessagePushException(int code) {
		super(code);
	}

	public MessagePushException(int code, String msg) {
		super(code, msg);
	}

	public MessagePushException(int code, String i18n, String defMsg) {
		super(code, i18n,  defMsg);
	}
	
	public MessagePushException(int code, String msg, Throwable cause) {
		super(code, msg, cause);
	}
	
	public MessagePushException(int code, String i18n, String defMsg, Throwable cause) {
		super(code, i18n, defMsg, cause);
	}
	
}
