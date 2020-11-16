package com.x.server.console.action;

import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.log.SignalStackLog;
import com.x.query.core.entity.Item;

public class EraseContentProcessPlatform extends EraseContent {

	private static Logger logger = LoggerFactory.getLogger(EraseContentProcessPlatform.class);
	
	public boolean execute() throws Exception {
		this.init("processPlatform", ItemCategory.pp);
		addClass(Attachment.class);
		addClass(DocumentVersion.class);
		addClass(Draft.class);
		addClass(Read.class);
		addClass(ReadCompleted.class);
		addClass(Record.class);
		addClass(Review.class);
		addClass(SerialNumber.class);
		addClass(Task.class);
		addClass(TaskCompleted.class);
		addClass(Work.class);
		addClass(WorkCompleted.class);
		addClass(WorkLog.class);
		addClass(Item.class);
		addClass(SignalStackLog.class);
		this.run();
		return true;
	}
}