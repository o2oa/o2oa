package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.DataLobItem;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;

/**
 * 创建处于start状态的work
 * 
 * @author Rui
 *
 */
class ActionDelete {

	WrapOutId execute(Business business, String id) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Work work = emc.find(id, Work.class, ExceptionWhen.not_found);
		emc.beginTransaction(Task.class);
		emc.beginTransaction(TaskCompleted.class);
		emc.beginTransaction(Read.class);
		emc.beginTransaction(ReadCompleted.class);
		emc.beginTransaction(Review.class);
		emc.beginTransaction(Attachment.class);
		emc.beginTransaction(WorkLog.class);
		emc.beginTransaction(DataItem.class);
		emc.beginTransaction(DataLobItem.class);
		emc.beginTransaction(Work.class);
		emc.delete(Task.class, business.task().listWithWork(work.getId()));
		emc.delete(TaskCompleted.class, business.taskCompleted().listWithWork(work.getId()));
		emc.delete(Read.class, business.read().listWithWork(work.getId()));
		emc.delete(ReadCompleted.class, business.readCompleted().listWithWork(work.getId()));
		emc.delete(Review.class, business.review().listWithWork(work.getId()));
		// 判断附件是否有其他的Work在引用，如果没有那么删除
		for (Attachment o : emc.list(Attachment.class, business.attachment().listWithJob(work.getJob()))) {
			if (!business.attachmentMultiReferenced(o.getId())) {
				// 删除实际附件
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						o.getStorage());
				// 如果没有附件存储的对象就算了
				if (null != mapping) {
					o.deleteContent(mapping);
				}
				emc.remove(o);
			}
		}
		emc.delete(WorkLog.class, business.workLog().listWithWork(work.getId()));
		// 没有其他拆分的work，那么删除Data
		if (business.work().listWithJob(work.getJob()).size() == 1) {
			List<DataItem> os = business.dataItem().listWithJobWithPath(work.getJob());
			for (DataItem o : os) {
				if (o.isLobItem()) {
					DataLobItem lob = emc.find(o.getLobItem(), DataLobItem.class);
					if (null != lob) {
						emc.remove(lob);
					}
				}
			}
		}
		emc.remove(work);
		emc.commit();
		WrapOutId wrap = new WrapOutId(work.getId());
		return wrap;
	}

}
