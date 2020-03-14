package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;
import java.util.concurrent.Callable;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.query.core.entity.Item;

/**
 * 
 * @author Rui
 *
 */
class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = work.getJob();
		}

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					Work work = emc.find(id, Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(id, Work.class);
					}
					if (business.work().listWithJob(work.getJob()).size() > 1) {
						List<String> taskIds = emc.idsEqual(Task.class, Task.work_FIELDNAME, work.getId());
						if (ListTools.isNotEmpty(taskIds)) {
							emc.beginTransaction(Task.class);
							emc.delete(Task.class, taskIds);
						}
						emc.beginTransaction(Work.class);
						emc.remove(work, CheckRemoveType.all);
						emc.commit();
					} else {
						deleteTask(business, work.getJob());
						deleteTaskCompleted(business, work.getJob());
						deleteRead(business, work.getJob());
						deleteReadCompleted(business, work.getJob());
						deleteReview(business, work.getJob());
						deleteAttachment(business, work.getJob());
						deleteWorkLog(business, work.getJob());
						deleteItem(business, work.getJob());
						deleteDocumentVersion(business, work.getJob());
						deleteRecord(business, work.getJob());
						deleteWork(business, work);
						emc.commit();
					}
					ActionResult<Wo> result = new ActionResult<>();
					Wo wo = new Wo();
					wo.setId(work.getId());
					result.setData(wo);
					return result;
				}
			}
		};

		return ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

	}

	private void deleteTask(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(Task.class, Task.job_FIELDNAME, job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(Task.class);
			for (Task o : business.entityManagerContainer().list(Task.class, ids)) {
				business.entityManagerContainer().remove(o);
				MessageFactory.task_delete(o);
			}
		}
	}

	private void deleteTaskCompleted(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME,
				job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(TaskCompleted.class);
			for (TaskCompleted o : business.entityManagerContainer().list(TaskCompleted.class, ids)) {
				business.entityManagerContainer().remove(o);
				MessageFactory.taskCompleted_delete(o);
			}
		}
	}

	private void deleteRead(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(Read.class, Read.job_FIELDNAME, job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(Read.class);
			for (Read o : business.entityManagerContainer().list(Read.class, ids)) {
				business.entityManagerContainer().remove(o);
				MessageFactory.read_delete(o);
			}
		}
	}

	private void deleteReadCompleted(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME,
				job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(ReadCompleted.class);
			for (ReadCompleted o : business.entityManagerContainer().list(ReadCompleted.class, ids)) {
				business.entityManagerContainer().remove(o);
				MessageFactory.readCompleted_delete(o);
			}
		}
	}

	private void deleteReview(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(Review.class, Review.job_FIELDNAME, job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(Review.class);
			business.entityManagerContainer().delete(Review.class, ids);
		}
	}

	private void deleteAttachment(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(Attachment.class, Attachment.job_FIELDNAME, job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(Attachment.class);
			Attachment obj;
			for (String id : ids) {
				obj = business.entityManagerContainer().find(id, Attachment.class);
				if (null != obj) {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
							obj.getStorage());
					if (null != mapping) {
						obj.deleteContent(mapping);
					}
					business.entityManagerContainer().remove(obj, CheckRemoveType.all);
				}
			}
		}
	}

	private void deleteWorkLog(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(WorkLog.class, WorkLog.job_FIELDNAME, job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(WorkLog.class);
			business.entityManagerContainer().delete(WorkLog.class, ids);
		}
	}

	private void deleteItem(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(Item.class, Item.bundle_FIELDNAME, job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(Item.class);
			business.entityManagerContainer().delete(Item.class, ids);
		}
	}

	private void deleteWork(Business business, Work work) throws Exception {
		business.entityManagerContainer().beginTransaction(Work.class);
		List<String> ids = business.entityManagerContainer().idsEqualAndNotEqual(Work.class, Work.job_FIELDNAME,
				work.getJob(), Work.id_FIELDNAME, work.getId());
		if (ListTools.isNotEmpty(ids)) {
			for (Work o : business.entityManagerContainer().list(Work.class, ids)) {
				business.entityManagerContainer().remove(o);
				MessageFactory.work_delete(o);
			}
		}
		business.entityManagerContainer().remove(work);
		MessageFactory.work_delete(work);
	}

	private void deleteDocumentVersion(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(DocumentVersion.class,
				DocumentVersion.job_FIELDNAME, job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(DocumentVersion.class);
			business.entityManagerContainer().delete(DocumentVersion.class, ids);
		}
	}

	private void deleteRecord(Business business, String job) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(Record.class, Record.job_FIELDNAME, job);
		if (ListTools.isNotEmpty(ids)) {
			business.entityManagerContainer().beginTransaction(Record.class);
			business.entityManagerContainer().delete(Record.class, ids);
		}
	}

	public static class Wo extends WoId {

	}

}