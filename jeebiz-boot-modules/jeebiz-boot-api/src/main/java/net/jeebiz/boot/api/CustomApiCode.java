package net.jeebiz.boot.api;

public class CustomApiCode {

	protected final int code;
	protected final String status;
	protected final String reason;
	
	public CustomApiCode(int code, String reason) {
		this.code = code;
		this.status = code ==  ApiCodeValue.SC_SUCCESS ? Constants.RT_SUCCESS : Constants.RT_FAIL;
		this.reason = reason;
	}
	
	public CustomApiCode(int code, String status, String reason) {
		this.code = code;
		this.status = status;
		this.reason = reason;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getReason() {
		return reason;
	}
	
	public String getStatus() {
		return status;
	}
	
}
