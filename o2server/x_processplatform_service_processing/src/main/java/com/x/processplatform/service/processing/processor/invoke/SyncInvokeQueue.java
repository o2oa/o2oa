package com.x.processplatform.service.processing.processor.invoke;

import com.x.base.core.project.queue.AbstractQueue;

public class SyncInvokeQueue extends AbstractQueue<ExecuteObject> {

	@Override
	protected void execute(ExecuteObject o) throws Exception {
		InvokeExecutor executor = new InvokeExecutor();
		executor.execute(o);
	}

}
