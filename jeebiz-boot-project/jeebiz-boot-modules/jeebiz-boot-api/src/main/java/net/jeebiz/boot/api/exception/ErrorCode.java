/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.exception;

/**
 * Enumeration of Error types.
 */
public enum ErrorCode {
	
	/**
	 *成功				      
	 */
	SUCCESS("0" ),
	/**
	 *   失败					      
	 */
	FAIL("-1" ),
	/**
	 *异常					      
	 */
	ERROR("-2" );
    
	private final String code;
	
    private ErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
    	return code;
    }

	public String getCode() {
		return code;
	}
    
}
