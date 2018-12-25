package com.x.attendance.assemble.control.processor.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.x.attendance.assemble.control.processor.AbStractDataForOperator;
import com.x.attendance.assemble.control.processor.EntityAnalyseData;
import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.EntitySupplementData;


public class DataProcessThreadFactory {
	
	private static DataProcessThreadFactory single = null;
	/** 
     * 当一个任务通过execute(Runnable)方法欲添加到线程池时： 
     * 1.如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。 
     * 2.如果此时线程池中的数量等于 corePoolSize，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。 
     * 3.如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。 
     * 4.如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，那么通过 
     * handler所指定的策略来处理此任务。也就是：处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize 
     * ，如果三者都满了，使用handler处理被拒绝的任务。  
     *  
     * 5.当线程池中的线程数量大于corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。 
     */  
	private static ThreadPoolExecutor threadpool = null;
	 
	private DataProcessThreadFactory() {
		/** 
	     * Param:  
	     * corePoolSize - 池中所保存的线程数，包括空闲线程。  
	     * maximumPoolSize - 池中允许的最大线程数(采用LinkedBlockingQueue时没有作用)。  
	     * keepAliveTime -当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间，线程池维护线程所允许的空闲时间。  
	     * unit - keepAliveTime参数的时间单位，线程池维护线程所允许的空闲时间的单位:秒 。  
	     * workQueue - 执行前用于保持任务的队列（缓冲队列）。此队列仅保持由execute 方法提交的 Runnable 任务。  
	     * RejectedExecutionHandler -线程池对拒绝任务的处理策略(重试添加当前的任务，自动重复调用execute()方法) 
	     */
		threadpool = new ThreadPoolExecutor( 20, 40, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy() );
	}
	
	public static DataProcessThreadFactory getInstance() {
		if( single == null ) {
			single = new DataProcessThreadFactory();
		}
		return single;
	}
	
	public long getAliveThreadCount() {
		return threadpool.getActiveCount();
	}
	
	public long getTaskCount() {
		return threadpool.getTaskCount();
	}
	
	public void showdown() {
		threadpool.shutdownNow();
	}
	
	public void submit( AbStractDataForOperator data, Boolean debugger ) {
		if( "excel".equals( data.getData_type() )) {
			threadpool.execute( new OperatorDataValidator( ( EntityImportDataDetail)data, debugger ) );
		}else if( "saveData".equals( data.getData_type() ) ) {
			threadpool.execute( new OperatorDataSaver( ( EntityImportDataDetail)data, debugger ) );
		}else if( "supplement".equals( data.getData_type() ) ) {
			threadpool.execute( new OperatorDataSupplement( ( EntitySupplementData)data, debugger ) );
		}else if( "analyse".equals( data.getData_type() ) ) {
			threadpool.execute( new OperatorDataAnalyse( ( EntityAnalyseData )data, debugger ) );
		}
	}
}
