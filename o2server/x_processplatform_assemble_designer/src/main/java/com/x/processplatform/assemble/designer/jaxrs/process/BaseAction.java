package com.x.processplatform.assemble.designer.jaxrs.process;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.x.processplatform.core.entity.element.*;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.wrap.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DocumentVersion;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Record;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.query.core.entity.Item;

abstract class BaseAction extends StandardJaxrsAction {

	void deleteBatch(EntityManagerContainer emc, Class<? extends JpaObject> clz, List<String> ids) throws Exception {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < ids.size(); i++) {
			list.add(ids.get(i));
			if ((list.size() == 1000) || (i == (ids.size() - 1))) {
				EntityManager em = emc.beginTransaction(clz);
				for (String str : list) {
					em.remove(em.find(clz, str));
				}
				em.getTransaction().commit();
				list.clear();
			}
		}
	}

	void deleteDraft(Business business, Process process) throws Exception {
		List<String> ids = business.draft().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), Draft.class, ids);
	}

	void deleteTask(Business business, Process process) throws Exception {
		List<String> ids = business.task().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), Task.class, ids);
	}

	void deleteTaskCompleted(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.taskCompleted().listWithProcessWithCompleted(process.getId(), false)
				: business.taskCompleted().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), TaskCompleted.class, ids);
	}

	void deleteRead(Business business, Process process) throws Exception {
		List<String> ids = business.read().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), Read.class, ids);
	}

	void deleteReadCompleted(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.readCompleted().listWithProcessWithCompleted(process.getId(), false)
				: business.readCompleted().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), ReadCompleted.class, ids);
	}

	void deleteReview(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.review().listWithProcessWithCompleted(process.getId(), false)
				: business.review().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), Review.class, ids);
	}

	void deleteAttachment(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.attachment().listWithProcessWithCompleted(process.getId(), false)
				: business.attachment().listWithProcess(process.getId());
		/** 附件需要单独处理删除 */
		EntityManagerContainer emc = business.entityManagerContainer();
		for (List<String> list : ListTools.batch(ids, 1000)) {
			emc.beginTransaction(Attachment.class);
			for (Attachment o : business.entityManagerContainer().list(Attachment.class, list)) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						o.getStorage());
				/** 如果找不到存储器就算了 */
				if (null != mapping) {
					o.deleteContent(mapping);
				}
				emc.remove(o);
			}
			emc.commit();
		}
	}

	void deleteItem(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
		List<String> jobs = business.work().listJobWithProcess(process.getId());
		if (!onlyRemoveNotCompleted) {
			jobs = ListUtils.union(jobs, business.workCompleted().listJobWithProcess(process.getId()));
		}
		EntityManagerContainer emc = business.entityManagerContainer();
		for (String job : jobs) {
			emc.beginTransaction(Item.class);
			for (Item o : business.item().listObjectWithJob(job)) {
				emc.remove(o);
			}
			emc.commit();
		}
	}

	void deleteSerialNumber(Business business, Process process) throws Exception {
		List<String> ids = business.serialNumber().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), SerialNumber.class, ids);
	}

	void deleteWork(Business business, Process process) throws Exception {
		List<String> ids = business.work().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), Work.class, ids);
	}

	void deleteRecord(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.record().listWithProcessWithCompleted(process.getId(), false)
				: business.record().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), Record.class, ids);
	}

	void deleteDocumentVersion(Business business, Process process) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(DocumentVersion.class,
				DocumentVersion.process_FIELDNAME, process.getId());
		this.deleteBatch(business.entityManagerContainer(), DocumentVersion.class, ids);
	}

	void deleteWorkCompleted(Business business, Process process) throws Exception {
		List<String> ids = business.workCompleted().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), WorkCompleted.class, ids);
	}

	void deleteWorkLog(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.workLog().listWithProcessWithCompleted(process.getId(), false)
				: business.workLog().listWithProcess(process.getId());
		this.deleteBatch(business.entityManagerContainer(), WorkLog.class, ids);
	}

	<T extends JpaObject> T wrapInJpaList(Object wrap, List<T> list)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (T t : list) {
			if (t.getId().equalsIgnoreCase(PropertyUtils.getProperty(wrap, "id").toString())) {
				return t;
			}
		}
		return null;
	}

	// @MethodDescribe("判断实体在Wrap对象列表中是否有同样id的对象.")
	<T> T jpaInWrapList(JpaObject jpa, List<T> list)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		for (T t : list) {
			if (PropertyUtils.getProperty(t, JpaObject.id_FIELDNAME).toString().equalsIgnoreCase(jpa.getId())) {
				return t;
			}
		}
		return null;
	}

	List<Agent> createAgent(List<WrapAgent> wraps, Process process) {
		List<Agent> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapAgent w : wraps) {
				Agent o = new Agent();
				WrapAgent.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	Begin createBegin(WrapBegin wrap, Process process) {
		Begin o = null;
		if (wrap != null) {
			o = new Begin();
			WrapBegin.inCopier.copy(wrap, o);
			o.setProcess(process.getId());
			o.setDistributeFactor(process.getDistributeFactor());
		}
		return o;
	}

	List<Cancel> createCancel(List<WrapCancel> wraps, Process process) {
		List<Cancel> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapCancel w : wraps) {
				Cancel o = new Cancel();
				WrapCancel.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Choice> createChoice(List<WrapChoice> wraps, Process process) {
		List<Choice> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapChoice w : wraps) {
				Choice o = new Choice();
				WrapChoice.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Delay> createDelay(List<WrapDelay> wraps, Process process) {
		List<Delay> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapDelay w : wraps) {
				Delay o = new Delay();
				WrapDelay.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Embed> createEmbed(List<WrapEmbed> wraps, Process process) {
		List<Embed> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapEmbed w : wraps) {
				Embed o = new Embed();
				WrapEmbed.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<End> createEnd(List<WrapEnd> wraps, Process process) {
		List<End> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapEnd w : wraps) {
				End o = new End();
				WrapEnd.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Invoke> createInvoke(List<WrapInvoke> wraps, Process process) {
		List<Invoke> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInvoke w : wraps) {
				Invoke o = new Invoke();
				WrapInvoke.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Manual> createManual(List<WrapManual> wraps, Process process) {
		List<Manual> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapManual w : wraps) {
				Manual o = new Manual();
				WrapManual.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Merge> createMerge(List<WrapMerge> wraps, Process process) {
		List<Merge> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapMerge w : wraps) {
				Merge o = new Merge();
				WrapMerge.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Parallel> createParallel(List<WrapParallel> wraps, Process process) {
		List<Parallel> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapParallel w : wraps) {
				Parallel o = new Parallel();
				WrapParallel.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Publish> createPublish(List<WrapPublish> wraps, Process process) {
		List<Publish> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapPublish w : wraps) {
				Publish o = new Publish();
				WrapPublish.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Route> createRoute(List<WrapRoute> wraps, Process process) {
		List<Route> list = new ArrayList<>();
		if (null != wraps) {
			for (int i = 0; i < wraps.size(); i++) {
				Route o = new Route();
				WrapRoute.inCopier.copy(wraps.get(i), o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}

		return list;
	}

	List<Service> createService(List<WrapService> wraps, Process process) {
		List<Service> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapService w : wraps) {
				Service o = new Service();
				WrapService.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Split> createSplit(List<WrapSplit> wraps, Process process) {
		List<Split> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapSplit w : wraps) {
				Split o = new Split();
				WrapSplit.inCopier.copy(w, o);
				o.setProcess(process.getId());
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	void updateAgent(Business business, List<WrapAgent> wraps, Process process) throws Exception {
		List<String> ids = business.agent().listWithProcess(process.getId());
		List<Agent> os = business.entityManagerContainer().list(Agent.class, ids);
		for (Agent o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapAgent w : wraps) {
				Agent o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Agent();
					o.setProcess(process.getId());
					WrapAgent.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapAgent.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateBegin(Business business, WrapBegin wrap, Process process) throws Exception {
		String id = business.begin().getWithProcess(process.getId());
		Begin o = business.entityManagerContainer().find(id, Begin.class);
		if (null != wrap) {
			if (!o.getId().equalsIgnoreCase(wrap.getId())) {
				business.entityManagerContainer().get(Begin.class).remove(o);
				o = new Begin();
				o.setProcess(process.getId());
				WrapBegin.inCopier.copy(wrap, o);
				o.setDistributeFactor(process.getDistributeFactor());
				business.entityManagerContainer().persist(o, CheckPersistType.all);
			} else {
				WrapBegin.inCopier.copy(wrap, o);
				business.entityManagerContainer().check(o, CheckPersistType.all);
			}
		}
	}

	void updateCancel(Business business, List<WrapCancel> wraps, Process process) throws Exception {
		List<String> ids = business.cancel().listWithProcess(process.getId());
		List<Cancel> os = business.entityManagerContainer().list(Cancel.class, ids);
		for (Cancel o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapCancel w : wraps) {
				Cancel o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Cancel();
					o.setProcess(process.getId());
					WrapCancel.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapCancel.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateChoice(Business business, List<WrapChoice> wraps, Process process) throws Exception {
		List<String> ids = business.choice().listWithProcess(process.getId());
		List<Choice> os = business.entityManagerContainer().list(Choice.class, ids);
		for (Choice o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapChoice w : wraps) {
				Choice o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Choice();
					o.setProcess(process.getId());
					WrapChoice.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapChoice.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateDelay(Business business, List<WrapDelay> wraps, Process process) throws Exception {
		List<String> ids = business.delay().listWithProcess(process.getId());
		List<Delay> os = business.entityManagerContainer().list(Delay.class, ids);
		for (Delay o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapDelay w : wraps) {
				Delay o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Delay();
					o.setProcess(process.getId());
					WrapDelay.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapDelay.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateEmbed(Business business, List<WrapEmbed> wraps, Process process) throws Exception {
		List<String> ids = business.embed().listWithProcess(process.getId());
		List<Embed> os = business.entityManagerContainer().list(Embed.class, ids);
		for (Embed o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapEmbed w : wraps) {
				Embed o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Embed();
					o.setProcess(process.getId());
					WrapEmbed.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapEmbed.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateEnd(Business business, List<WrapEnd> wraps, Process process) throws Exception {
		List<String> ids = business.end().listWithProcess(process.getId());
		List<End> os = business.entityManagerContainer().list(End.class, ids);
		for (End o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapEnd w : wraps) {
				End o = wrapInJpaList(w, os);
				if (null == o) {
					o = new End();
					o.setProcess(process.getId());
					WrapEnd.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapEnd.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateInvoke(Business business, List<WrapInvoke> wraps, Process process) throws Exception {
		List<String> ids = business.invoke().listWithProcess(process.getId());
		List<Invoke> os = business.entityManagerContainer().list(Invoke.class, ids);
		for (Invoke o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInvoke w : wraps) {
				Invoke o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Invoke();
					o.setProcess(process.getId());
					WrapInvoke.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapInvoke.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateManual(Business business, List<WrapManual> wraps, Process process) throws Exception {
		List<String> ids = business.manual().listWithProcess(process.getId());
		List<Manual> os = business.entityManagerContainer().list(Manual.class, ids);
		for (Manual o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapManual w : wraps) {
				Manual o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Manual();
					o.setProcess(process.getId());
					WrapManual.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapManual.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateMerge(Business business, List<WrapMerge> wraps, Process process) throws Exception {
		List<String> ids = business.merge().listWithProcess(process.getId());
		List<Merge> os = business.entityManagerContainer().list(Merge.class, ids);
		for (Merge o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapMerge w : wraps) {
				Merge o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Merge();
					o.setProcess(process.getId());
					WrapMerge.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapMerge.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateParallel(Business business, List<WrapParallel> wraps, Process process) throws Exception {
		List<String> ids = business.parallel().listWithProcess(process.getId());
		List<Parallel> os = business.entityManagerContainer().list(Parallel.class, ids);
		for (Parallel o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapParallel w : wraps) {
				Parallel o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Parallel();
					o.setProcess(process.getId());
					WrapParallel.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapParallel.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updatePublish(Business business, List<WrapPublish> wraps, Process process) throws Exception {
		List<String> ids = business.publish().listWithProcess(process.getId());
		List<Publish> os = business.entityManagerContainer().list(Publish.class, ids);
		for (Publish o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapPublish w : wraps) {
				Publish o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Publish();
					o.setProcess(process.getId());
					WrapPublish.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapPublish.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateRoute(Business business, List<WrapRoute> wraps, Process process) throws Exception {
		List<String> ids = business.route().listWithProcess(process.getId());
		List<Route> os = business.entityManagerContainer().list(Route.class, ids);
		for (Route o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapRoute w : wraps) {
				Route o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Route();
					o.setProcess(process.getId());
					WrapRoute.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapRoute.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateService(Business business, List<WrapService> wraps, Process process) throws Exception {
		List<String> ids = business.service().listWithProcess(process.getId());
		List<Service> os = business.entityManagerContainer().list(Service.class, ids);
		for (Service o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapService w : wraps) {
				Service o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Service();
					o.setProcess(process.getId());
					WrapService.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapService.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void updateSplit(Business business, List<WrapSplit> wraps, Process process) throws Exception {
		List<String> ids = business.split().listWithProcess(process.getId());
		List<Split> os = business.entityManagerContainer().list(Split.class, ids);
		for (Split o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapSplit w : wraps) {
				Split o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Split();
					o.setProcess(process.getId());
					WrapSplit.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapSplit.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void deleteAgent(Business business, Process process) throws Exception {
		for (String str : business.agent().listWithProcess(process.getId())) {
			Agent o = business.entityManagerContainer().find(str, Agent.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteBegin(Business business, Process process) throws Exception {
		String str = business.begin().getWithProcess(process.getId());
		if (StringUtils.isNotEmpty(str)) {
			Begin o = business.entityManagerContainer().find(str, Begin.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteCancel(Business business, Process process) throws Exception {
		for (String str : business.cancel().listWithProcess(process.getId())) {
			Cancel o = business.entityManagerContainer().find(str, Cancel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteChoice(Business business, Process process) throws Exception {
		for (String str : business.choice().listWithProcess(process.getId())) {
			Choice o = business.entityManagerContainer().find(str, Choice.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteDelay(Business business, Process process) throws Exception {
		for (String str : business.delay().listWithProcess(process.getId())) {
			Delay o = business.entityManagerContainer().find(str, Delay.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteEmbed(Business business, Process process) throws Exception {
		for (String str : business.embed().listWithProcess(process.getId())) {
			Embed o = business.entityManagerContainer().find(str, Embed.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteEnd(Business business, Process process) throws Exception {
		for (String str : business.end().listWithProcess(process.getId())) {
			End o = business.entityManagerContainer().find(str, End.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteInvoke(Business business, Process process) throws Exception {
		for (String str : business.invoke().listWithProcess(process.getId())) {
			Invoke o = business.entityManagerContainer().find(str, Invoke.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteManual(Business business, Process process) throws Exception {
		for (String str : business.manual().listWithProcess(process.getId())) {
			Manual o = business.entityManagerContainer().find(str, Manual.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteMerge(Business business, Process process) throws Exception {
		for (String str : business.merge().listWithProcess(process.getId())) {
			Merge o = business.entityManagerContainer().find(str, Merge.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteParallel(Business business, Process process) throws Exception {
		for (String str : business.parallel().listWithProcess(process.getId())) {
			Parallel o = business.entityManagerContainer().find(str, Parallel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deletePublish(Business business, Process process) throws Exception {
		for (String str : business.publish().listWithProcess(process.getId())) {
			Publish o = business.entityManagerContainer().find(str, Publish.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteRoute(Business business, Process process) throws Exception {
		for (String str : business.route().listWithProcess(process.getId())) {
			Route o = business.entityManagerContainer().find(str, Route.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteService(Business business, Process process) throws Exception {
		for (String str : business.service().listWithProcess(process.getId())) {
			Service o = business.entityManagerContainer().find(str, Service.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void deleteSplit(Business business, Process process) throws Exception {
		for (String str : business.split().listWithProcess(process.getId())) {
			Split o = business.entityManagerContainer().find(str, Split.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void cacheNotify() {
		CacheManager.notify(Application.class);
		CacheManager.notify(Process.class);
		CacheManager.notify(Agent.class);
		CacheManager.notify(Begin.class);
		CacheManager.notify(Cancel.class);
		CacheManager.notify(Choice.class);
		CacheManager.notify(Delay.class);
		CacheManager.notify(Embed.class);
		CacheManager.notify(End.class);
		CacheManager.notify(Invoke.class);
		CacheManager.notify(Manual.class);
		CacheManager.notify(Merge.class);
		CacheManager.notify(Parallel.class);
		CacheManager.notify(Publish.class);
		CacheManager.notify(Route.class);
		CacheManager.notify(Service.class);
		CacheManager.notify(Split.class);
	}

}
