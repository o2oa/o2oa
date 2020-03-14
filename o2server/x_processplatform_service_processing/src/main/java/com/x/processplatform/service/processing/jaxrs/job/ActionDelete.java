package com.x.processplatform.service.processing.jaxrs.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.query.core.entity.Item;

public class ActionDelete extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String job) throws Exception {

		Callable<ActionResult<List<Wo>>> callable = new Callable<ActionResult<List<Wo>>>() {
			public ActionResult<List<Wo>> call() throws Exception {
				List<Wo> wos = new ArrayList<>();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
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
					emc.beginTransaction(DocumentVersion.class);
					emc.beginTransaction(Record.class);
					for (Task o : emc.listEqual(Task.class, Task.job_FIELDNAME, job)) {
						emc.remove(o);
					}
					for (TaskCompleted o : emc.listEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, job)) {
						emc.remove(o);
					}
					for (Read o : emc.listEqual(Read.class, Read.job_FIELDNAME, job)) {
						emc.remove(o);
					}
					for (ReadCompleted o : emc.listEqual(ReadCompleted.class, ReadCompleted.job_FIELDNAME, job)) {
						emc.remove(o);
					}
					for (Review o : emc.listEqual(Review.class, Review.job_FIELDNAME, job)) {
						emc.remove(o);
					}
					/* 删除所有附件 */
					for (Attachment o : emc.listEqual(Attachment.class, Attachment.job_FIELDNAME, job)) {
						StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
								o.getStorage());
						/* 如果没有附件存储的对象就算了 */
						if (null != mapping) {
							o.deleteContent(mapping);
						}
						emc.remove(o);
					}
					for (WorkLog o : emc.listEqual(WorkLog.class, WorkLog.job_FIELDNAME, job)) {
						emc.remove(o);
					}
					for (Item o : emc.listEqual(Item.class, Item.bundle_FIELDNAME, job)) {
						emc.remove(o);
					}
					for (Work o : emc.listEqual(Work.class, Work.job_FIELDNAME, job)) {
						emc.remove(o);
						Wo wo = new Wo();
						wo.setId(o.getId());
						wos.add(wo);
					}
					for (WorkCompleted o : emc.listEqual(WorkCompleted.class, WorkCompleted.job_FIELDNAME, job)) {
						emc.remove(o);
						Wo wo = new Wo();
						wo.setId(o.getId());
						wos.add(wo);
					}
					for (DocumentVersion o : emc.listEqual(DocumentVersion.class, DocumentVersion.job_FIELDNAME, job)) {
						emc.remove(o);
						Wo wo = new Wo();
						wo.setId(o.getId());
						wos.add(wo);
					}
					for (Record o : emc.listEqual(Record.class, Record.job_FIELDNAME, job)) {
						emc.remove(o);
						Wo wo = new Wo();
						wo.setId(o.getId());
						wos.add(wo);
					}
					emc.commit();
				}
				ActionResult<List<Wo>> result = new ActionResult<>();
				result.setData(wos);
				return result;
			}
		};

		return ProcessPlatformExecutorFactory.get(job).submit(callable).get();

	}

	public static class Wo extends WoId {

	}
}
