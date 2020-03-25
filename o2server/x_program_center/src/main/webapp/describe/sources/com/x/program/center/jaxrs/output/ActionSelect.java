package com.x.program.center.jaxrs.output;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Agent;
import com.x.program.center.core.entity.Invoke;
import com.x.program.center.core.entity.wrap.ServiceModuleEnum;
import com.x.program.center.core.entity.wrap.WrapAgent;
import com.x.program.center.core.entity.wrap.WrapInvoke;
import com.x.program.center.core.entity.wrap.WrapServiceModule;
import net.sf.ehcache.Element;

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

			cache.put(new Element(flag, cacheObject));
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

		return wo;
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