package com.x.program.center.jaxrs.output;

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
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
import com.x.general.core.entity.ApplicationDictItem_;
import com.x.general.core.entity.wrap.WrapApplicationDict;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.core.entity.Invoke;
import com.x.program.center.core.entity.Script;
import com.x.program.center.core.entity.wrap.ServiceModuleEnum;
import com.x.program.center.core.entity.wrap.WrapAgent;
import com.x.program.center.core.entity.wrap.WrapInvoke;
import com.x.program.center.core.entity.wrap.WrapScript;
import com.x.program.center.core.entity.wrap.WrapServiceModule;

class ActionSelect extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String appInfoFlag, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			ServiceModuleEnum serviceModuleEnum = ServiceModuleEnum.getEnumByValue(appInfoFlag);
			if (null == serviceModuleEnum) {
				throw new ExceptionAppInfoNotExist(appInfoFlag);
			}

			WrapServiceModule wrapAppInfo = this.get(business, serviceModuleEnum, wi);

			CacheObject cacheObject = new CacheObject();
			cacheObject.setModule(wrapAppInfo);
			cacheObject.setName(serviceModuleEnum.getDescription());

			String flag = StringTools.uniqueToken();
			CacheCategory cacheCategory = new CacheCategory(CacheObject.class);
			CacheKey cacheKey = new CacheKey(flag);
			CacheManager.put(cacheCategory, cacheKey, cacheObject);

			Wo wo = gson.fromJson(gson.toJson(wrapAppInfo), Wo.class);
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}
	}

	private WrapServiceModule get(Business business, ServiceModuleEnum serviceModuleEnum, Wi wi) throws Exception {
		WrapServiceModule wo = WrapServiceModule.copy(serviceModuleEnum);
		if(ServiceModuleEnum.AGENT.getValue().equals(wo.getId())) {
			wo.setAgentList(WrapAgent.outCopier.copy(business.entityManagerContainer().list(Agent.class, wi.listAgentId())));
		}
		if(ServiceModuleEnum.INVOKE.getValue().equals(wo.getId())) {
			wo.setInvokeList(WrapInvoke.outCopier.copy(business.entityManagerContainer().list(Invoke.class, wi.listInvokeId())));
		}
		if(ServiceModuleEnum.SCRIPT.getValue().equals(wo.getId())) {
			wo.setScriptList(WrapScript.outCopier.copy(business.entityManagerContainer().list(Script.class, wi.listScriptId())));
		}
		if(ServiceModuleEnum.DICT.getValue().equals(wo.getId())) {
			wo.setDictList(this.listDict(business, wi));
		}
		return wo;
	}

	private List<WrapApplicationDict> listDict(Business business, Wi wi)
			throws Exception {
		List<WrapApplicationDict> wos = new ArrayList<>();
		for (String id : ListTools.trim(wi.listDictId(), true, true)) {
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			ApplicationDict applicationDict = business.entityManagerContainer().find(id, ApplicationDict.class);
			if(applicationDict != null) {
				WrapApplicationDict wo = WrapApplicationDict.outCopier.copy(applicationDict);
				List<ApplicationDictItem> items = this.listApplicationDictItem(business, applicationDict);
				JsonElement json = converter.assemble(items);
				wo.setData(json);
				wos.add(wo);
			}
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

	public static class Wi extends WrapServiceModule {

	}

	public static class Wo extends WrapServiceModule {

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
