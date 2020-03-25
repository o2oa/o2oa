package com.x.processplatform.service.processing.processor.embed;

import com.x.base.core.project.queue.AbstractQueue;

public class SyncEmbedQueue extends AbstractQueue<AssginData> {

	@Override
	protected void execute(AssginData o) throws Exception {
		EmbedExecutor executor = new EmbedExecutor();
		executor.execute(o);
	}

}
