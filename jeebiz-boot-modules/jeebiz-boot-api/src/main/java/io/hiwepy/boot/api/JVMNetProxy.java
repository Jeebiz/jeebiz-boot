/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.api;


public class JVMNetProxy {

	public static final String JVM_NET_PROXY_ENABLE = "proxySet";
	public static final String JVM_NET_PROXY_TYPE = "proxyType";
	public static final String JVM_NET_PROXY_USER = "proxyUser";
	public static final String JVM_NET_PROXY_PASSWORD = "proxyPassword";
	
	/**  
	 * 一般代理
     * 
     * proxySet 是个boolean类型的，可以设置为true或者false，true代表使用代理服务器。
     * proxyHost 是代理服务器的IP地址
     * proxyPort 是代理服务器的端口地址
	 * 
	 * 方式一：
	 * 
	 * set "JAVA_OPTS = -DproxySet=true -DproxyHost=someProxyHost -DproxyPort=someProxyPort"
	 * 
	 * 方式二：
	 * 
	 * System.getProperties().setProperty("proxySet","true");
	 * System.getProperties().setProperty("proxyHost","someProxyHost");
	 * System.getProperties().setProperty("proxyPort","someProxyPort");
	 * System.getProperties().setProperty("nonProxyHosts","nonProxyHosts");
	 * 
	 */
     
     public static final String JVM_PROXY_HOST = "proxyHost";
     public static final String JVM_PROXY_NON_HOSTS = "nonProxyHosts";
     public static final String JVM_PROXY_PORT = "proxyPort";
 	
     
	/**  
	 * 
	 * Http代理
     * 
     * proxySet 是个boolean类型的，可以设置为true或者false，true代表使用代理服务器。
     * http.proxyHost 是代理服务器的IP地址
     * http.proxyPort 是代理服务器的端口地址
 	 * http.proxyUser 是代理服务器的账户
 	 * http.proxyPassword 是代理服务器的账户密码
 	 * http.nonProxyHosts 是不需要通过代理服务器访问的主机IP地址，可以使用*通配符，多个地址用|分隔
 	 * 
 	 * 方式一：
 	 * 
 	 * set "JAVA_OPTS = -DproxySet=true -Dhttp.proxyHost=someProxyHost -Dhttp.proxyPort=someProxyPort"
 	 * 
 	 * 方式二：
 	 * 
 	 * System.getProperties().setProperty("proxySet","true");
 	 * System.getProperties().setProperty("http.proxyHost","someProxyHost");
 	 * System.getProperties().setProperty("http.proxyPort","someProxyPort");
 	 * System.getProperties().setProperty("http.nonProxyHosts","someNonProxyHosts");
 	 * System.getProperties().setProperty("http.proxyUser","someUserName");
 	 * System.getProperties().setProperty("http.proxyPassword","somePassword");
 	 * 
 	 */
	
	public static final String JVM_HTTP_PROXY_HOST = "http.proxyHost";
	public static final String JVM_HTTP_PROXY_PORT = "http.proxyPort";
	public static final String JVM_HTTP_PROXY_USER = "http.proxyUser";
	public static final String JVM_HTTP_PROXY_PASSWORD = "http.proxyPassword";
	public static final String JVM_HTTP_PROXY_NON_HOSTS = "http.nonProxyHosts";
	
	/**  
	 * 
	 * Https设置安全访问使用的代理服务器地址与端口（它没有https.nonProxyHosts属性，它按照http.nonProxyHosts 中设置的规则访问）
     * 
     * proxySet 是个boolean类型的，可以设置为true或者false，true代表使用代理服务器。
     * https.proxyHost 是代理服务器的IP地址
     * https.proxyPort 是代理服务器的端口地址
 	 * 
 	 * 方式一：
 	 * 
 	 * set "JAVA_OPTS = -DproxySet=true -Dhttps.proxyHost=someProxyHost -Dhttps.proxyPort=someProxyPort"
 	 * 
 	 * 方式二：
 	 * 
 	 * System.getProperties().setProperty("proxySet","true");
 	 * System.getProperties().setProperty("https.proxyHost","someProxyHost");
 	 * System.getProperties().setProperty("https.proxyPort","someProxyPort");
 	 * 
 	 */
			
	public static final String JVM_HTTPS_PROXY_PROXYHOST = "https.proxyHost";
	public static final String JVM_HTTPS_PROXY_PROXYPORT = "https.proxyPort";
	
	/**  
	 * 
	 * FTP代理服务器的主机、端口以及不需要使用FTP代理服务器的主机
     * 
     * proxySet 是个boolean类型的，可以设置为true或者false，true代表使用代理服务器。
     * ftp.proxyHost 是代理服务器的IP地址
     * ftp.proxyPort 是代理服务器的端口地址
 	 * ftp.nonProxyHosts 是不需要通过代理服务器访问的主机IP地址，可以使用*通配符，多个地址用|分隔
 	 * 
 	 * 方式一：
 	 * 
 	 * set "JAVA_OPTS = -DproxySet=true -Dftp.proxyHost=someProxyHost -Dftp.proxyPort=someProxyPort -Dftp.nonProxyHosts=someNonProxyHosts"
 	 * 
 	 * 方式二：
 	 * 
 	 * System.getProperties().setProperty("proxySet","true");
 	 * System.getProperties().setProperty("ftp.proxyHost","someProxyHost");
 	 * System.getProperties().setProperty("ftp.proxyPort","someProxyPort");
 	 * System.getProperties().setProperty("ftp.nonProxyHosts","someNonProxyHosts");
 	 * 
 	 */

	public static final String JVM_FTP_PROXY_PROXYHOST = "ftp.proxyHost";
	public static final String JVM_FTP_PROXY_PROXYPORT = "ftp.proxyPort";
	public static final String JVM_FTP_PROXY_NON_HOSTS = "ftp.nonProxyHosts";
	
   /**
    * 
    * Socks代理服务器的地址与端口
	* 
	* proxySet 是个boolean类型的，可以设置为true或者false，true代表使用代理服务器。
	* socksProxyHost 是代理服务器的IP地址
	* socksProxyPort 是代理服务器的端口地址
	* 
	* 方式一：
	* 
	* set "JAVA_OPTS = -DproxySet=true -DsocksProxyHost=someProxyHost -DsocksProxyPort=someProxyPort"
	* 
	* 方式二：
	* 
	* System.getProperties().setProperty("proxySet","true");
	* System.getProperties().setProperty("socksProxyHost","someProxyHost");
	* System.getProperties().setProperty("socksProxyPort","someProxyPort");
	* 
    */
   
	public static final String JVM_SOCKS_PROXY_HOST = "socksProxyHost";
	public static final String JVM_SOCKS_PROXY_PORT = "socksProxyPort";
	
	
}
