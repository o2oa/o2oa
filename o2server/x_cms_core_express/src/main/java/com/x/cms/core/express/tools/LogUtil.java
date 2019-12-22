package com.x.cms.core.express.tools;

import com.x.base.core.project.tools.ListTools;

import java.util.List;

/**
 * 缓存管理帮助类
 * @author liyi
 *
 */
public class LogUtil {
	
	public static void INFO( String message, List<String> contents ) {
		System.out.println( ">>>>>Printing, " + message + ", Start -------------" );
		if(ListTools.isNotEmpty( contents )) {
			for( String content : contents ) {
				System.out.println( message + ":" + content );
			}
		}
	}
	
	public static void INFO( String message, String content ) {
		System.out.println( message + ":" + content );
	}
	
	public static void INFO( String message) {
		System.out.println( message );
	}
	
	public static void INFO( String message, Boolean b) {
		System.out.println( message + ":" + b );
	}
}
