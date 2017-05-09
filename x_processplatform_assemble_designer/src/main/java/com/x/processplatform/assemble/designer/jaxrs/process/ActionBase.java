package com.x.processplatform.assemble.designer.jaxrs.process;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInAgent;
import com.x.processplatform.assemble.designer.wrapin.WrapInBegin;
import com.x.processplatform.assemble.designer.wrapin.WrapInCancel;
import com.x.processplatform.assemble.designer.wrapin.WrapInChoice;
import com.x.processplatform.assemble.designer.wrapin.WrapInDelay;
import com.x.processplatform.assemble.designer.wrapin.WrapInEmbed;
import com.x.processplatform.assemble.designer.wrapin.WrapInEnd;
import com.x.processplatform.assemble.designer.wrapin.WrapInInvoke;
import com.x.processplatform.assemble.designer.wrapin.WrapInManual;
import com.x.processplatform.assemble.designer.wrapin.WrapInMerge;
import com.x.processplatform.assemble.designer.wrapin.WrapInMessage;
import com.x.processplatform.assemble.designer.wrapin.WrapInParallel;
import com.x.processplatform.assemble.designer.wrapin.WrapInProcess;
import com.x.processplatform.assemble.designer.wrapin.WrapInRoute;
import com.x.processplatform.assemble.designer.wrapin.WrapInService;
import com.x.processplatform.assemble.designer.wrapin.WrapInSplit;
import com.x.processplatform.assemble.designer.wrapout.WrapOutAgent;
import com.x.processplatform.assemble.designer.wrapout.WrapOutBegin;
import com.x.processplatform.assemble.designer.wrapout.WrapOutCancel;
import com.x.processplatform.assemble.designer.wrapout.WrapOutChoice;
import com.x.processplatform.assemble.designer.wrapout.WrapOutDelay;
import com.x.processplatform.assemble.designer.wrapout.WrapOutEmbed;
import com.x.processplatform.assemble.designer.wrapout.WrapOutEnd;
import com.x.processplatform.assemble.designer.wrapout.WrapOutInvoke;
import com.x.processplatform.assemble.designer.wrapout.WrapOutManual;
import com.x.processplatform.assemble.designer.wrapout.WrapOutMerge;
import com.x.processplatform.assemble.designer.wrapout.WrapOutMessage;
import com.x.processplatform.assemble.designer.wrapout.WrapOutParallel;
import com.x.processplatform.assemble.designer.wrapout.WrapOutProcess;
import com.x.processplatform.assemble.designer.wrapout.WrapOutProcessComplex;
import com.x.processplatform.assemble.designer.wrapout.WrapOutRoute;
import com.x.processplatform.assemble.designer.wrapout.WrapOutService;
import com.x.processplatform.assemble.designer.wrapout.WrapOutSplit;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.DataLobItem;
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
import com.x.processplatform.core.entity.element.Condition;
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

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<Agent, WrapOutAgent> agentOutCopier = BeanCopyToolsBuilder.create(Agent.class,
			WrapOutAgent.class, null, WrapOutAgent.Excludes);

	static BeanCopyTools<Begin, WrapOutBegin> beginOutCopier = BeanCopyToolsBuilder.create(Begin.class,
			WrapOutBegin.class, null, WrapOutBegin.Excludes);

	static BeanCopyTools<Cancel, WrapOutCancel> cancelOutCopier = BeanCopyToolsBuilder.create(Cancel.class,
			WrapOutCancel.class, null, WrapOutCancel.Excludes);

	static BeanCopyTools<Choice, WrapOutChoice> choiceOutCopier = BeanCopyToolsBuilder.create(Choice.class,
			WrapOutChoice.class, null, WrapOutChoice.Excludes);

	static BeanCopyTools<Delay, WrapOutDelay> delayOutCopier = BeanCopyToolsBuilder.create(Delay.class,
			WrapOutDelay.class, null, WrapOutDelay.Excludes);

	static BeanCopyTools<Embed, WrapOutEmbed> embedOutCopier = BeanCopyToolsBuilder.create(Embed.class,
			WrapOutEmbed.class, null, WrapOutEmbed.Excludes);

	static BeanCopyTools<End, WrapOutEnd> endOutCopier = BeanCopyToolsBuilder.create(End.class, WrapOutEnd.class, null,
			WrapOutEnd.Excludes);

	static BeanCopyTools<Invoke, WrapOutInvoke> invokeOutCopier = BeanCopyToolsBuilder.create(Invoke.class,
			WrapOutInvoke.class, null, WrapOutInvoke.Excludes);

	static BeanCopyTools<Manual, WrapOutManual> manualOutCopier = BeanCopyToolsBuilder.create(Manual.class,
			WrapOutManual.class, null, WrapOutManual.Excludes);

	static BeanCopyTools<Merge, WrapOutMerge> mergeOutCopier = BeanCopyToolsBuilder.create(Merge.class,
			WrapOutMerge.class, null, WrapOutMerge.Excludes);

	static BeanCopyTools<Message, WrapOutMessage> messageOutCopier = BeanCopyToolsBuilder.create(Message.class,
			WrapOutMessage.class, null, WrapOutMessage.Excludes);

	static BeanCopyTools<Parallel, WrapOutParallel> parallelOutCopier = BeanCopyToolsBuilder.create(Parallel.class,
			WrapOutParallel.class, null, WrapOutParallel.Excludes);

	static BeanCopyTools<Service, WrapOutService> serviceOutCopier = BeanCopyToolsBuilder.create(Service.class,
			WrapOutService.class, null, WrapOutService.Excludes);

	static BeanCopyTools<Split, WrapOutSplit> splitOutCopier = BeanCopyToolsBuilder.create(Split.class,
			WrapOutSplit.class, null, WrapOutSplit.Excludes);

	static BeanCopyTools<Route, WrapOutRoute> routeOutCopier = BeanCopyToolsBuilder.create(Route.class,
			WrapOutRoute.class, null, WrapOutRoute.Excludes);

	static BeanCopyTools<Process, WrapOutProcess> processOutCopier = BeanCopyToolsBuilder.create(Process.class,
			WrapOutProcess.class, null, WrapOutProcess.Excludes);

	static BeanCopyTools<Process, WrapOutProcessComplex> complexProcessOutCopier = BeanCopyToolsBuilder
			.create(Process.class, WrapOutProcessComplex.class, null, WrapOutProcessComplex.Excludes);

	static BeanCopyTools<WrapInAgent, Agent> agentInCopier = BeanCopyToolsBuilder.create(WrapInAgent.class, Agent.class,
			null, WrapInAgent.Excludes);

	static BeanCopyTools<WrapInBegin, Begin> beginInCopier = BeanCopyToolsBuilder.create(WrapInBegin.class, Begin.class,
			null, WrapInBegin.Excludes);

	static BeanCopyTools<WrapInCancel, Cancel> cancelInCopier = BeanCopyToolsBuilder.create(WrapInCancel.class,
			Cancel.class, null, WrapInCancel.Excludes);

	static BeanCopyTools<WrapInChoice, Choice> choiceInCopier = BeanCopyToolsBuilder.create(WrapInChoice.class,
			Choice.class, null, WrapInChoice.Excludes);

	static BeanCopyTools<WrapInDelay, Delay> delayInCopier = BeanCopyToolsBuilder.create(WrapInDelay.class, Delay.class,
			null, WrapInDelay.Excludes);

	static BeanCopyTools<WrapInEmbed, Embed> embedInCopier = BeanCopyToolsBuilder.create(WrapInEmbed.class, Embed.class,
			null, WrapInEmbed.Excludes);

	static BeanCopyTools<WrapInEnd, End> endInCopier = BeanCopyToolsBuilder.create(WrapInEnd.class, End.class, null,
			WrapInEnd.Excludes);

	static BeanCopyTools<WrapInInvoke, Invoke> invokeInCopier = BeanCopyToolsBuilder.create(WrapInInvoke.class,
			Invoke.class, null, WrapInInvoke.Excludes);

	static BeanCopyTools<WrapInManual, Manual> manualInCopier = BeanCopyToolsBuilder.create(WrapInManual.class,
			Manual.class, null, WrapInManual.Excludes);

	static BeanCopyTools<WrapInMerge, Merge> mergeInCopier = BeanCopyToolsBuilder.create(WrapInMerge.class, Merge.class,
			null, WrapInMerge.Excludes);

	static BeanCopyTools<WrapInMessage, Message> messageInCopier = BeanCopyToolsBuilder.create(WrapInMessage.class,
			Message.class, null, WrapInMessage.Excludes);

	static BeanCopyTools<WrapInParallel, Parallel> parallelInCopier = BeanCopyToolsBuilder.create(WrapInParallel.class,
			Parallel.class, null, WrapInParallel.Excludes);

	static BeanCopyTools<WrapInService, Service> serviceInCopier = BeanCopyToolsBuilder.create(WrapInService.class,
			Service.class, null, WrapInService.Excludes);

	static BeanCopyTools<WrapInSplit, Split> splitInCopier = BeanCopyToolsBuilder.create(WrapInSplit.class, Split.class,
			null, WrapInSplit.Excludes);

	static BeanCopyTools<WrapInRoute, Route> routeInCopier = BeanCopyToolsBuilder.create(WrapInRoute.class, Route.class,
			null, WrapInRoute.Excludes);

	static BeanCopyTools<WrapInProcess, Process> processInCopier = BeanCopyToolsBuilder.create(WrapInProcess.class,
			Process.class, null, WrapInProcess.Excludes);

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

	void delete_attachment(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.attachment().listWithProcessWithCompleted(process.getId(), false)
				: business.attachment().listWithProcess(process.getId());
		this.delete_batch(business.entityManagerContainer(), Attachment.class, ids);
	}

	// void delete_attachmentLog(Business business, Process process,
	// boolean onlyRemoveNotCompleted)
	// throws Exception {
	// List<String> ids = onlyRemoveNotCompleted
	// ? business.attachmentLog().listWithProcessWithCompleted(process.getId(),
	// false)
	// : business.attachmentLog().listWithProcess(process.getId());
	// this.delete_batch(business.entityManagerContainer(), AttachmentLog.class,
	// ids);
	// }

	void delete_dataItem(Business business, Process process, boolean onlyRemoveNotCompleted) throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.dataItem().listWithProcessWithCompleted(process.getId(), false)
				: business.dataItem().listWithProcess(process.getId());
		EntityManagerContainer emc = business.entityManagerContainer();
		for (List<String> list : ListTools.batch(ids, 1000)) {
			/** 需要级联删除LobItem */
			emc.beginTransaction(DataItem.class);
			emc.beginTransaction(DataLobItem.class);
			for (DataItem o : emc.list(DataItem.class, list)) {
				if (o.isLobItem()) {
					DataLobItem lob = emc.find(o.getLobItem(), DataLobItem.class);
					if (null != lob) {
						emc.remove(lob);
					}
				}
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

	WrapOutProcessComplex complexProcess(Business business, Process process) throws Exception {
		WrapOutProcessComplex wrap = complexProcessOutCopier.copy(process);
		wrap.setAgentList(agentOutCopier.copy(business.entityManagerContainer().list(Agent.class,
				business.agent().listWithProcess(process.getId()))));
		wrap.setBegin(beginOutCopier.copy(
				business.entityManagerContainer().find(business.begin().getWithProcess(process.getId()), Begin.class)));
		wrap.setCancelList(cancelOutCopier.copy(business.entityManagerContainer().list(Cancel.class,
				business.cancel().listWithProcess(process.getId()))));
		wrap.setChoiceList(choiceOutCopier.copy(business.entityManagerContainer().list(Choice.class,
				business.choice().listWithProcess(process.getId()))));
		wrap.setDelayList(delayOutCopier.copy(business.entityManagerContainer().list(Delay.class,
				business.delay().listWithProcess(process.getId()))));
		wrap.setEmbedList(embedOutCopier.copy(business.entityManagerContainer().list(Embed.class,
				business.embed().listWithProcess(process.getId()))));
		wrap.setEndList(endOutCopier.copy(
				business.entityManagerContainer().list(End.class, business.end().listWithProcess(process.getId()))));
		wrap.setInvokeList(invokeOutCopier.copy(business.entityManagerContainer().list(Invoke.class,
				business.invoke().listWithProcess(process.getId()))));
		wrap.setManualList(manualOutCopier.copy(business.entityManagerContainer().list(Manual.class,
				business.manual().listWithProcess(process.getId()))));
		wrap.setMergeList(mergeOutCopier.copy(business.entityManagerContainer().list(Merge.class,
				business.merge().listWithProcess(process.getId()))));
		wrap.setMessageList(messageOutCopier.copy(business.entityManagerContainer().list(Message.class,
				business.message().listWithProcess(process.getId()))));
		wrap.setParallelList(parallelOutCopier.copy(business.entityManagerContainer().list(Parallel.class,
				business.parallel().listWithProcess(process.getId()))));
		wrap.setServiceList(serviceOutCopier.copy(business.entityManagerContainer().list(Service.class,
				business.service().listWithProcess(process.getId()))));
		wrap.setSplitList(splitOutCopier.copy(business.entityManagerContainer().list(Split.class,
				business.split().listWithProcess(process.getId()))));
		wrap.setRouteList(routeOutCopier.copy(business.entityManagerContainer().list(Route.class,
				business.route().listWithProcess(process.getId()))));
		return wrap;

	}

	<T extends JpaObject> T wrapInJpaList(Object wrap, List<T> list) throws Exception {
		for (T t : list) {
			if (t.getId().equalsIgnoreCase(PropertyUtils.getProperty(wrap, "id").toString())) {
				return t;
			}
		}
		return null;
	}

	@MethodDescribe("判断实体在Wrap对象列表中是否有同样id的对象.")
	<T> T jpaInWrapList(JpaObject jpa, List<T> list) throws Exception {
		for (T t : list) {
			if (PropertyUtils.getProperty(t, "id").toString().equalsIgnoreCase(jpa.getId())) {
				return t;
			}
		}
		return null;
	}

	List<Agent> create_agent(List<WrapInAgent> wraps, Process process) throws Exception {
		List<Agent> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInAgent w : wraps) {
				Agent o = new Agent();
				o.setProcess(process.getId());
				agentInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	Begin create_begin(WrapInBegin wrap, Process process) throws Exception {
		Begin o = null;
		if (wrap != null) {
			o = new Begin();
			o.setProcess(process.getId());
			beginInCopier.copy(wrap, o);
			o.setDistributeFactor(process.getDistributeFactor());
		}
		return o;
	}

	List<Cancel> create_cancel(List<WrapInCancel> wraps, Process process) throws Exception {
		List<Cancel> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInCancel w : wraps) {
				Cancel o = new Cancel();
				o.setProcess(process.getId());
				cancelInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Choice> create_choice(List<WrapInChoice> wraps, Process process) throws Exception {
		List<Choice> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInChoice w : wraps) {
				Choice o = new Choice();
				o.setProcess(process.getId());
				choiceInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Delay> create_delay(List<WrapInDelay> wraps, Process process) throws Exception {
		List<Delay> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInDelay w : wraps) {
				Delay o = new Delay();
				o.setProcess(process.getId());
				delayInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Embed> create_embed(List<WrapInEmbed> wraps, Process process) throws Exception {
		List<Embed> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInEmbed w : wraps) {
				Embed o = new Embed();
				o.setProcess(process.getId());
				embedInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<End> create_end(List<WrapInEnd> wraps, Process process) throws Exception {
		List<End> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInEnd w : wraps) {
				End o = new End();
				o.setProcess(process.getId());
				endInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Invoke> create_invoke(List<WrapInInvoke> wraps, Process process) throws Exception {
		List<Invoke> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInInvoke w : wraps) {
				Invoke o = new Invoke();
				o.setProcess(process.getId());
				invokeInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Manual> create_manual(List<WrapInManual> wraps, Process process) throws Exception {
		List<Manual> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInManual w : wraps) {
				Manual o = new Manual();
				o.setProcess(process.getId());
				manualInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Merge> create_merge(List<WrapInMerge> wraps, Process process) throws Exception {
		List<Merge> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInMerge w : wraps) {
				Merge o = new Merge();
				o.setProcess(process.getId());
				mergeInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Message> create_message(List<WrapInMessage> wraps, Process process) throws Exception {
		List<Message> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInMessage w : wraps) {
				Message o = new Message();
				o.setProcess(process.getId());
				messageInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Parallel> create_parallel(List<WrapInParallel> wraps, Process process) throws Exception {
		List<Parallel> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInParallel w : wraps) {
				Parallel o = new Parallel();
				o.setProcess(process.getId());
				parallelInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Route> create_route(List<WrapInRoute> wraps, Process process) throws Exception {
		List<Route> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInRoute w : wraps) {
				Route o = new Route();
				o.setProcess(process.getId());
				routeInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Service> create_service(List<WrapInService> wraps, Process process) throws Exception {
		List<Service> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInService w : wraps) {
				Service o = new Service();
				o.setProcess(process.getId());
				serviceInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	List<Split> create_split(List<WrapInSplit> wraps, Process process) throws Exception {
		List<Split> list = new ArrayList<>();
		if (null != wraps) {
			for (WrapInSplit w : wraps) {
				Split o = new Split();
				o.setProcess(process.getId());
				splitInCopier.copy(w, o);
				o.setDistributeFactor(process.getDistributeFactor());
				list.add(o);
			}
		}
		return list;
	}

	void update_agent(Business business, List<WrapInAgent> wraps, Process process) throws Exception {
		List<String> ids = business.agent().listWithProcess(process.getId());
		List<Agent> os = business.entityManagerContainer().list(Agent.class, ids);
		for (Agent o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInAgent w : wraps) {
				Agent o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Agent();
					o.setProcess(process.getId());
					agentInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					agentInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_begin(Business business, WrapInBegin wrap, Process process) throws Exception {
		String id = business.begin().getWithProcess(process.getId());
		Begin o = business.entityManagerContainer().find(id, Begin.class);
		if (null != wrap) {
			if (!o.getId().equalsIgnoreCase(wrap.getId())) {
				business.entityManagerContainer().get(Begin.class).remove(o);
				o = new Begin();
				o.setProcess(process.getId());
				beginInCopier.copy(wrap, o);
				o.setDistributeFactor(process.getDistributeFactor());
				business.entityManagerContainer().persist(o, CheckPersistType.all);
			} else {
				beginInCopier.copy(wrap, o);
				business.entityManagerContainer().check(o, CheckPersistType.all);
			}
		}
	}

	void update_cancel(Business business, List<WrapInCancel> wraps, Process process) throws Exception {
		List<String> ids = business.cancel().listWithProcess(process.getId());
		List<Cancel> os = business.entityManagerContainer().list(Cancel.class, ids);
		for (Cancel o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInCancel w : wraps) {
				Cancel o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Cancel();
					o.setProcess(process.getId());
					cancelInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					cancelInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_choice(Business business, List<WrapInChoice> wraps, Process process) throws Exception {
		List<String> ids = business.choice().listWithProcess(process.getId());
		List<Choice> os = business.entityManagerContainer().list(Choice.class, ids);
		for (Choice o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInChoice w : wraps) {
				Choice o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Choice();
					o.setProcess(process.getId());
					choiceInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					choiceInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_delay(Business business, List<WrapInDelay> wraps, Process process) throws Exception {
		List<String> ids = business.delay().listWithProcess(process.getId());
		List<Delay> os = business.entityManagerContainer().list(Delay.class, ids);
		for (Delay o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInDelay w : wraps) {
				Delay o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Delay();
					o.setProcess(process.getId());
					delayInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					delayInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_embed(Business business, List<WrapInEmbed> wraps, Process process) throws Exception {
		List<String> ids = business.embed().listWithProcess(process.getId());
		List<Embed> os = business.entityManagerContainer().list(Embed.class, ids);
		for (Embed o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInEmbed w : wraps) {
				Embed o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Embed();
					o.setProcess(process.getId());
					embedInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					embedInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_end(Business business, List<WrapInEnd> wraps, Process process) throws Exception {
		List<String> ids = business.end().listWithProcess(process.getId());
		List<End> os = business.entityManagerContainer().list(End.class, ids);
		for (End o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInEnd w : wraps) {
				End o = wrapInJpaList(w, os);
				if (null == o) {
					o = new End();
					o.setProcess(process.getId());
					endInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					endInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_invoke(Business business, List<WrapInInvoke> wraps, Process process) throws Exception {
		List<String> ids = business.invoke().listWithProcess(process.getId());
		List<Invoke> os = business.entityManagerContainer().list(Invoke.class, ids);
		for (Invoke o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInInvoke w : wraps) {
				Invoke o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Invoke();
					o.setProcess(process.getId());
					invokeInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					invokeInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_manual(Business business, List<WrapInManual> wraps, Process process) throws Exception {
		List<String> ids = business.manual().listWithProcess(process.getId());
		List<Manual> os = business.entityManagerContainer().list(Manual.class, ids);
		for (Manual o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInManual w : wraps) {
				Manual o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Manual();
					o.setProcess(process.getId());
					manualInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					manualInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_merge(Business business, List<WrapInMerge> wraps, Process process) throws Exception {
		List<String> ids = business.merge().listWithProcess(process.getId());
		List<Merge> os = business.entityManagerContainer().list(Merge.class, ids);
		for (Merge o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInMerge w : wraps) {
				Merge o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Merge();
					o.setProcess(process.getId());
					mergeInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					mergeInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_message(Business business, List<WrapInMessage> wraps, Process process) throws Exception {
		List<String> ids = business.message().listWithProcess(process.getId());
		List<Message> os = business.entityManagerContainer().list(Message.class, ids);
		for (Message o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInMessage w : wraps) {
				Message o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Message();
					o.setProcess(process.getId());
					messageInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					messageInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_parallel(Business business, List<WrapInParallel> wraps, Process process) throws Exception {
		List<String> ids = business.parallel().listWithProcess(process.getId());
		List<Parallel> os = business.entityManagerContainer().list(Parallel.class, ids);
		for (Parallel o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInParallel w : wraps) {
				Parallel o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Parallel();
					o.setProcess(process.getId());
					parallelInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					parallelInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_route(Business business, List<WrapInRoute> wraps, Process process) throws Exception {
		List<String> ids = business.route().listWithProcess(process.getId());
		List<Route> os = business.entityManagerContainer().list(Route.class, ids);
		for (Route o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInRoute w : wraps) {
				Route o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Route();
					o.setProcess(process.getId());
					routeInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					routeInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_service(Business business, List<WrapInService> wraps, Process process) throws Exception {
		List<String> ids = business.service().listWithProcess(process.getId());
		List<Service> os = business.entityManagerContainer().list(Service.class, ids);
		for (Service o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInService w : wraps) {
				Service o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Service();
					o.setProcess(process.getId());
					serviceInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					serviceInCopier.copy(w, o);
					business.entityManagerContainer().check(o, CheckPersistType.all);
				}
			}
		}
	}

	void update_split(Business business, List<WrapInSplit> wraps, Process process) throws Exception {
		List<String> ids = business.split().listWithProcess(process.getId());
		List<Split> os = business.entityManagerContainer().list(Split.class, ids);
		for (Split o : os) {
			if (null == jpaInWrapList(o, wraps)) {
				business.entityManagerContainer().remove(o);
			}
		}
		if (null != wraps) {
			for (WrapInSplit w : wraps) {
				Split o = wrapInJpaList(w, os);
				if (null == o) {
					o = new Split();
					o.setProcess(process.getId());
					splitInCopier.copy(w, o);
					o.setDistributeFactor(process.getDistributeFactor());
					business.entityManagerContainer().persist(o, CheckPersistType.all);
				} else {
					splitInCopier.copy(w, o);
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
		if (StringUtils.isNoneEmpty(str)) {
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
		ApplicationCache.notify(Condition.class);
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

}
