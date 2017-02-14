package com.x.mail.assemble.control;

import java.util.Date;
import java.util.List;

/**
 * 日志打印的帮助类
 * @author liyi
 *
 */
public class LogUtil {
	
	private Class<?> cls = null;
	
	public LogUtil(Class<?> cls){
		this.cls = cls;
	}
	
	public void info( String message ){
		StringBuffer sb = new StringBuffer( new Date().toString() );
		sb.append( " ["+cls.getName()+"] " );
		sb.append( " [INFO] " );
		sb.append( message );
		System.out.println( sb.toString() );
	}
	
	public void debug( String message ){
		StringBuffer sb = new StringBuffer( new Date().toString() );
		sb.append( " ["+cls.getName()+"] " );
		sb.append( " [DEBUG] " );
		sb.append( message );
		System.out.println( sb.toString() );
	}
	
	public void debug( List messageList ){
		StringBuffer sb = new StringBuffer( new Date().toString() );
		sb.append( " ["+cls.getName()+"] " );
		sb.append( " [DEBUG] " );
		for( Object o : messageList){
			sb.append( "["+o.getClass().getName()+"]" + o.toString() + ", " );
		}
		System.out.println( sb.toString() );
	}
	
	public void warn( String message ){
		StringBuffer sb = new StringBuffer( new Date().toString() );
		sb.append( " ["+cls.getName()+"] " );
		sb.append( " [WARN] " );
		sb.append( message );
		System.out.println( sb.toString() );
	}
	
	public void error( String message ){
		StringBuffer sb = new StringBuffer( new Date().toString() );
		sb.append( " ["+cls.getName()+"] " );
		sb.append( " [ERROR] " );
		sb.append( message );
		System.out.println( sb.toString() );
	}
	
	public void error( String message, Exception e ){
		StringBuffer sb = new StringBuffer( new Date().toString() );
		sb.append( " ["+cls.getName()+"] " );
		sb.append( " [ERROR] " );
		sb.append( message );
		System.out.println( sb.toString() );
		e.printStackTrace();
	}
}
