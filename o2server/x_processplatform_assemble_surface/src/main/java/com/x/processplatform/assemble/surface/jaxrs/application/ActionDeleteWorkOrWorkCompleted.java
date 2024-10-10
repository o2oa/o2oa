package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.*;
import com.x.processplatform.core.entity.element.*;
import com.x.query.core.entity.Item;
import org.apache.commons.collections4.ListUtils;


import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

class ActionDeleteWorkOrWorkCompleted extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, boolean onlyRemoveNotCompleted)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			if(!(effectivePerson.isManager() || business.organization().person().hasRole(effectivePerson,
					OrganizationDefinition.ProcessPlatformManager))){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			Application application = emc.find(flag, Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(flag,Application.class);
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

			emc.remove(application);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(application.getId());
			result.setData(wo);

			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 5079652219303230570L;

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
