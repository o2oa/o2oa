package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
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
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.File;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormField;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.query.core.entity.Item;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, boolean onlyRemoveNotCompleted)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = emc.find(id, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(id);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionInsufficientPermission(effectivePerson.getDistinguishedName());
			}
			/** 先删除content内容,删除内容的时候在方法内进行批量删除,在方法内部进行beginTransaction和commit */
			this.delete_task(business, application);
			this.delete_taskCompleted(business, application, onlyRemoveNotCompleted);
			this.delete_read(business, application);
			this.delete_readCompleted(business, application, onlyRemoveNotCompleted);
			this.delete_review(business, application, onlyRemoveNotCompleted);
			this.delete_hint(business, application);
			this.delete_attachment(business, application, onlyRemoveNotCompleted);
			this.delete_dataItem(business, application, onlyRemoveNotCompleted);
			this.delete_serialNumber(business, application);
			this.delete_work(business, application);
			if (!onlyRemoveNotCompleted) {
				this.delete_workCompleted(business, application);
			}
			this.delete_workLog(business, application, onlyRemoveNotCompleted);
			/** 删除数据字典和数据字典数据 */
			this.delete_applicationDictItem(business, application);
			this.delete_applicationDict(business, application);
			/** 再删除设计,开启事务进行统一删除 */
			emc.beginTransaction(Application.class);
			emc.beginTransaction(Process.class);
			emc.beginTransaction(Agent.class);
			emc.beginTransaction(Begin.class);
			emc.beginTransaction(Cancel.class);
			emc.beginTransaction(Choice.class);
			emc.beginTransaction(Delay.class);
			emc.beginTransaction(Embed.class);
			emc.beginTransaction(End.class);
			emc.beginTransaction(Invoke.class);
			emc.beginTransaction(Manual.class);
			emc.beginTransaction(Merge.class);
			emc.beginTransaction(Message.class);
			emc.beginTransaction(Parallel.class);
			emc.beginTransaction(Service.class);
			emc.beginTransaction(Split.class);
			emc.beginTransaction(Route.class);
			emc.beginTransaction(FormField.class);
			emc.beginTransaction(Form.class);
			emc.beginTransaction(Script.class);
			emc.beginTransaction(SerialNumber.class);
			emc.beginTransaction(QueryView.class);
			emc.beginTransaction(QueryStat.class);
			emc.beginTransaction(File.class);
			for (String str : business.process().listWithApplication(id)) {
				/** 流程 1种 */
				Process process = emc.find(str, Process.class);
				/** 流程Activity 14种 */
				this.delete_agent(business, process);
				this.delete_begin(business, process);
				this.delete_cancel(business, process);
				this.delete_choice(business, process);
				this.delete_delay(business, process);
				this.delete_embed(business, process);
				this.delete_end(business, process);
				this.delete_invoke(business, process);
				this.delete_manual(business, process);
				this.delete_merge(business, process);
				this.delete_message(business, process);
				this.delete_parallel(business, process);
				this.delete_service(business, process);
				this.delete_split(business, process);
				/** 路由 1种 */
				this.delete_route(business, process);
				emc.remove(process);
			}
			/** 应用内容 6种 */
			this.delete_formField(business, application);
			this.delete_form(business, application);
			this.delete_script(business, application);
			this.delete_file(business, application);
			this.delete_serialNumber(business, application);
			this.delete_queryView(business, application);
			this.delete_queryStat(business, application);
			/** 应用本体 1种 */
			emc.remove(application);
			emc.commit();
			ApplicationCache.notify(Application.class);
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
			ApplicationCache.notify(Service.class);
			ApplicationCache.notify(Split.class);
			ApplicationCache.notify(Route.class);
			ApplicationCache.notify(FormField.class);
			ApplicationCache.notify(Form.class);
			ApplicationCache.notify(File.class);
			ApplicationCache.notify(Script.class);
			ApplicationCache.notify(SerialNumber.class);
			ApplicationCache.notify(QueryView.class);
			ApplicationCache.notify(QueryStat.class);
			Wo wo = new Wo();
			wo.setId(application.getId());
			result.setData(wo);
			MessageFactory.application_delete(application);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	private void delete_agent(Business business, Process process) throws Exception {
		for (String str : business.agent().listWithProcess(process.getId())) {
			Agent o = business.entityManagerContainer().find(str, Agent.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_begin(Business business, Process process) throws Exception {
		String str = business.begin().getWithProcess(process.getId());
		if (StringUtils.isNotEmpty(str)) {
			Begin o = business.entityManagerContainer().find(str, Begin.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_cancel(Business business, Process process) throws Exception {
		for (String str : business.cancel().listWithProcess(process.getId())) {
			Cancel o = business.entityManagerContainer().find(str, Cancel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_choice(Business business, Process process) throws Exception {
		for (String str : business.choice().listWithProcess(process.getId())) {
			Choice o = business.entityManagerContainer().find(str, Choice.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_delay(Business business, Process process) throws Exception {
		for (String str : business.delay().listWithProcess(process.getId())) {
			Delay o = business.entityManagerContainer().find(str, Delay.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_embed(Business business, Process process) throws Exception {
		for (String str : business.embed().listWithProcess(process.getId())) {
			Embed o = business.entityManagerContainer().find(str, Embed.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_end(Business business, Process process) throws Exception {
		for (String str : business.end().listWithProcess(process.getId())) {
			End o = business.entityManagerContainer().find(str, End.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_invoke(Business business, Process process) throws Exception {
		for (String str : business.invoke().listWithProcess(process.getId())) {
			Invoke o = business.entityManagerContainer().find(str, Invoke.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_manual(Business business, Process process) throws Exception {
		for (String str : business.manual().listWithProcess(process.getId())) {
			Manual o = business.entityManagerContainer().find(str, Manual.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_merge(Business business, Process process) throws Exception {
		for (String str : business.merge().listWithProcess(process.getId())) {
			Merge o = business.entityManagerContainer().find(str, Merge.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_message(Business business, Process process) throws Exception {
		for (String str : business.message().listWithProcess(process.getId())) {
			Message o = business.entityManagerContainer().find(str, Message.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_parallel(Business business, Process process) throws Exception {
		for (String str : business.parallel().listWithProcess(process.getId())) {
			Parallel o = business.entityManagerContainer().find(str, Parallel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_route(Business business, Process process) throws Exception {
		for (String str : business.route().listWithProcess(process.getId())) {
			Route o = business.entityManagerContainer().find(str, Route.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_service(Business business, Process process) throws Exception {
		for (String str : business.service().listWithProcess(process.getId())) {
			Service o = business.entityManagerContainer().find(str, Service.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_split(Business business, Process process) throws Exception {
		for (String str : business.split().listWithProcess(process.getId())) {
			Split o = business.entityManagerContainer().find(str, Split.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_form(Business business, Application application) throws Exception {
		for (String str : business.form().listWithApplication(application.getId())) {
			Form o = business.entityManagerContainer().find(str, Form.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_formField(Business business, Application application) throws Exception {
		for (String str : business.formField().listWithApplication(application.getId())) {
			FormField o = business.entityManagerContainer().find(str, FormField.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_script(Business business, Application application) throws Exception {
		for (String str : business.script().listWithApplication(application.getId())) {
			Script o = business.entityManagerContainer().find(str, Script.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_file(Business business, Application application) throws Exception {
		for (File o : business.entityManagerContainer().listEqual(File.class, File.application_FIELDNAME,
				application.getId())) {
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_applicationDict(Business business, Application application) throws Exception {
		List<String> ids = business.applicationDict().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), ApplicationDict.class, ids);
	}

	private void delete_applicationDictItem(Business business, Application application) throws Exception {
		List<String> ids = business.applicationDictItem().listWithApplication(application.getId());
		EntityManagerContainer emc = business.entityManagerContainer();
		for (List<String> list : ListTools.batch(ids, 1000)) {
			emc.beginTransaction(ApplicationDictItem.class);
			// emc.beginTransaction(ApplicationDictLobItem.class);
			for (ApplicationDictItem o : emc.list(ApplicationDictItem.class, list)) {
				emc.remove(o);
			}
			emc.commit();
		}
	}

	private void delete_batch(EntityManagerContainer emc, Class<? extends JpaObject> clz, List<String> ids)
			throws Exception {
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

	private void delete_attachment(Business business, Application application, boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.attachment().listWithApplicationWithCompleted(application.getId(), false)
				: business.attachment().listWithApplication(application.getId());
		/** 附件需要单独处理删除 */
		EntityManagerContainer emc = business.entityManagerContainer();
		for (List<String> list : ListTools.batch(ids, 1000)) {
			emc.beginTransaction(Attachment.class);
			for (Attachment o : business.entityManagerContainer().list(Attachment.class, list)) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class,
						o.getStorage());
				if (null != mapping) {
					o.deleteContent(mapping);
				}
				emc.remove(o);
			}
			emc.commit();
		}
	}

	private void delete_dataItem(Business business, Application application, boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> jobs = business.work().listJobWithApplication(application.getId());
		if (!onlyRemoveNotCompleted) {
			jobs = ListUtils.union(jobs, business.workCompleted().listJobWithApplication(application.getId()));
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

	private void delete_serialNumber(Business business, Application application) throws Exception {
		List<String> ids = business.serialNumber().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), SerialNumber.class, ids);
	}

	private void delete_queryView(Business business, Application application) throws Exception {
		List<String> ids = business.queryView().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), QueryView.class, ids);
	}

	private void delete_queryStat(Business business, Application application) throws Exception {
		List<String> ids = business.queryStat().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), QueryStat.class, ids);
	}

	private void delete_task(Business business, Application application) throws Exception {
		List<String> ids = business.task().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Task.class, ids);
	}

	private void delete_work(Business business, Application application) throws Exception {
		List<String> ids = business.work().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Work.class, ids);
	}

	private void delete_workCompleted(Business business, Application application) throws Exception {
		List<String> ids = business.workCompleted().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), WorkCompleted.class, ids);
	}

	private void delete_workLog(Business business, Application application, Boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.workLog().listWithApplicationWithCompleted(application.getId(), false)
				: business.workLog().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), WorkLog.class, ids);
	}

	private void delete_taskCompleted(Business business, Application application, Boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.taskCompleted().listWithApplicationWithCompleted(application.getId(), false)
				: business.taskCompleted().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), TaskCompleted.class, ids);
	}

	private void delete_read(Business business, Application application) throws Exception {
		List<String> ids = business.read().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Read.class, ids);
	}

	private void delete_readCompleted(Business business, Application application, Boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.readCompleted().listWithApplicationWithCompleted(application.getId(), false)
				: business.readCompleted().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), ReadCompleted.class, ids);
	}

	private void delete_review(Business business, Application application, Boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.review().listWithApplicationWithCompleted(application.getId(), false)
				: business.review().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Review.class, ids);
	}

	private void delete_hint(Business business, Application application) throws Exception {
		List<String> ids = business.hint().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Hint.class, ids);
	}

}