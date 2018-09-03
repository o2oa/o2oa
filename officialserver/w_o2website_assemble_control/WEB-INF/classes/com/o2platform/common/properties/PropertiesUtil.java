package com.o2platform.common.properties;

import java.util.ResourceBundle;
/**
 * 配置文件操作类
 * 创建时间:2009-1-15
 * 作者:李义
 * 单位:浙江兰德纵横网络技术有限公司
 */ 
public class PropertiesUtil {

	private PropertiesUtil() {	//不允许创建该对象
	}
	/**
	 * 从配置文件conf.property里取参数
	 * @param name
	 * @return
	 */
	public static String getProperties( String name ) {
		String resource = "config";
		ResourceBundle prop = ResourceBundle.getBundle(resource);
		return prop.getString( name );
	}
	/**
	 * 从配置文件$fileName$.properties里取参数
	 * @param fileName
	 * @param name
	 * @return
	 */
	public static String getProperties( String fileName,String name ) {
		String resource = fileName;
		ResourceBundle prop = ResourceBundle.getBundle(resource);
		return prop.getString( name );
	}
}
