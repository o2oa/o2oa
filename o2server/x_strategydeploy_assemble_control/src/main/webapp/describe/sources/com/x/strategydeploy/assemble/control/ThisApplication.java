package com.x.strategydeploy.assemble.control;

import com.x.base.core.project.Context;
//import com.x.hotpic.assemble.control.queueTask.DocumentExistsCheckTask;
//import com.x.hotpic.assemble.control.timertask.InfoExistsCheckTask;

public class ThisApplication{
	
//	public static DocumentExistsCheckTask queueLoginRecord;
	protected static Context context;
	
	public static Context context() {
		return context;
	}
	
	public static void init() {
//		try {
//			queueLoginRecord = new DocumentExistsCheckTask();
//			context().startQueue( queueLoginRecord );
//			context().schedule( InfoExistsCheckTask.class, "0 0 0 * * ?");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
