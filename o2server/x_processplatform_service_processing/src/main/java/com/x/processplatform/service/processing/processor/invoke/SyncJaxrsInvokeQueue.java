package com.x.processplatform.service.processing.processor.invoke;

import com.x.base.core.project.queue.AbstractQueue;

public class SyncJaxrsInvokeQueue extends AbstractQueue<JaxrsObject> {

	@Override
	protected void execute(JaxrsObject o) throws Exception {
		InvokeExecutor executor = new InvokeExecutor();
		executor.execute(o);
	}

}
