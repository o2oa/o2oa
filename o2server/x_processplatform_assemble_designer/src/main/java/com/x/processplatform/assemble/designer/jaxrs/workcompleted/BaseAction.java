package com.x.processplatform.assemble.designer.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Hint;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.element.wrap.WrapAgent;
import com.x.processplatform.core.entity.element.wrap.WrapBegin;
import com.x.processplatform.core.entity.element.wrap.WrapCancel;
import com.x.processplatform.core.entity.element.wrap.WrapChoice;
import com.x.processplatform.core.entity.element.wrap.WrapDelay;
import com.x.processplatform.core.entity.element.wrap.WrapEmbed;
import com.x.processplatform.core.entity.element.wrap.WrapEnd;
import com.x.processplatform.core.entity.element.wrap.WrapInvoke;
import com.x.processplatform.core.entity.element.wrap.WrapManual;
import com.x.processplatform.core.entity.element.wrap.WrapMerge;
import com.x.processplatform.core.entity.element.wrap.WrapMessage;
import com.x.processplatform.core.entity.element.wrap.WrapParallel;
import com.x.processplatform.core.entity.element.wrap.WrapRoute;
import com.x.processplatform.core.entity.element.wrap.WrapService;
import com.x.processplatform.core.entity.element.wrap.WrapSplit;
import com.x.query.core.entity.Item;

abstract class BaseAction extends StandardJaxrsAction {

