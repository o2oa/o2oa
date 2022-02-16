package com.x.processplatform.service.processing.processor.embed;

import com.x.base.core.project.queue.AbstractQueue;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWi;

public class SyncEmbedQueue extends AbstractQueue<ActionAssignCreateWi> {

	@Override
	protected void execute(ActionAssignCreateWi o) throws Exception {
		EmbedExecutor executor = new EmbedExecutor();
		executor.execute(o);
	}

}
