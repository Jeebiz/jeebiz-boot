package net.jeebiz.boot.api.exception;


@SuppressWarnings("serial")
public class MessagePushException extends BizRuntimeException {


	public MessagePushException(String message) {
		super(message);
	}

	public MessagePushException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
