package com.x.processplatform.service.processing.processor.cancel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.server.StorageMapping;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ProcessingAttributes;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WorkDataHelper;
import com.x.processplatform.service.processing.configurator.ProcessingConfigurator;
import com.x.processplatform.service.processing.processor.AbstractProcessor;

public class CancelProcessor extends AbstractProcessor {

	private static Logger logger = LoggerFactory.getLogger(CancelProcessor.class);

	public CancelProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriveProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		return work;
	}

	@Override
	public List<Work> executeProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes, Work work,
			Data data, Activity activity) throws Exception {
		EntityManagerContainer emc = this.entityManagerContainer();
		emc.beginTransaction(Task.class);
		emc.beginTransaction(TaskCompleted.class);
		emc.beginTransaction(Read.class);
		emc.beginTransaction(ReadCompleted.class);
		emc.beginTransaction(Review.class);
		emc.beginTransaction(Attachment.class);
		emc.beginTransaction(WorkLog.class);
		emc.beginTransaction(Work.class);
		emc.delete(Task.class, this.business().task().listWithWork(work.getId()));
		emc.delete(TaskCompleted.class, this.business().taskCompleted().listWithWork(work.getId()));
		emc.delete(Read.class, this.business().read().listWithWork(work.getId()));
		emc.delete(ReadCompleted.class, this.business().readCompleted().listWithWork(work.getId()));
		emc.delete(Review.class, this.business().review().listWithWork(work.getId()));
		for (Attachment o : this.business().entityManagerContainer().list(Attachment.class, work.getAttachmentList())) {
			if (!this.business().attachmentMultiReferenced(o.getId())) {
				StorageMapping mapping = ThisApplication.storageMappings.get(Attachment.class,
						o.getStorage());
				if (null != mapping) {
					o.deleteContent(mapping);
				}
				this.entityManagerContainer().remove(o);
			}
		}
		emc.delete(WorkLog.class, this.business().workLog().listWithWork(work.getId()));
		if (this.business().work().listWithJob(work.getJob()).size() == 1) {
			/* 如果只有一份数据，没有拆分，那么删除Data */
			WorkDataHelper workDataHelper = new WorkDataHelper(this.entityManagerContainer(), work);
			workDataHelper.remove();
		}
		emc.remove(work, CheckRemoveType.all);
		/* 强制提交，后面的提交无效，保证数据被删除 */
		emc.commit();
		/* 删除后再次检查，如果存在多个副本，且都已经在End状态，那么试图推动一个 */
		List<String> relativeWorkIds = this.business().work().listWithJob(work.getJob());
		if (this.checkAllRelativeOnEnd(relativeWorkIds)) {
			Processing processing = new Processing(new ProcessingAttributes());
			processing.processing(relativeWorkIds.get(0));
		}
		return new ArrayList<>();
	}

	@Override
	protected List<Route> inquireProcessing(ProcessingConfigurator configurator, ProcessingAttributes attributes,
			Work work, Data data, Activity activity, List<Route> routes) throws Exception {
		return new ArrayList<Route>();
	}

	private boolean checkAllRelativeOnEnd(List<String> ids) throws Exception {
		if (ids.isEmpty()) {
			return false;
		} else {
			for (Work o : this.entityManagerContainer().fetchAttribute(ids, Work.class, "activityType")) {
				if (!o.getActivityType().equals(ActivityType.end)) {
					return false;
				}
			}
			return true;
		}

	}
}