package com.x.processplatform.service.processing.jaxrs.work;

import java.util.List;
import java.util.concurrent.Callable;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Hint;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
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

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String executorSeed = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(id, Work.class);
			}
			executorSeed = work.getJob();
		}

		Callable<String> callable = new Callable<String>() {
			public String call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					Work work = emc.find(id, Work.class);
					if (null == work) {
						throw new ExceptionEntityNotExist(id, Work.class);
					}
					emc.beginTransaction(Task.class);
					emc.beginTransaction(TaskCompleted.class);
					emc.beginTransaction(Read.class);
					emc.beginTransaction(ReadCompleted.class);
					emc.beginTransaction(Review.class);
					emc.beginTransaction(Attachment.class);
					emc.beginTransaction(WorkLog.class);
					emc.beginTransaction(Item.class);
					emc.beginTransaction(Work.class);
					emc.beginTransaction(Hint.class);
					emc.beginTransaction(DocumentVersion.class);
					List<Task> tasks = emc.list(Task.class, business.task().listWithWork(work.getId()));
					List<TaskCompleted> taskCompleteds = emc.list(TaskCompleted.class,
							business.taskCompleted().listWithWork(work.getId()));
					List<Read> reads = emc.list(Read.class, business.read().listWithWork(work.getId()));
					List<ReadCompleted> readCompleteds = emc.list(ReadCompleted.class,
							business.readCompleted().listWithWork(work.getId()));
					for (Task _o : tasks) {
						emc.remove(_o);
					}
					for (TaskCompleted _o : taskCompleteds) {
						emc.remove(_o);
					}
					for (Read _o : reads) {
						emc.remove(_o);
					}
					for (ReadCompleted _o : readCompleteds) {
						emc.remove(_o);
					}
					emc.delete(TaskCompleted.class, business.taskCompleted().listWithWork(work.getId()));
					emc.delete(Read.class, business.read().listWithWork(work.getId()));
					emc.delete(ReadCompleted.class, business.readCompleted().listWithWork(work.getId()));
					emc.delete(Review.class, business.review().listWithWork(work.getId()));
					emc.delete(Hint.class, business.hint().listWithWork(work.getId()));
					emc.delete(DocumentVersion.class, business.hint().listWithWork(work.getId()));
					// 判断附件是否有其他的Work在引用，如果没有那么删除
					if (business.work().listWithJob(work.getJob()).size() == 1) {
						List<Item> os = business.item().listWithJobWithPath(work.getJob());
						for (Item o : os) {
							emc.remove(o);
						}
						for (Attachment o : business.attachment().listWithJobObject(work.getJob())) {
							StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
									o.getStorage());
							/** 如果没有附件存储的对象就算了 */
							if (null != mapping) {
								o.deleteContent(mapping);
							}
							emc.remove(o);
						}
						emc.delete(WorkLog.class, business.workLog().listWithJob(work.getJob()));
						emc.delete(DocumentVersion.class, business.documentVersion().listWithJob(work.getJob()));
					}
					emc.remove(work);
					emc.commit();
					for (Task _o : tasks) {
						MessageFactory.task_delete(_o);
					}
					for (TaskCompleted _o : taskCompleteds) {
						MessageFactory.taskCompleted_delete(_o);
					}
					for (Read _o : reads) {
						MessageFactory.read_delete(_o);
					}
					for (ReadCompleted _o : readCompleteds) {
						MessageFactory.readCompleted_delete(_o);
					}
					wo.setId(work.getId());
				}
				return "";
			}
		};

		ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();

		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}

}