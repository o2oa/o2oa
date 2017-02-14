package com.x.processplatform.service.processing;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.time.WorkTime;
import com.x.organization.core.express.Organization;
import com.x.processplatform.service.processing.factory.AttachmentFactory;
import com.x.processplatform.service.processing.factory.DataItemFactory;
import com.x.processplatform.service.processing.factory.ElementFactory;
import com.x.processplatform.service.processing.factory.ReadCompletedFactory;
import com.x.processplatform.service.processing.factory.ReadFactory;
import com.x.processplatform.service.processing.factory.ReviewFactory;
import com.x.processplatform.service.processing.factory.TaskCompletedFactory;
import com.x.processplatform.service.processing.factory.TaskFactory;
import com.x.processplatform.service.processing.factory.WorkCompletedFactory;
import com.x.processplatform.service.processing.factory.WorkFactory;
import com.x.processplatform.service.processing.factory.WorkLogFactory;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private AttachmentFactory attachment;

	public AttachmentFactory attachment() throws Exception {
		if (null == this.attachment) {
			this.attachment = new AttachmentFactory(this);
		}
		return attachment;
	}

	private DataItemFactory dataItem;

	public DataItemFactory dataItem() throws Exception {
		if (null == this.dataItem) {
			this.dataItem = new DataItemFactory(this);
		}
		return dataItem;
	}

	private TaskFactory task;

	public TaskFactory task() throws Exception {
		if (null == this.task) {
			this.task = new TaskFactory(this);
		}
		return task;
	}

	private TaskCompletedFactory taskCompleted;

	public TaskCompletedFactory taskCompleted() throws Exception {
		if (null == this.taskCompleted) {
			this.taskCompleted = new TaskCompletedFactory(this);
		}
		return taskCompleted;
	}

	private ReadFactory read;

	public ReadFactory read() throws Exception {
		if (null == this.read) {
			this.read = new ReadFactory(this);
		}
		return read;
	}

	private ReadCompletedFactory readCompleted;

	public ReadCompletedFactory readCompleted() throws Exception {
		if (null == this.readCompleted) {
			this.readCompleted = new ReadCompletedFactory(this);
		}
		return readCompleted;
	}

	private ReviewFactory review;

	public ReviewFactory review() throws Exception {
		if (null == this.review) {
			this.review = new ReviewFactory(this);
		}
		return review;
	}

	private WorkLogFactory workLog;

	public WorkLogFactory workLog() throws Exception {
		if (null == this.workLog) {
			this.workLog = new WorkLogFactory(this);
		}
		return workLog;
	}

	private WorkFactory work;

	public WorkFactory work() throws Exception {
		if (null == this.work) {
			this.work = new WorkFactory(this);
		}
		return work;
	}

	private WorkCompletedFactory workCompleted;

	public WorkCompletedFactory workCompleted() throws Exception {
		if (null == this.workCompleted) {
			this.workCompleted = new WorkCompletedFactory(this);
		}
		return workCompleted;
	}

	private ElementFactory element;

	public ElementFactory element() throws Exception {
		if (null == this.element) {
			this.element = new ElementFactory(this);
		}
		return element;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization();
		}
		return organization;
	}

	private WorkTime workTime;

	public WorkTime workTime() throws Exception {
		if (null == this.workTime) {
			this.workTime = new WorkTime();
		}
		return workTime;
	}

	public boolean attachmentMultiReferenced(String attachment) throws Exception {
		Long count = this.work().countWithAttachment(attachment) + this.workCompleted().countWithAttachment(attachment);
		return (count > 1) ? true : false;
	}

}
