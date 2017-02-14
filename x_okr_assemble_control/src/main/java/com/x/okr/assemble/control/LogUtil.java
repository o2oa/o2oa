package com.x.okr.assemble.control;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志打印的帮助类
 * @author liyi
 *
 */
public class LogUtil {
	
	private static Map<String, LogUtil> loggerMap = new HashMap<String, LogUtil>();
	
	private Class<?> cls = null;
	
	private LogUtil(Class<?> cls){
		this.cls = cls;
	}
	
	public static LogUtil getLogger( Class<?> cls ){
		if( loggerMap.get( cls.getName() ) == null ){
			System.out.println( "add a logger object for class:" + cls.getName() );
			loggerMap.put( cls.getName(), new LogUtil(cls));
		}
		return loggerMap.get( cls.getName() );
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

	public void debug( List messageList ){
		StringBuffer sb = new StringBuffer( new Date().toString() );
		sb.append( " ["+cls.getName()+"] " );
		sb.append( " [DEBUG] " );
		for( Object o : messageList){
			if( o != null && o.getClass() != null ){
				sb.append( "["+o.getClass().getName()+"]" + o.toString() + ", " );
			}else{
				sb.append( "[ null ], " );
			}
		}
		System.out.println( sb.toString() );
	}
}
