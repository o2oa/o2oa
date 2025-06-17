package com.x.ai.assemble.control;

import com.x.ai.assemble.control.quartz.CmsDocumentIndexTask;
import com.x.ai.assemble.control.queue.QueueDocumentIndex;
import com.x.base.core.project.Context;

/**
 * 应用初始化及销毁业务处理
 * @author sword
 */
public class ThisApplication {

	protected static Context context;

	public static Context context() {
		return context;
	}

	public static final QueueDocumentIndex queueDocumentIndex = new QueueDocumentIndex();

	public static void init() throws Exception {
		context().startQueue(queueDocumentIndex);
		context.schedule(CmsDocumentIndexTask.class, "0 0 1,12 * * ?");
	}

	public static void destroy() {
	}
}
