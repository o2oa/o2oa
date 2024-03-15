package com.x.processplatform.service.processing.jaxrs.work;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkStatus;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;
import com.x.processplatform.core.express.ProcessingAttributes;
import com.x.processplatform.core.express.WorkDataHelper;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWi;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWi.WiAttachment;
import com.x.processplatform.core.express.service.processing.jaxrs.work.ActionAssignCreateWo;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.MessageFactory;
import com.x.processplatform.service.processing.Processing;
import com.x.processplatform.service.processing.ThisApplication;

/**
 * 创建处于start状态的work 此方法不需要进入队列运行
 *
 * @author Rui
 *
 *         此方法不需要推入线程池运行
 */
class ActionAssignCreate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionAssignCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Work work = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> applicationIds = listApplication(business, wi.getApplication());
			if (ListTools.isEmpty(applicationIds)) {
				throw new ExceptionEntityNotExist(wi.getApplication(), Application.class);
			}
			Process process = getProcess(business, applicationIds, wi.getProcess());
			Application application = business.element().get(process.getApplication(), Application.class);
			Begin begin = business.element().getBeginWithProcess(process.getId());
			work = create(application, process, begin);
			updateWork(business, work, wi);
			// 通过赋值调用的是不能被作为草稿删除的
			work.setDataChanged(true);
			updateAttachment(business, effectivePerson, work, begin, wi);
			emc.beginTransaction(Work.class);
			emc.persist(work, CheckPersistType.all);
			if (null != wi.getData()) {
				WorkDataHelper workDataHelper = new WorkDataHelper(emc, work);
				workDataHelper.update(wi.getData());
			}
			emc.commit();
		}
		MessageFactory.work_create(work);
		if (BooleanUtils.isTrue(wi.getProcessing())) {
			ProcessingAttributes processingAttributes = new ProcessingAttributes();
			Processing p = new Processing(processingAttributes);
			p.processing(work.getId());
		}

		wo.setId(work.getId());
		wo.setJob(work.getJob());
		result.setData(wo);
		return result;
	}

	private void updateAttachment(Business business, EffectivePerson effectivePerson, Work work, Begin begin, Wi wi)
			throws Exception {
		if (ListTools.isNotEmpty(wi.getAttachmentList())) {
			EntityManagerContainer emc = business.entityManagerContainer();
			emc.beginTransaction(Attachment.class);
			// 这个attachmentList要手动初始化
			for (WiAttachment o : wi.getAttachmentList()) {
				StorageMapping fromMapping = ThisApplication.context().storageMappings().get(Attachment.class,
						o.getStorage());
				if (null == fromMapping) {
					throw new ExceptionFromMappingNotExist(o.getStorage());
				}
				Attachment fromAttachment = emc.find(o.getId(), Attachment.class);
				if (null == fromAttachment) {
					throw new ExceptionEntityNotExist(o.getId(), Attachment.class);
				}
				StorageMapping toMapping = ThisApplication.context().storageMappings().random(Attachment.class);
				if (null == toMapping) {
					throw new ExceptionToMappingNotExist(Attachment.class);
				}
				Attachment toAttachment = new Attachment(work, effectivePerson.getDistinguishedName(), o.getSite());
				toAttachment.setActivity(begin.getId());
				toAttachment.setActivityName(begin.getName());
				toAttachment.setActivityType(ActivityType.begin);
				toAttachment.setActivityToken(work.getActivityToken());
				copyAttachment(fromMapping, toMapping, fromAttachment, toAttachment, wi, o);
				emc.persist(toAttachment, CheckPersistType.all);
			}
		}
	}

	private void copyAttachment(StorageMapping fromMapping, StorageMapping toMapping, Attachment formAttachment,
			Attachment toAttachment, Wi wi, WiAttachment o) throws Exception {
		if (BooleanUtils.isTrue(wi.getAttachmentSoftCopy())) {
			toAttachment.setName(o.getName());
			toAttachment.setDeepPath(toMapping.getDeepPath());
			toAttachment.setExtension(StringUtils.lowerCase(StringUtils.substringAfterLast(o.getName(), ".")));
			toAttachment.setLength(formAttachment.getLength());
			toAttachment.setStorage(toMapping.getName());
			toAttachment.setLastUpdateTime(new Date());
			toAttachment.setFromJob(formAttachment.getJob());
			toAttachment.setFromId(formAttachment.getId());
			toAttachment.setFromPath(formAttachment.path());
			toAttachment.setOrderNumber(formAttachment.getOrderNumber());
		} else {
			toAttachment.setOrderNumber(formAttachment.getOrderNumber());
			toAttachment.saveContent(toMapping, o.readContent(fromMapping), o.getName(), Config.general().getStorageEncrypt());
		}
	}

	private void updateWork(Business business, Work work, Wi wi) throws Exception {
		String identityDn = business.organization().identity().get(wi.getIdentity());
		if (StringUtils.isEmpty(identityDn)) {
			throw new ExceptionIdentityNotExist(wi.getIdentity());
		}
		work.setTitle(wi.getTitle());
		work.setCreatorIdentity(identityDn);
		work.setCreatorPerson(business.organization().person().getWithIdentity(identityDn));
		work.setCreatorUnit(business.organization().unit().getWithIdentity(identityDn));
		work.setParentWork(wi.getParentWork());
		work.setParentJob(wi.getParentJob());
		if (StringUtils.isNotEmpty(work.getCreatorUnit())) {
			Unit unit = business.organization().unit().getObject(work.getCreatorUnit());
			work.setCreatorUnitLevelName(unit.getLevelName());
		}
	}

	public static class Wi extends ActionAssignCreateWi {

		private static final long serialVersionUID = 6368988907940597016L;

	}

	public static class Wo extends ActionAssignCreateWo {

		private static final long serialVersionUID = 1974733636474717701L;

	}

	private List<String> listApplication(Business business, String applicationFlag) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		Predicate p = cb.equal(root.get(Application_.name), applicationFlag);
		p = cb.or(p, cb.equal(root.get(Application_.alias), applicationFlag));
		p = cb.or(p, cb.equal(root.get(Application_.id), applicationFlag));
		p = cb.or(p, cb.equal(root.get(Application_.applicationCategory), applicationFlag));
		cq.select(root.get(Application_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	private Process getProcess(Business business, List<String> applicationIds, String processFlag) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Process> cq = cb.createQuery(Process.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.name), processFlag);
		p = cb.or(p, cb.equal(root.get(Process_.alias), processFlag));
		p = cb.or(p, cb.equal(root.get(Process_.id), processFlag));
		p = cb.and(p, root.get(Process_.application).in(applicationIds));
		p = cb.and(p, cb.or(cb.isTrue(root.get(Process_.editionEnable)), cb.isNull(root.get(Process_.editionEnable))));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Process_.editionNumber)));
		List<Process> list = em.createQuery(cq).getResultList();
		if (list.isEmpty()) {
			throw new ExceptionEntityNotExist(processFlag, Process.class);
		}
		return list.get(0);
	}

	private Work create(Application application, Process process, Begin begin) {
		Date now = new Date();
		Work work = new Work();
		// 标识工作数据未修改
		work.setDataChanged(false);
		work.setWorkThroughManual(false);
		work.setWorkCreateType(Work.WORKCREATETYPE_ASSIGN);
		work.setApplication(application.getId());
		work.setApplicationName(application.getName());
		work.setApplicationAlias(application.getAlias());
		work.setProcess(process.getId());
		work.setProcessName(process.getName());
		work.setProcessAlias(process.getAlias());
		work.setJob(StringTools.uniqueToken());
		work.setStartTime(now);
		work.setWorkStatus(WorkStatus.start);
		work.setDestinationActivity(begin.getId());
		work.setDestinationActivityType(ActivityType.begin);
		work.setDestinationRoute(null);
		work.setSplitting(false);
		work.setActivityToken(StringTools.uniqueToken());
		return work;
	}

}
