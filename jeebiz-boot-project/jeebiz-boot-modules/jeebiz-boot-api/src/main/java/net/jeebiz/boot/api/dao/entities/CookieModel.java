/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.dao.entities;

public class CookieModel {
	
	private String cookieName;

	private String cookieDomain;

	private String cookiePath = "/";
	
	private Integer cookieMaxAge = null;

	private boolean cookieSecure = false;

	private boolean cookieHttpOnly = false;
	
	private String cookieValue;

	/**
	 * Use the given name for cookies created by this generator.
	 * @see javax.servlet.http.Cookie#getName()
	 */
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	/**
	 * Return the given name for cookies created by this generator.
	 */
	public String getCookieName() {
		return this.cookieName;
	}

	/**
	 * Use the given domain for cookies created by this generator.
	 * The cookie is only visible to servers in this domain.
	 * @see javax.servlet.http.Cookie#setDomain
	 */
	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	/**
	 * Return the domain for cookies created by this generator, if any.
	 */
	public String getCookieDomain() {
		return this.cookieDomain;
	}

	/**
	 * Use the given path for cookies created by this generator.
	 * The cookie is only visible to URLs in this path and below.
	 * @see javax.servlet.http.Cookie#setPath
	 */
	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

	/**
	 * Return the path for cookies created by this generator.
	 */
	public String getCookiePath() {
		return this.cookiePath;
	}

	/**
	 * Use the given maximum age (in seconds) for cookies created by this generator.
	 * Useful special value: -1 ... not persistent, deleted when client shuts down
	 * @see javax.servlet.http.Cookie#setMaxAge
	 */
	public void setCookieMaxAge(Integer cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
	}

	/**
	 * Return the maximum age for cookies created by this generator.
	 */
	public Integer getCookieMaxAge() {
		return this.cookieMaxAge;
	}

	/**
	 * Set whether the cookie should only be sent using a secure protocol,
	 * such as HTTPS (SSL). This is an indication to the receiving browser,
	 * not processed by the HTTP server itself. Default is "false".
	 * @see javax.servlet.http.Cookie#setSecure
	 */
	public void setCookieSecure(boolean cookieSecure) {
		this.cookieSecure = cookieSecure;
	}

	/**
	 * Return whether the cookie should only be sent using a secure protocol,
	 * such as HTTPS (SSL).
	 */
	public boolean isCookieSecure() {
		return this.cookieSecure;
	}

	/**
	 * Set whether the cookie is supposed to be marked with the "HttpOnly" attribute.
	 * <p>Note that this feature is only available on Servlet 3.0 and higher.
	 * @see javax.servlet.http.Cookie#setHttpOnly
	 */
	public void setCookieHttpOnly(boolean cookieHttpOnly) {
		this.cookieHttpOnly = cookieHttpOnly;
	}

	/**
	 * Return whether the cookie is supposed to be marked with the "HttpOnly" attribute.
	 */
	public boolean isCookieHttpOnly() {
		return this.cookieHttpOnly;
	}

	public String getCookieValue() {
		return cookieValue;
	}

	public void setCookieValue(String cookieValue) {
		this.cookieValue = cookieValue;
	}

	
}
