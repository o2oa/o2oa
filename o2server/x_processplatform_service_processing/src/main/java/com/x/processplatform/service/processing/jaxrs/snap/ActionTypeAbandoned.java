package com.x.processplatform.service.processing.jaxrs.snap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.DocSign;
import com.x.processplatform.core.entity.content.DocSignScrawl;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Snap;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.message.WorkEvent;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.query.core.entity.Item;

class ActionTypeAbandoned extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTypeAbandoned.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String workId) throws Exception {

		LOGGER.debug("execute:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> workId);

		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Work work = emc.fetch(workId, Work.class, ListTools.toList(Work.job_FIELDNAME));
			if (null == work) {
				throw new ExceptionEntityNotExist(workId, Work.class);
			}
			job = work.getJob();
		}
		return ProcessPlatformKeyClassifyExecutorFactory.get(job).submit(new CallableImpl(workId)).get(300, TimeUnit.SECONDS);
	}

	public class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		public CallableImpl(String id) {
			this.id = id;
		}

		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Work work = emc.find(id, Work.class);
				if (null == work) {
					throw new ExceptionEntityNotExist(id, Work.class);
				}
				Snap snap = new Snap(work);
				List<Item> items = new ArrayList<>();
				List<Work> works = new ArrayList<>();
				List<Task> tasks = new ArrayList<>();
				List<TaskCompleted> taskCompleteds = new ArrayList<>();
				List<Read> reads = new ArrayList<>();
				List<ReadCompleted> readCompleteds = new ArrayList<>();
				List<Review> reviews = new ArrayList<>();
				List<WorkLog> workLogs = new ArrayList<>();
				List<Record> records = new ArrayList<>();
				List<DocumentVersion> documentVersions = new ArrayList<>();
				List<DocSign> docSigns = new ArrayList<>();
				List<DocSignScrawl> docSignScrawls = new ArrayList<>();
				snap.setProperties(snap(business, work.getJob(), items, works, tasks, taskCompleteds, reads,
						readCompleteds, reviews, workLogs, records, documentVersions, docSigns, docSignScrawls));
				snap.setType(Snap.TYPE_ABANDONED);
				emc.beginTransaction(Snap.class);
				emc.persist(snap, CheckPersistType.all);
				emc.commit();
				clean(business, items, works, tasks, taskCompleteds, reads, readCompleteds, reviews, workLogs, records,
						documentVersions, docSigns, docSignScrawls);
				emc.commit();
				// 创建删除事件
				emc.beginTransaction(WorkEvent.class);
				emc.persist(WorkEvent.deleteEventInstance(work), CheckPersistType.all);
				emc.commit();
				Wo wo = new Wo();
				wo.setId(snap.getId());
				result.setData(wo);
				return result;
			}
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2577413577740827608L;

	}

}
