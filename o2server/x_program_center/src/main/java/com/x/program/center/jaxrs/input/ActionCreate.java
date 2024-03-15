package com.x.program.center.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.element.Form;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
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

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			ServiceModuleEnum serviceModuleEnum = this.create(business, wi);
			wo.setId(serviceModuleEnum.getValue());
			result.setData(wo);
			return result;
		}
	}

	/**
	 * 新建导入
	 * @param business
	 * @param wi
	 * @return
	 * @throws Exception
	 */
	private ServiceModuleEnum create( Business business, Wi wi ) throws Exception {
		List<JpaObject> persistObjects = new ArrayList<>();
		ServiceModuleEnum serviceModuleEnum = ServiceModuleEnum.getEnumByValue(wi.getId());
		if (null == serviceModuleEnum) {
			throw new ExceptionAppInfoNotExist(wi.getId());
		}
		for (WrapAgent _o : wi.getAgentList() ) {
			Agent obj = business.entityManagerContainer().find( _o.getId(), Agent.class );
			if (null != obj) {
				throw new ExceptionEntityExistForCreate(_o.getId(), Form.class);
			}
			obj = WrapAgent.inCopier.copy(_o);
			persistObjects.add(obj);
		}
		for (WrapInvoke _o : wi.getInvokeList() ) {
			Invoke obj = business.entityManagerContainer().find( _o.getId(), Invoke.class );
			if (null != obj) {
				throw new ExceptionEntityExistForCreate(_o.getId(), Form.class);
			}
			obj = WrapInvoke.inCopier.copy(_o);
			persistObjects.add(obj);
		}
		for (WrapScript _o : wi.getScriptList()) {
			Script obj = business.entityManagerContainer().find(_o.getId(), Script.class);
			if (null != obj) {
				throw new ExceptionEntityExistForCreate(_o.getId(), Script.class);
			}
			obj = WrapScript.inCopier.copy(_o);
			persistObjects.add(obj);
		}
		for (WrapApplicationDict _o : wi.getDictList()) {
			ApplicationDict obj = business.entityManagerContainer().find(_o.getId(), ApplicationDict.class);
			if (null != obj) {
				throw new ExceptionEntityExistForCreate(_o.getId(), ApplicationDict.class);
			}
			obj = WrapApplicationDict.inCopier.copy(_o);
			obj.setApplication(ApplicationDict.PROJECT_SERVICE);
			obj.setProject(ApplicationDict.PROJECT_SERVICE);
			persistObjects.add(obj);
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> list = converter.disassemble(_o.getData());
			for (ApplicationDictItem o : list) {
				o.setBundle(obj.getId());
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(obj.getDistributeFactor());
				o.setApplication(obj.getApplication());
				persistObjects.add(o);
			}
		}

		business.entityManagerContainer().beginTransaction(Agent.class);
		business.entityManagerContainer().beginTransaction(Invoke.class);
		business.entityManagerContainer().beginTransaction(Script.class);
		business.entityManagerContainer().beginTransaction(ApplicationDict.class);
		business.entityManagerContainer().beginTransaction(ApplicationDictItem.class);

		for (JpaObject o : persistObjects) {
			business.entityManagerContainer().persist(o);
		}
		business.entityManagerContainer().commit();
		if(!wi.getAgentList().isEmpty()){
			CacheManager.notify(Agent.class);
		}
		if(!wi.getInvokeList().isEmpty()){
			CacheManager.notify(Invoke.class);
		}
		if(!wi.getDictList().isEmpty()){
			CacheManager.notify(ApplicationDict.class);
		}
		if(!wi.getScriptList().isEmpty()){
			CacheManager.notify(Script.class);
		}
		return serviceModuleEnum;
	}

	public static class Wi extends WrapServiceModule {

	}

	public static class Wo extends WoId {

	}
}
