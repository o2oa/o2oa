package com.x.processplatform.assemble.designer.jaxrs.output;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.*;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.wrap.*;

class ActionSelect extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationFlag, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Application application = emc.flag(applicationFlag, Application.class);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			WrapProcessPlatform wrapApplication = this.get(business, application, wi);
			OutputCacheObject outputCacheObject = new OutputCacheObject();
			outputCacheObject.setName(application.getName());
			outputCacheObject.setApplication(wrapApplication);
			String flag = StringTools.uniqueToken();
			CacheKey cacheKey = new CacheKey(flag);
			CacheManager.put(cacheCategory, cacheKey, outputCacheObject);
			Wo wo = gson.fromJson(gson.toJson(wrapApplication), Wo.class);
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}
	}

	private WrapProcessPlatform get(Business business, Application application, Wi wi) throws Exception {
		WrapProcessPlatform wo = WrapProcessPlatform.outCopier.copy(application);
		wo.setProcessList(this.listProcess(business, application, wi));
		wo.setFormList(this.listForm(business, application, wi));
		wo.setApplicationDictList(this.listApplicationDict(business, application, wi));
		wo.setScriptList(
				WrapScript.outCopier.copy(business.entityManagerContainer().list(Script.class, wi.listScriptId())));
		wo.setFileList(WrapFile.outCopier.copy(business.entityManagerContainer().list(File.class, wi.listFileId())));
		return wo;
	}

	private List<WrapForm> listForm(Business business, Application application, Wi wi) throws Exception {
		List<WrapForm> wos = new ArrayList<>();
		for (String id : ListTools.trim(wi.listFormId(), true, true)) {
			Form form = business.entityManagerContainer().find(id, Form.class);
			if (null == form) {
				throw new ExceptionEntityNotExist(id, Process.class);
			}
			WrapForm wo = WrapForm.outCopier.copy(form);
			wo.setFormFieldList(WrapFormField.outCopier.copy(business.entityManagerContainer()
					.listEqual(FormField.class, FormField.form_FIELDNAME, form.getId())));
			wos.add(wo);
		}
		return wos;
	}

	private List<WrapProcess> listProcess(Business business, Application application, Wi wi) throws Exception {
		List<WrapProcess> wos = new ArrayList<>();
		for (String id : ListTools.trim(wi.listProcessId(), true, true)) {
			Process process = business.entityManagerContainer().find(id, Process.class);
			if (null == process) {
				throw new ExceptionEntityNotExist(id, Process.class);
			}
			WrapProcess wo = WrapProcess.outCopier.copy(process);
			wo.setAgentList(WrapAgent.outCopier.copy(business.entityManagerContainer().listEqual(Agent.class,
					Agent.process_FIELDNAME, process.getId())));
			List<Begin> begins = business.entityManagerContainer().listEqual(Begin.class, Begin.process_FIELDNAME,
					process.getId());
			if (ListTools.isNotEmpty(begins)) {
				wo.setBegin(WrapBegin.outCopier.copy(begins.get(0)));
			}
			wo.setCancelList(WrapCancel.outCopier.copy(business.entityManagerContainer().listEqual(Cancel.class,
					Cancel.process_FIELDNAME, process.getId())));
			wo.setChoiceList(WrapChoice.outCopier.copy(business.entityManagerContainer().listEqual(Choice.class,
					Choice.process_FIELDNAME, process.getId())));
			wo.setDelayList(WrapDelay.outCopier.copy(business.entityManagerContainer().listEqual(Delay.class,
					Delay.process_FIELDNAME, process.getId())));
			wo.setEmbedList(WrapEmbed.outCopier.copy(business.entityManagerContainer().listEqual(Embed.class,
					Embed.process_FIELDNAME, process.getId())));
			wo.setEndList(WrapEnd.outCopier.copy(
					business.entityManagerContainer().listEqual(End.class, End.process_FIELDNAME, process.getId())));
			wo.setInvokeList(WrapInvoke.outCopier.copy(business.entityManagerContainer().listEqual(Invoke.class,
					Invoke.process_FIELDNAME, process.getId())));
			wo.setManualList(WrapManual.outCopier.copy(business.entityManagerContainer().listEqual(Manual.class,
					Manual.process_FIELDNAME, process.getId())));
			wo.setMergeList(WrapMerge.outCopier.copy(business.entityManagerContainer().listEqual(Merge.class,
					Merge.process_FIELDNAME, process.getId())));
			wo.setParallelList(WrapParallel.outCopier.copy(business.entityManagerContainer().listEqual(Parallel.class,
					Parallel.process_FIELDNAME, process.getId())));
			wo.setPublishList(WrapPublish.outCopier.copy(business.entityManagerContainer().listEqual(Publish.class,
					Publish.process_FIELDNAME, process.getId())));
			wo.setServiceList(WrapService.outCopier.copy(business.entityManagerContainer().listEqual(Service.class,
					Service.process_FIELDNAME, process.getId())));
			wo.setSplitList(WrapSplit.outCopier.copy(business.entityManagerContainer().listEqual(Split.class,
					Split.process_FIELDNAME, process.getId())));
			wo.setRouteList(WrapRoute.outCopier.copy(business.entityManagerContainer().listEqual(Route.class,
					Route.process_FIELDNAME, process.getId())));
			wos.add(wo);
		}
		return wos;
	}

	private List<WrapApplicationDict> listApplicationDict(Business business, Application application, Wi wi)
			throws Exception {
		List<WrapApplicationDict> wos = new ArrayList<>();
		for (String id : ListTools.trim(wi.listApplicationDictId(), true, true)) {
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			ApplicationDict applicationDict = business.entityManagerContainer().find(id, ApplicationDict.class);
			if (null == applicationDict) {
				throw new ExceptionEntityNotExist(id, Application.class);
			}
			WrapApplicationDict wo = WrapApplicationDict.outCopier.copy(applicationDict);
			List<ApplicationDictItem> items = this.listApplicationDictItem(business, applicationDict);
			JsonElement json = converter.assemble(items);
			wo.setData(json);
			wos.add(wo);
		}
		return wos;
	}

	private List<ApplicationDictItem> listApplicationDictItem(Business business, ApplicationDict applicationDict)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict.getId());
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public static class Wi extends WrapProcessPlatform {

		private static final long serialVersionUID = -8134258296651783870L;

	}

	public static class Wo extends WrapProcessPlatform {

		private static final long serialVersionUID = -1130848016754973977L;
		@FieldDescribe("返回标识")
		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

	}

}
