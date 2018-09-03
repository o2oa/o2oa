package com.x.processplatform.service.processing.jaxrs.job;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.WrapOutId;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Hint;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.query.core.entity.Item;

public class ActionDelete extends BaseAction {

	protected List<WrapOutId> execute(Business business, String job) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Task.class);
		emc.beginTransaction(TaskCompleted.class);
		emc.beginTransaction(Read.class);
		emc.beginTransaction(ReadCompleted.class);
		emc.beginTransaction(Review.class);
		emc.beginTransaction(Attachment.class);
		emc.beginTransaction(WorkLog.class);
		emc.beginTransaction(Item.class);
		emc.beginTransaction(Work.class);
		emc.beginTransaction(WorkCompleted.class);
		emc.beginTransaction(Hint.class);
		emc.delete(Task.class, business.task().listWithJob(job));
		emc.delete(TaskCompleted.class, business.taskCompleted().listWithJob(job));
		emc.delete(Read.class, business.read().listWithJob(job));
		emc.delete(ReadCompleted.class, business.readCompleted().listWithJob(job));
		emc.delete(Review.class, business.review().listWithJob(job));
		emc.delete(Hint.class, business.hint().listWithJob(job));
		// 删除所有附件
		for (Attachment o : emc.list(Attachment.class, business.attachment().listWithJob(job))) {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, o.getStorage());
			/** 如果没有附件存储的对象就算了 */
			if (null != mapping) {
				o.deleteContent(mapping);
			}
			emc.remove(o);
		}
		emc.delete(WorkLog.class, business.workLog().listWithJob(job));
		for (Item o : business.dataItem().listWithJobWithPath(job)) {
			emc.remove(o);
		}
		List<String> workIds = business.work().listWithJob(job);
		emc.delete(Work.class, workIds);
		List<String> workCompletedIds = business.workCompleted().listWithJob(job);
		emc.delete(WorkCompleted.class, workCompletedIds);
		emc.commit();
		List<WrapOutId> wraps = new ArrayList<>();
		for (String str : workIds) {
			wraps.add(new WrapOutId(str));
		}
		for (String str : workCompletedIds) {
			wraps.add(new WrapOutId(str));
		}
		return wraps;
	}

}
