package com.x.processplatform.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.builder.WorkLogBuilder;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutRead;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutReadCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutReview;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutTaskCompleted;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutWorkCompleted;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.DataItem_;
import com.x.processplatform.core.entity.content.DataLobItem;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkCompleted_;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<Attachment, WrapOutAttachment> attachmentOutCopier = BeanCopyToolsBuilder
			.create(Attachment.class, WrapOutAttachment.class, null, WrapOutAttachment.Excludes);

	static BeanCopyTools<WorkCompleted, WrapOutWorkCompleted> workCompletedOutCopier = BeanCopyToolsBuilder
			.create(WorkCompleted.class, WrapOutWorkCompleted.class, null, WrapOutWorkCompleted.Excludes);

	static BeanCopyTools<TaskCompleted, WrapOutTaskCompleted> taskCompletedOutCopier = BeanCopyToolsBuilder
			.create(TaskCompleted.class, WrapOutTaskCompleted.class, null, WrapOutTaskCompleted.Excludes);

	static BeanCopyTools<Read, WrapOutRead> readOutCopier = BeanCopyToolsBuilder.create(Read.class, WrapOutRead.class,
			null, WrapOutRead.Excludes);

	static BeanCopyTools<ReadCompleted, WrapOutReadCompleted> readCompletedOutCopier = BeanCopyToolsBuilder
			.create(ReadCompleted.class, WrapOutReadCompleted.class, null, WrapOutReadCompleted.Excludes);

	static BeanCopyTools<Review, WrapOutReview> reviewOutCopier = BeanCopyToolsBuilder.create(Review.class,
			WrapOutReview.class, null, WrapOutReview.Excludes);

	static ItemConverter<DataItem> itemConverter = new ItemConverter<>(DataItem.class);

	WrapOutMap complexWithoutForm(Business business, EffectivePerson effectivePerson, WorkCompleted workCompleted)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		WrapOutMap wrap = new WrapOutMap();
		/* 装入WorkCompleted */
		wrap.put("workCompleted", workCompletedOutCopier.copy(workCompleted));
		/* 装入 Attachment */
		wrap.put("attachmentList", this.listAttachment(business, workCompleted));
		/* 装入 WorkLog */
		wrap.put("workLogList", WorkLogBuilder.complex(business,
				emc.list(WorkLog.class, business.workLog().listWithJob(workCompleted.getJob()))));
		/* 装入Data */
		// WorkCompletedDataHelper workCompletedDataHelper = new
		// WorkCompletedDataHelper(business.entityManagerContainer(),
		// workCompleted);
		wrap.put("data", this.loadData(business, workCompleted));
		Control control = business.getControlOfWorkCompleted(effectivePerson, workCompleted);
		wrap.put("control", control);
		return wrap;
	}

	String getApplicationName(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		Application application = business.application().pick(id);
		if (null != application) {
			return application.getName();
		}
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.application), id));
		cq.select(root.get(WorkCompleted_.applicationName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	String getProcessName(Business business, EffectivePerson effectivePerson, String id) throws Exception {
		Process process = business.process().pick(id);
		if (null != process) {
			return process.getName();
		}
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(WorkCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<WorkCompleted> root = cq.from(WorkCompleted.class);
		Predicate p = cb.equal(root.get(WorkCompleted_.creatorPerson), effectivePerson.getName());
		p = cb.and(p, cb.equal(root.get(WorkCompleted_.process), id));
		cq.select(root.get(WorkCompleted_.processName)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	List<WrapOutTaskCompleted> listTaskCompleted(Business business, WorkCompleted workCompleted) throws Exception {
		List<WrapOutTaskCompleted> list = new ArrayList<>();
		List<String> ids = business.taskCompleted().listWithWorkCompleted(workCompleted.getId());
		List<TaskCompleted> os = business.entityManagerContainer().list(TaskCompleted.class, ids);
		list = taskCompletedOutCopier.copy(os);
		SortTools.asc(list, "createTime");
		return list;
	}

	List<WrapOutRead> listRead(Business business, WorkCompleted workCompleted) throws Exception {
		List<WrapOutRead> list = new ArrayList<>();
		List<String> ids = business.read().listWithWorkCompleted(workCompleted.getId());
		List<Read> os = business.entityManagerContainer().list(Read.class, ids);
		list = readOutCopier.copy(os);
		SortTools.asc(list, "createTime");
		return list;
	}

	List<WrapOutReadCompleted> listReadCompleted(Business business, WorkCompleted workCompleted) throws Exception {
		List<WrapOutReadCompleted> list = new ArrayList<>();
		List<String> ids = business.readCompleted().listWithWorkCompleted(workCompleted.getId());
		List<ReadCompleted> os = business.entityManagerContainer().list(ReadCompleted.class, ids);
		list = readCompletedOutCopier.copy(os);
		SortTools.asc(list, "createTime");
		return list;
	}

	List<WrapOutReview> listReview(Business business, WorkCompleted workCompleted) throws Exception {
		List<WrapOutReview> list = new ArrayList<>();
		List<String> ids = business.review().listWithWorkCompleted(workCompleted.getId());
		List<Review> os = business.entityManagerContainer().list(Review.class, ids);
		list = reviewOutCopier.copy(os);
		SortTools.asc(list, "createTime");
		return list;
	}

	List<WrapOutAttachment> listAttachment(Business business, WorkCompleted workCompleted) throws Exception {
		List<WrapOutAttachment> list = new ArrayList<>();
		List<Attachment> os = business.entityManagerContainer().list(Attachment.class,
				workCompleted.getAttachmentList());
		list = attachmentOutCopier.copy(os);
		SortTools.asc(list, "createTime");
		return list;
	}

	Data loadData(Business business, WorkCompleted workCompleted) throws Exception {
		EntityManager em = business.entityManagerContainer().get(DataItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DataItem> cq = cb.createQuery(DataItem.class);
		Root<DataItem> root = cq.from(DataItem.class);
		Predicate p = cb.equal(root.get(DataItem_.job), workCompleted.getJob());
		List<DataItem> list = em.createQuery(cq.where(p)).getResultList();
		for (DataItem o : list) {
			if (o.isLobItem()) {
				DataLobItem lob = business.entityManagerContainer().find(o.getLobItem(), DataLobItem.class);
				if (null != lob) {
					o.setStringLobValue(lob.getData());
				}
			}
		}
		if (list.isEmpty()) {
			return new Data();
		} else {
			JsonElement jsonElement = itemConverter.assemble(list);
			if (jsonElement.isJsonObject()) {
				return gson.fromJson(jsonElement, Data.class);
			} else {
				/* 如果不是Object强制返回一个Map对象 */
				return new Data();
			}
		}
	}

}