	void delete_batch(EntityManagerContainer emc, Class<? extends JpaObject> clz, List<String> ids) throws Exception {
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

	void delete_task(Business business, Process process) throws Exception {
		List<String> ids = business.task().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), Task.class, ids);
	}

	void delete_taskCompleted(Business business, Process process, Boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.taskCompleted().listWithProcessWithCompleted(process.getId(), false)
				: business.taskCompleted().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), TaskCompleted.class, ids);
	}

	void delete_read(Business business, Process process) throws Exception {
		List<String> ids = business.read().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), Read.class, ids);
	}

	void delete_readCompleted(Business business, Process process, Boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.readCompleted().listWithProcessWithCompleted(process.getId(), false)
				: business.readCompleted().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), ReadCompleted.class, ids);
	}

	void delete_review(Business business, Process process, Boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.review().listWithProcessWithCompleted(process.getId(), false)
				: business.review().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), Review.class, ids);
	}

	void delete_hint(Business business, Process process) throws Exception {
		List<String> ids = business.hint().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), Hint.class, ids);
	}

	void delete_attachment(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
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

	void delete_item(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
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

	void delete_serialNumber(Business business, Process process) throws Exception {
		List<String> ids = business.serialNumber().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), SerialNumber.class, ids);
	}

	void delete_work(Business business, Process process) throws Exception {
		List<String> ids = business.work().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), Work.class, ids);
	}

	void delete_workCompleted(Business business, Process process) throws Exception {
		List<String> ids = business.workCompleted().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), WorkCompleted.class, ids);
	}

	void delete_workLog(Business business, Process process, Boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.workLog().listWithProcessWithCompleted(process.getId(), false)
				: business.workLog().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), WorkLog.class, ids);
	}

	<T extends JpaObject> T wrapInJpaList(Object wrap, List<T> list) throws Exception {
		for (T t : list) {
			if (t.getId().equalsIgnoreCase(PropertyUtils.getProperty(wrap, "id").toString())) {
				return t;
			}
		}
		return null;
	}

	// @MethodDescribe("判断实体在Wrap对象列表中是否有同样id的对象.")
	<T> T jpaInWrapList(JpaObject jpa, List<T> list) throws Exception {
		for (T t : list) {
			if (PropertyUtils.getProperty(t, "id").toString().equalsIgnoreCase(jpa.getId())) {
				return t;
			}
		}
		return null;
	}

	List<Agent> create_agent(List<WrapAgent> wraps, Process process) throws Exception {
		List<Agent> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapAgent w : wraps) {
				Agent o = new Agent();
				o.setProcess(process.getId());
				WrapAgent.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	Begin create_begin(WrapBegin wrap, Process process) throws Exception {
		Begin o = null;
		if (wrap != null) {
			o = new Begin();
			o.setProcess(process.getId());
			WrapBegin.inCopier.copy(wrap, o);
			o.setDistributeFactor(process.getDistributeFactor());
		}
		return o;
	}

	List<Cancel> create_cancel(List<WrapCancel> wraps, Process process) throws Exception {
		List<Cancel> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapCancel w : wraps) {
				Cancel o = new Cancel();
				o.setProcess(process.getId());
				WrapCancel.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Choice> create_choice(List<WrapChoice> wraps, Process process) throws Exception {
		List<Choice> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapChoice w : wraps) {
				Choice o = new Choice();
				o.setProcess(process.getId());
				WrapChoice.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Delay> create_delay(List<WrapDelay> wraps, Process process) throws Exception {
		List<Delay> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapDelay w : wraps) {
				Delay o = new Delay();
				o.setProcess(process.getId());
				WrapDelay.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Embed> create_embed(List<WrapEmbed> wraps, Process process) throws Exception {
		List<Embed> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapEmbed w : wraps) {
				Embed o = new Embed();
				o.setProcess(process.getId());
				WrapEmbed.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<End> create_end(List<WrapEnd> wraps, Process process) throws Exception {
		List<End> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapEnd w : wraps) {
				End o = new End();
				o.setProcess(process.getId());
				WrapEnd.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Invoke> create_invoke(List<WrapInvoke> wraps, Process process) throws Exception {
		List<Invoke> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInvoke w : wraps) {
				Invoke o = new Invoke();
				o.setProcess(process.getId());
				WrapInvoke.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Manual> create_manual(List<WrapManual> wraps, Process process) throws Exception {
		List<Manual> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapManual w : wraps) {
				Manual o = new Manual();
				o.setProcess(process.getId());
				WrapManual.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Merge> create_merge(List<WrapMerge> wraps, Process process) throws Exception {
		List<Merge> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapMerge w : wraps) {
				Merge o = new Merge();
				o.setProcess(process.getId());
				WrapMerge.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Message> create_message(List<WrapMessage> wraps, Process process) throws Exception {
		List<Message> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapMessage w : wraps) {
				Message o = new Message();
				o.setProcess(process.getId());
				WrapMessage.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Parallel> create_parallel(List<WrapParallel> wraps, Process process) throws Exception {
		List<Parallel> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapParallel w : wraps) {
				Parallel o = new Parallel();
				o.setProcess(process.getId());
				WrapParallel.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Route> create_route(List<WrapRoute> wraps, Process process) throws Exception {
		List<Route> list = new ArrayList<>();
		if (null != wraps) {
			for (int i = 0; i < wraps.size(); i++) {
				Route o = new Route();
				o.setProcess(process.getId());
				WrapRoute.inCopier.copy(wraps.get(i), o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}

		return list;
	}

	List<Service> create_service(List<WrapService> wraps, Process process) throws Exception {
		List<Service> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapService w : wraps) {
				Service o = new Service();
				o.setProcess(process.getId());
				WrapService.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Split> create_split(List<WrapSplit> wraps, Process process) throws Exception {
		List<Split> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapSplit w : wraps) {
				Split o = new Split();
				o.setProcess(process.getId());
				WrapSplit.inCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	void update_agent(Business business, List<WrapAgent> wraps, Process process) throws Exception {
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

	void update_begin(Business business, WrapBegin wrap, Process process) throws Exception {
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

	void update_cancel(Business business, List<WrapCancel> wraps, Process process) throws Exception {
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

	void update_choice(Business business, List<WrapChoice> wraps, Process process) throws Exception {
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

	void update_delay(Business business, List<WrapDelay> wraps, Process process) throws Exception {
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

	void update_embed(Business business, List<WrapEmbed> wraps, Process process) throws Exception {
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

	void update_end(Business business, List<WrapEnd> wraps, Process process) throws Exception {
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

	void update_invoke(Business business, List<WrapInvoke> wraps, Process process) throws Exception {
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

	void update_manual(Business business, List<WrapManual> wraps, Process process) throws Exception {
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

	void update_merge(Business business, List<WrapMerge> wraps, Process process) throws Exception {
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

	void update_message(Business business, List<WrapMessage> wraps, Process process) throws Exception {
		List<String> ids = business.message().listWithProcess(process.getId());
		List<Message> os = business.entityManagerContainer().list(Message.class, ids);
		for (Message o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapMessage w : wraps) {
				Message o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Message();
					o.setProcess(process.getId());
					WrapMessage.inCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					WrapMessage.inCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_parallel(Business business, List<WrapParallel> wraps, Process process) throws Exception {
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

	void update_route(Business business, List<WrapRoute> wraps, Process process) throws Exception {
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

	void update_service(Business business, List<WrapService> wraps, Process process) throws Exception {
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

	void update_split(Business business, List<WrapSplit> wraps, Process process) throws Exception {
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

	void delete_agent(Business business, Process process) throws Exception {
		for (String str : business.agent().listWithProcess(process.getId())) {
			Agent o = business.entityManagerContainer().find(str, Agent.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_begin(Business business, Process process) throws Exception {
		String str = business.begin().getWithProcess(process.getId());
		if (StringUtils.isNotEmpty(str)) {
			Begin o = business.entityManagerContainer().find(str, Begin.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_cancel(Business business, Process process) throws Exception {
		for (String str : business.cancel().listWithProcess(process.getId())) {
			Cancel o = business.entityManagerContainer().find(str, Cancel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_choice(Business business, Process process) throws Exception {
		for (String str : business.choice().listWithProcess(process.getId())) {
			Choice o = business.entityManagerContainer().find(str, Choice.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_delay(Business business, Process process) throws Exception {
		for (String str : business.delay().listWithProcess(process.getId())) {
			Delay o = business.entityManagerContainer().find(str, Delay.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_embed(Business business, Process process) throws Exception {
		for (String str : business.embed().listWithProcess(process.getId())) {
			Embed o = business.entityManagerContainer().find(str, Embed.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_end(Business business, Process process) throws Exception {
		for (String str : business.end().listWithProcess(process.getId())) {
			End o = business.entityManagerContainer().find(str, End.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_invoke(Business business, Process process) throws Exception {
		for (String str : business.invoke().listWithProcess(process.getId())) {
			Invoke o = business.entityManagerContainer().find(str, Invoke.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_manual(Business business, Process process) throws Exception {
		for (String str : business.manual().listWithProcess(process.getId())) {
			Manual o = business.entityManagerContainer().find(str, Manual.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_merge(Business business, Process process) throws Exception {
		for (String str : business.merge().listWithProcess(process.getId())) {
			Merge o = business.entityManagerContainer().find(str, Merge.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_message(Business business, Process process) throws Exception {
		for (String str : business.message().listWithProcess(process.getId())) {
			Message o = business.entityManagerContainer().find(str, Message.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_parallel(Business business, Process process) throws Exception {
		for (String str : business.parallel().listWithProcess(process.getId())) {
			Parallel o = business.entityManagerContainer().find(str, Parallel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_route(Business business, Process process) throws Exception {
		for (String str : business.route().listWithProcess(process.getId())) {
			Route o = business.entityManagerContainer().find(str, Route.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_service(Business business, Process process) throws Exception {
		for (String str : business.service().listWithProcess(process.getId())) {
			Service o = business.entityManagerContainer().find(str, Service.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void delete_split(Business business, Process process) throws Exception {
		for (String str : business.split().listWithProcess(process.getId())) {
			Split o = business.entityManagerContainer().find(str, Split.class);
			business.entityManagerContainer().remove(o);
		}
	}

	void cacheNotify() throws Exception {
		ApplicationCache.notify(Process.class);
		ApplicationCache.notify(Agent.class);
		ApplicationCache.notify(Begin.class);
		ApplicationCache.notify(Cancel.class);
		ApplicationCache.notify(Choice.class);
		ApplicationCache.notify(Delay.class);
		ApplicationCache.notify(Embed.class);
		ApplicationCache.notify(End.class);
		ApplicationCache.notify(Invoke.class);
		ApplicationCache.notify(Manual.class);
		ApplicationCache.notify(Merge.class);
		ApplicationCache.notify(Message.class);
		ApplicationCache.notify(Parallel.class);
		ApplicationCache.notify(Route.class);
		ApplicationCache.notify(Service.class);
		ApplicationCache.notify(Split.class);
	}

	// public static class WiAgent extends Agent {
	//
	// private static final long serialVersionUID = 97907810148448429L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiAgent, Agent> copier =
	// WrapCopierFactory.wi(WiAgent.class, Agent.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiBegin extends Begin {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiBegin, Begin> copier =
	// WrapCopierFactory.wi(WiBegin.class, Begin.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiCancel extends Cancel {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiCancel, Cancel> copier =
	// WrapCopierFactory.wi(WiCancel.class, Cancel.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiChoice extends Choice {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiChoice, Choice> copier =
	// WrapCopierFactory.wi(WiChoice.class, Choice.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiDelay extends Delay {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiDelay, Delay> copier =
	// WrapCopierFactory.wi(WiDelay.class, Delay.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiEmbed extends Embed {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiEmbed, Embed> copier =
	// WrapCopierFactory.wi(WiEmbed.class, Embed.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiEnd extends End {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiEnd, End> copier = WrapCopierFactory.wi(WiEnd.class,
	// End.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiInvoke extends Invoke {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiInvoke, Invoke> copier =
	// WrapCopierFactory.wi(WiInvoke.class, Invoke.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiManual extends Manual {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiManual, Manual> copier =
	// WrapCopierFactory.wi(WiManual.class, Manual.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiMerge extends Merge {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiMerge, Merge> copier =
	// WrapCopierFactory.wi(WiMerge.class, Merge.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiMessage extends Message {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiMessage, Message> copier =
	// WrapCopierFactory.wi(WiMessage.class, Message.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiRoute extends Route {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiRoute, Route> copier =
	// WrapCopierFactory.wi(WiRoute.class, Route.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiParallel extends Parallel {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiParallel, Parallel> copier =
	// WrapCopierFactory.wi(WiParallel.class, Parallel.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiService extends Service {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiService, Service> copier =
	// WrapCopierFactory.wi(WiService.class, Service.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }
	//
	// public static class WiSplit extends Split {
	//
	// private static final long serialVersionUID = -1743455783385531904L;
	//
	// /** 这里要允许id字段的拷贝 */
	// static WrapCopier<WiSplit, Split> copier =
	// WrapCopierFactory.wi(WiSplit.class, Split.class, null,
	// ListTools.toList(DISTRIBUTEFACTOR, UPDATETIME, CREATETIME, SEQUENCE));
	// }

}
