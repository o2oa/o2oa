package com.x.processplatform.service.processing.processor.invoke;

import com.x.base.core.project.queue.AbstractQueue;

public class SyncJaxwsInvokeQueue extends AbstractQueue<JaxwsObject> {

	@Override
	protected void execute(JaxwsObject o) throws Exception {
		InvokeExecutor executor = new InvokeExecutor();
		executor.execute(o);
	}

}
