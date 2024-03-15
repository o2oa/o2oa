package com.x.processplatform.service.processing.jaxrs.snap;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
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
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.message.WorkCompletedEvent;
import com.x.processplatform.core.entity.message.WorkEvent;
import com.x.processplatform.core.express.WorkDataHelper;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.query.core.entity.Item;

class ActionRestore extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRestore.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		String job = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Snap snap = emc.fetch(id, Snap.class, ListTools.toList(Snap.job_FIELDNAME));
			if (null == snap) {
				throw new ExceptionEntityNotExist(id, Snap.class);
			}
			job = snap.getJob();
		}
		return ProcessPlatformKeyClassifyExecutorFactory.get(job).submit(new CallableImpl(id)).get(300, TimeUnit.SECONDS);
	}

	public class CallableImpl implements Callable<ActionResult<Wo>> {

		private String id;

		public CallableImpl(String id) {
			this.id = id;
		}

		@Override
		public ActionResult<Wo> call() throws Exception {
			ActionResult<Wo> result = new ActionResult<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				Snap snap = emc.find(id, Snap.class);
				if (null == snap) {
					throw new ExceptionEntityNotExist(id, Snap.class);
				}
				if (Objects.equals(Snap.TYPE_ABANDONEDWORKCOMPLETED, snap.getType())
						|| Objects.equals(Snap.TYPE_SNAPWORKCOMPLETED, snap.getType())) {
					CompletableFuture.allOf(deleteItem(business, snap.getJob()),
							deleteWorkCompleted(business, snap.getJob()), deleteTask(business, snap.getJob()),
							deleteTaskCompleted(business, snap.getJob()), deleteRead(business, snap.getJob()),
							deleteReadCompleted(business, snap.getJob()), deleteReview(business, snap.getJob()),
							deleteWorkLog(business, snap.getJob()), deleteRecord(business, snap.getJob()),
							deleteDocumentVersion(business, snap.getJob()), deleteDocSign(business, snap.getJob()),
							deleteDocSignScrawl(business, snap.getJob())).get();
				} else {
					CompletableFuture.allOf(deleteItem(business, snap.getJob()), deleteWork(business, snap.getJob()),
							deleteTask(business, snap.getJob()), deleteTaskCompleted(business, snap.getJob()),
							deleteRead(business, snap.getJob()), deleteReadCompleted(business, snap.getJob()),
							deleteReview(business, snap.getJob()), deleteWorkLog(business, snap.getJob()),
							deleteRecord(business, snap.getJob()), deleteDocumentVersion(business, snap.getJob()),
							deleteDocSign(business, snap.getJob()), deleteDocSignScrawl(business, snap.getJob())).get();
				}
				emc.commit();
				if (Objects.equals(Snap.TYPE_ABANDONEDWORKCOMPLETED, snap.getType())
						|| Objects.equals(Snap.TYPE_SNAPWORKCOMPLETED, snap.getType())) {
					restoreWorkCompleted(business, snap);
				} else {
					restore(business, snap);
				}
				emc.commit();
				emc.beginTransaction(Snap.class);
				emc.remove(snap, CheckRemoveType.all);
				emc.commit();
				Wo wo = new Wo();
				wo.setId(snap.getId());
				result.setData(wo);
				return result;
			}
		}

		private void restore(Business business, Snap snap) throws Exception {
			EntityManagerContainer emc = business.entityManagerContainer();
			emc.beginTransaction(Work.class);
			emc.beginTransaction(Task.class);
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			emc.beginTransaction(Review.class);
			emc.beginTransaction(WorkLog.class);
			emc.beginTransaction(Record.class);
			emc.beginTransaction(DocumentVersion.class);
			emc.beginTransaction(Item.class);
			emc.beginTransaction(DocSign.class);
			emc.beginTransaction(DocSignScrawl.class);
			restoreTask(emc, snap);
			restoreTaskCompleted(emc, snap);
			restoreRead(emc, snap);
			restoreReadCompleted(emc, snap);
			restoreReview(emc, snap);
			restoreWorkLog(emc, snap);
			restoreRecord(emc, snap);
			restoreDocumentVersion(emc, snap);
			restoreDocSign(emc, snap);
			restoreDocSignScrawl(emc, snap);
			if (ListTools.isNotEmpty(snap.getProperties().getWorkList())) {
				WorkDataHelper workDataHelper = new WorkDataHelper(emc, snap.getProperties().getWorkList().get(0));
				for (Work o : snap.getProperties().getWorkList()) {
					emc.persist(o, CheckPersistType.all);
				}
				workDataHelper.update(snap.getProperties().getData());
			}
			emc.commit();
			if (!snap.getProperties().getWorkList().isEmpty()) {
				// 创建create事件
				emc.beginTransaction(WorkEvent.class);
				emc.persist(WorkEvent.createEventInstance(snap.getProperties().getWorkList().get(0)),
						CheckPersistType.all);
				emc.commit();
			}
		}

		private void restoreWorkCompleted(Business business, Snap snap) throws Exception {
			EntityManagerContainer emc = business.entityManagerContainer();
			emc.beginTransaction(WorkCompleted.class);
			emc.beginTransaction(TaskCompleted.class);
			emc.beginTransaction(Read.class);
			emc.beginTransaction(ReadCompleted.class);
			emc.beginTransaction(Review.class);
			emc.beginTransaction(WorkLog.class);
			emc.beginTransaction(Record.class);
			emc.beginTransaction(Item.class);
			emc.beginTransaction(DocSign.class);
			emc.beginTransaction(DocSignScrawl.class);
			restoreTaskCompleted(emc, snap);
			restoreRead(emc, snap);
			restoreReadCompleted(emc, snap);
			restoreReview(emc, snap);
			restoreWorkLog(emc, snap);
			restoreRecord(emc, snap);
			restoreDocSign(emc, snap);
			restoreDocSignScrawl(emc, snap);
			emc.persist(snap.getProperties().getWorkCompleted(), CheckPersistType.all);
			if (BooleanUtils.isNotTrue(snap.getProperties().getWorkCompleted().getMerged())) {
				WorkDataHelper workDataHelper = new WorkDataHelper(emc, snap.getProperties().getWorkCompleted());
				workDataHelper.update(snap.getProperties().getData());
			}
			emc.commit();
			if (null != snap.getProperties().getWorkCompleted()) {
				// 创建create事件
				emc.beginTransaction(WorkCompletedEvent.class);
				emc.persist(WorkCompletedEvent.createEventInstance(snap.getProperties().getWorkCompleted()),
						CheckPersistType.all);
				emc.commit();
			}
		}

		private void restoreRead(EntityManagerContainer emc, Snap snap) throws Exception {
			for (Read o : snap.getProperties().getReadList()) {
				emc.persist(o, CheckPersistType.all);
				MessageFactory.read_create(o);
			}
		}

		private void restoreDocumentVersion(EntityManagerContainer emc, Snap snap) throws Exception {
			for (DocumentVersion o : snap.getProperties().getDocumentVersionList()) {
				emc.persist(o, CheckPersistType.all);
			}
		}

		private void restoreRecord(EntityManagerContainer emc, Snap snap) throws Exception {
			for (Record o : snap.getProperties().getRecordList()) {
				emc.persist(o, CheckPersistType.all);
			}
		}

		private void restoreWorkLog(EntityManagerContainer emc, Snap snap) throws Exception {
			for (WorkLog o : snap.getProperties().getWorkLogList()) {
				emc.persist(o, CheckPersistType.all);
			}
		}

		private void restoreReview(EntityManagerContainer emc, Snap snap) throws Exception {
			for (Review o : snap.getProperties().getReviewList()) {
				emc.persist(o, CheckPersistType.all);
				MessageFactory.review_create(o);
			}
		}

		private void restoreTask(EntityManagerContainer emc, Snap snap) throws Exception {
			for (Task o : snap.getProperties().getTaskList()) {
				emc.persist(o, CheckPersistType.all);
				MessageFactory.task_create(o);
			}
		}

		private void restoreTaskCompleted(EntityManagerContainer emc, Snap snap) throws Exception {
			for (TaskCompleted o : snap.getProperties().getTaskCompletedList()) {
				emc.persist(o, CheckPersistType.all);
				MessageFactory.taskCompleted_create(o);
			}
		}

		private void restoreReadCompleted(EntityManagerContainer emc, Snap snap) throws Exception {
			for (ReadCompleted o : snap.getProperties().getReadCompletedList()) {
				emc.persist(o, CheckPersistType.all);
				MessageFactory.readCompleted_create(o);
			}
		}

		private void restoreDocSign(EntityManagerContainer emc, Snap snap) throws Exception {
			for (DocSign o : snap.getProperties().getDocSignList()) {
				emc.persist(o, CheckPersistType.all);
			}
		}

		private void restoreDocSignScrawl(EntityManagerContainer emc, Snap snap) throws Exception {
			for (DocSignScrawl o : snap.getProperties().getDocSignScrawlList()) {
				String content = snap.getProperties().getAttachmentContentMap().get(o.getId());
				if (StringUtils.isNotEmpty(content)) {
					StorageMapping mapping = ThisApplication.context().storageMappings().get(DocSignScrawl.class,
							o.getStorage());
					if (null == mapping) {
						mapping = ThisApplication.context().storageMappings().random(DocSignScrawl.class);
					}
					if (null != mapping) {
						byte[] bytes = Base64.decodeBase64(content);
						o.updateContent(mapping, bytes, Config.general().getStorageEncrypt());
					}
				}
				emc.persist(o, CheckPersistType.all);
			}
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2577413577740827608L;

	}

}
