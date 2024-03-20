package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DocumentVersion;
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
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Publish;
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
			Long workConut = emc.countEqual(Work.class, Work.application_FIELDNAME, application.getId());
			if (workConut > 0) {
				throw new ExceptionWorkProcessing(application.getName(), application.getId(), workConut);
			}

			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionInsufficientPermission(effectivePerson.getDistinguishedName());
			}
			/** 先删除content内容,删除内容的时候在方法内进行批量删除,在方法内部进行beginTransaction和commit */
			this.deleteTask(business, application);
			this.deleteTaskCompleted(business, application, onlyRemoveNotCompleted);
			this.deleteRead(business, application);
			this.deleteReadCompleted(business, application, onlyRemoveNotCompleted);
			this.deleteReview(business, application, onlyRemoveNotCompleted);
			this.deleteAttachment(business, application, onlyRemoveNotCompleted);
			this.deleteDataItem(business, application, onlyRemoveNotCompleted);
			this.deleteSerialNumber(business, application);
			this.deleteRecord(business, application, onlyRemoveNotCompleted);
			this.deleteDocumentVersion(business, application);
			this.deleteWork(business, application);
			if (!onlyRemoveNotCompleted) {
				this.deleteWorkCompleted(business, application);
			}
			this.deleteWorkLog(business, application, onlyRemoveNotCompleted);
			/** 删除数据字典和数据字典数据 */
			this.deleteApplicationDictItem(business, application);
			this.deleteApplicationDict(business, application);
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
			emc.beginTransaction(Parallel.class);
			emc.beginTransaction(Publish.class);
			emc.beginTransaction(Service.class);
			emc.beginTransaction(Split.class);
			emc.beginTransaction(Route.class);
			emc.beginTransaction(FormField.class);
			emc.beginTransaction(Form.class);
			emc.beginTransaction(Script.class);
			emc.beginTransaction(SerialNumber.class);
			emc.beginTransaction(File.class);
			for (String str : business.process().listWithApplication(id, false)) {
				/** 流程 1种 */
				Process process = emc.find(str, Process.class);
				/** 流程Activity 14种 */
				this.deleteAgent(business, process);
				this.deleteBegin(business, process);
				this.deleteCancel(business, process);
				this.deleteChoice(business, process);
				this.deleteDelay(business, process);
				this.deleteEmbed(business, process);
				this.deleteEnd(business, process);
				this.deleteInvoke(business, process);
				this.deleteManual(business, process);
				this.deleteMerge(business, process);
				this.deleteParallel(business, process);
				this.deletePublish(business, process);
				this.deleteService(business, process);
				this.deleteSplit(business, process);
				/** 路由 1种 */
				this.deleteRoute(business, process);
				emc.remove(process);
			}
			/** 应用内容 6种 */
			this.deleteFormField(business, application);
			this.deleteForm(business, application);
			this.deleteScript(business, application);
			this.deleteFile(business, application);
			this.deleteSerialNumber(business, application);
			/** 应用本体 1种 */
			emc.remove(application);
			emc.commit();
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
			CacheManager.notify(Service.class);
			CacheManager.notify(Split.class);
			CacheManager.notify(Route.class);
			CacheManager.notify(FormField.class);
			CacheManager.notify(Form.class);
			CacheManager.notify(File.class);
			CacheManager.notify(Script.class);
			CacheManager.notify(SerialNumber.class);
			Wo wo = new Wo();
			wo.setId(application.getId());
			result.setData(wo);
			MessageFactory.application_delete(application);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 5079652219303230570L;

	}

	private void deleteAgent(Business business, Process process) throws Exception {
		for (String str : business.agent().listWithProcess(process.getId())) {
			Agent o = business.entityManagerContainer().find(str, Agent.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteBegin(Business business, Process process) throws Exception {
		String str = business.begin().getWithProcess(process.getId());
		if (StringUtils.isNotEmpty(str)) {
			Begin o = business.entityManagerContainer().find(str, Begin.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteCancel(Business business, Process process) throws Exception {
		for (String str : business.cancel().listWithProcess(process.getId())) {
			Cancel o = business.entityManagerContainer().find(str, Cancel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteChoice(Business business, Process process) throws Exception {
		for (String str : business.choice().listWithProcess(process.getId())) {
			Choice o = business.entityManagerContainer().find(str, Choice.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteDelay(Business business, Process process) throws Exception {
		for (String str : business.delay().listWithProcess(process.getId())) {
			Delay o = business.entityManagerContainer().find(str, Delay.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteEmbed(Business business, Process process) throws Exception {
		for (String str : business.embed().listWithProcess(process.getId())) {
			Embed o = business.entityManagerContainer().find(str, Embed.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteEnd(Business business, Process process) throws Exception {
		for (String str : business.end().listWithProcess(process.getId())) {
			End o = business.entityManagerContainer().find(str, End.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteInvoke(Business business, Process process) throws Exception {
		for (String str : business.invoke().listWithProcess(process.getId())) {
			Invoke o = business.entityManagerContainer().find(str, Invoke.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteManual(Business business, Process process) throws Exception {
		for (String str : business.manual().listWithProcess(process.getId())) {
			Manual o = business.entityManagerContainer().find(str, Manual.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteMerge(Business business, Process process) throws Exception {
		for (String str : business.merge().listWithProcess(process.getId())) {
			Merge o = business.entityManagerContainer().find(str, Merge.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteParallel(Business business, Process process) throws Exception {
		for (String str : business.parallel().listWithProcess(process.getId())) {
			Parallel o = business.entityManagerContainer().find(str, Parallel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deletePublish(Business business, Process process) throws Exception {
		for (String str : business.publish().listWithProcess(process.getId())) {
			Publish o = business.entityManagerContainer().find(str, Publish.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteRoute(Business business, Process process) throws Exception {
		for (String str : business.route().listWithProcess(process.getId())) {
			Route o = business.entityManagerContainer().find(str, Route.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteService(Business business, Process process) throws Exception {
		for (String str : business.service().listWithProcess(process.getId())) {
			Service o = business.entityManagerContainer().find(str, Service.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteSplit(Business business, Process process) throws Exception {
		for (String str : business.split().listWithProcess(process.getId())) {
			Split o = business.entityManagerContainer().find(str, Split.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteForm(Business business, Application application) throws Exception {
		for (String str : business.form().listWithApplication(application.getId())) {
			Form o = business.entityManagerContainer().find(str, Form.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteFormField(Business business, Application application) throws Exception {
		for (String str : business.formField().listWithApplication(application.getId())) {
			FormField o = business.entityManagerContainer().find(str, FormField.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteScript(Business business, Application application) throws Exception {
		for (String str : business.script().listWithApplication(application.getId())) {
			Script o = business.entityManagerContainer().find(str, Script.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteFile(Business business, Application application) throws Exception {
		for (File o : business.entityManagerContainer().listEqual(File.class, File.application_FIELDNAME,
				application.getId())) {
			business.entityManagerContainer().remove(o);
		}
	}

	private void deleteApplicationDict(Business business, Application application) throws Exception {
		List<String> ids = business.applicationDict().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), ApplicationDict.class, ids);
	}

	private void deleteApplicationDictItem(Business business, Application application) throws Exception {
		List<String> ids = business.applicationDictItem().listWithApplication(application.getId());
		EntityManagerContainer emc = business.entityManagerContainer();
		for (List<String> list : ListTools.batch(ids, 1000)) {
			emc.beginTransaction(ApplicationDictItem.class);
			for (ApplicationDictItem o : emc.list(ApplicationDictItem.class, list)) {
				emc.remove(o);
			}
			emc.commit();
		}
	}

	private void deleteBatch(EntityManagerContainer emc, Class<? extends JpaObject> clz, List<String> ids)
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

	private void deleteAttachment(Business business, Application application, boolean onlyRemoveNotCompleted)
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

	private void deleteDataItem(Business business, Application application, boolean onlyRemoveNotCompleted)
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

	private void deleteSerialNumber(Business business, Application application) throws Exception {
		List<String> ids = business.serialNumber().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), SerialNumber.class, ids);
	}

	private void deleteTask(Business business, Application application) throws Exception {
		List<String> ids = business.task().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), Task.class, ids);
	}

	private void deleteWork(Business business, Application application) throws Exception {
		List<String> ids = business.work().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), Work.class, ids);
	}

	private void deleteRecord(Business business, Application application, boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.record().listWithApplicationWithCompleted(application.getId(), false)
				: business.record().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), Record.class, ids);
	}

	private void deleteDocumentVersion(Business business, Application application) throws Exception {
		List<String> ids = business.entityManagerContainer().idsEqual(DocumentVersion.class,
				DocumentVersion.application_FIELDNAME, application.getId());
		this.deleteBatch(business.entityManagerContainer(), DocumentVersion.class, ids);
	}

	private void deleteWorkCompleted(Business business, Application application) throws Exception {
		List<String> ids = business.workCompleted().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), WorkCompleted.class, ids);
	}

	private void deleteWorkLog(Business business, Application application, boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.workLog().listWithApplicationWithCompleted(application.getId(), false)
				: business.workLog().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), WorkLog.class, ids);
	}

	private void deleteTaskCompleted(Business business, Application application, boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.taskCompleted().listWithApplicationWithCompleted(application.getId(), false)
				: business.taskCompleted().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), TaskCompleted.class, ids);
	}

	private void deleteRead(Business business, Application application) throws Exception {
		List<String> ids = business.read().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), Read.class, ids);
	}

	private void deleteReadCompleted(Business business, Application application, boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.readCompleted().listWithApplicationWithCompleted(application.getId(), false)
				: business.readCompleted().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), ReadCompleted.class, ids);
	}

	private void deleteReview(Business business, Application application, boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.review().listWithApplicationWithCompleted(application.getId(), false)
				: business.review().listWithApplication(application.getId());
		this.deleteBatch(business.entityManagerContainer(), Review.class, ids);
	}

}
