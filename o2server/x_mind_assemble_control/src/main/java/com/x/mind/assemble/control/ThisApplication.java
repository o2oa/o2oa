package com.x.mind.assemble.control;

import com.x.base.core.project.Context;
import com.x.base.core.project.message.MessageConnector;
import com.x.mind.assemble.control.queue.QueueShareNotify;

public class ThisApplication{
	protected static Context context;
	public static QueueShareNotify queueShareNotify;
	
	public static Context context() {
		return context;
	}
	
	public static void init() {
		queueShareNotify = new QueueShareNotify();

		MessageConnector.start(context());

		context().startQueue( queueShareNotify );
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
