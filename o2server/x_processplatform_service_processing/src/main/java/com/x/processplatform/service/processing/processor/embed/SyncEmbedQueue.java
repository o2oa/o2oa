package com.x.processplatform.service.processing.processor.embed;

import com.x.base.core.project.queue.AbstractQueue;

public class SyncEmbedQueue extends AbstractQueue<AssignData> {

	@Override
	protected void execute(AssignData o) throws Exception {
		EmbedExecutor executor = new EmbedExecutor();
		executor.execute(o);
	}

}
