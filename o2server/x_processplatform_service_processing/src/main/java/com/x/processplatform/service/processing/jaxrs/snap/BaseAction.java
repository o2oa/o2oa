package com.x.processplatform.service.processing.jaxrs.snap;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.SnapProperties;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.WorkDataHelper;
import com.x.query.core.entity.Item;

abstract class BaseAction extends StandardJaxrsAction {

	protected SnapProperties snap(Business business, String job) throws Exception {
		SnapProperties properties = new SnapProperties();
		EntityManagerContainer emc = business.entityManagerContainer();
		properties.setJob(job);
		properties.setWorkList(emc.listEqual(Work.class, Work.job_FIELDNAME, job));
		properties.setTaskList(emc.listEqual(Task.class, Task.job_FIELDNAME, job));
		properties.setTaskCompletedList(emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, job));
		properties.setReadList(emc.listEqual(Read.class, Read.job_FIELDNAME, job));
		properties.setReadCompletedList(emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, job));
		properties.setReviewList(emc.listEqual(Review.class, Review.job_FIELDNAME, job));
		properties.setAttachmentList(emc.listEqual(Attachment.class, Attachment.job_FIELDNAME, job));
		properties.setRecordList(emc.listEqual(Record.class, Record.job_FIELDNAME, job));
		properties.setWorkLogList(emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, job));
		if (ListTools.isNotEmpty(properties.getWorkList())) {
			Work work = properties.getWorkList().get(0);
			properties.setTitle(work.getTitle());
			WorkDataHelper workDataHelper = new WorkDataHelper(business.entityManagerContainer(), work);
			properties.setData(workDataHelper.get());
		}
		return properties;
	}

	protected void clean(Business business, String job) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Work.class);
		emc.beginTransaction(Task.class);
		emc.beginTransaction(TaskCompleted.class);
		emc.beginTransaction(Read.class);
		emc.beginTransaction(ReadCompleted.class);
		emc.beginTransaction(Review.class);
		emc.beginTransaction(Record.class);
		emc.beginTransaction(WorkLog.class);
		emc.beginTransaction(Item.class);
		emc.deleteEqual(Work.class, Work.job_FIELDNAME, job);
		emc.deleteEqual(Task.class, Task.job_FIELDNAME, job);
		emc.deleteEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, job);
		emc.deleteEqual(Read.class, Read.job_FIELDNAME, job);
		emc.deleteEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, job);
		emc.deleteEqual(Review.class, Review.job_FIELDNAME, job);
		emc.deleteEqual(Record.class, Record.job_FIELDNAME, job);
		emc.deleteEqual(WorkLog.class, WorkLog.job_FIELDNAME, job);
		emc.deleteEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME, job, DataItem.itemCategory_FIELDNAME,
				ItemCategory.pp);
		emc.commit();
	}
}
