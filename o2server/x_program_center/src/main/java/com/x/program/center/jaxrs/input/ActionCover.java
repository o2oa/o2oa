package com.x.program.center.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

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
import com.x.base.core.project.tools.StringTools;
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

class ActionCover extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCover.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			ServiceModuleEnum serviceModuleEnum = this.cover(business, wi);
			wo.setId(serviceModuleEnum.getValue());
			result.setData(wo);
			return result;
		}
	}

	private ServiceModuleEnum cover(Business business, Wi wi) throws Exception {
		List<JpaObject> persistObjects = new ArrayList<>();
		List<JpaObject> removeObjects = new ArrayList<>();
		ServiceModuleEnum serviceModuleEnum = ServiceModuleEnum.getEnumByValue(wi.getId());
		if (null == serviceModuleEnum) {
			throw new ExceptionAppInfoNotExist(wi.getId());
		}

		for (WrapAgent _o : wi.getAgentList() ) {
			Agent obj = business.entityManagerContainer().find( _o.getId(), Agent.class );
			if ( null != obj ) {
				WrapAgent.inCopier.copy(_o, obj);
			} else {
				obj = WrapAgent.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias( this.idleAliasWithEntity(business, null, obj.getAlias(), Agent.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithEntity(business, null, obj.getName(), Agent.class, obj.getId()));
			}
		}

		for (WrapInvoke _o : wi.getInvokeList() ) {
			Invoke obj = business.entityManagerContainer().find( _o.getId(), Invoke.class );
			if ( null != obj ) {
				WrapInvoke.inCopier.copy(_o, obj);
			} else {
				obj = WrapInvoke.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias( this.idleAliasWithEntity(business, null, obj.getAlias(), Invoke.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithEntity(business, null, obj.getName(), Invoke.class, obj.getId()));
			}
		}

		for (WrapScript _o : wi.getScriptList()) {
			Script obj = business.entityManagerContainer().find(_o.getId(), Script.class);
			if (null != obj) {
				WrapScript.inCopier.copy(_o, obj);
			} else {
				obj = WrapScript.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithEntity(business, null, obj.getAlias(), Script.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithEntity(business, null, obj.getName(), Script.class, obj.getId()));
			}
		}

		for (WrapApplicationDict _o : wi.getDictList()) {
			ApplicationDict obj = business.entityManagerContainer().find(_o.getId(), ApplicationDict.class);
			if (null != obj) {
				for (ApplicationDictItem o : business.applicationDictItem()
						.listWithApplicationDictObject(obj.getId())) {
					removeObjects.add(o);
				}
				WrapApplicationDict.inCopier.copy(_o, obj);
			} else {
				obj = WrapApplicationDict.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> list = converter.disassemble(_o.getData());
			for (ApplicationDictItem o : list) {
				o.setBundle(obj.getId());
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(obj.getDistributeFactor());
				o.setApplication(obj.getApplication());
				persistObjects.add(o);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithEntity(business, ApplicationDict.PROJECT_SERVICE, obj.getAlias(), ApplicationDict.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleAliasWithEntity(business, ApplicationDict.PROJECT_SERVICE, obj.getName(),
						ApplicationDict.class, obj.getId()));
			}
			obj.setApplication(ApplicationDict.PROJECT_SERVICE);
			obj.setProject(ApplicationDict.PROJECT_SERVICE);
		}

		business.entityManagerContainer().beginTransaction(Invoke.class);
		business.entityManagerContainer().beginTransaction(Agent.class);
		business.entityManagerContainer().beginTransaction(Script.class);
		business.entityManagerContainer().beginTransaction(ApplicationDict.class);
		business.entityManagerContainer().beginTransaction(ApplicationDictItem.class);

		for (JpaObject o : removeObjects) {
			business.entityManagerContainer().remove(o);
		}
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

	/**
	 * 根据id 和 name属性，查询重复的对象，适用Dict、Form、Scrpt
	 * @param business
	 * @param name
	 * @param cls
	 * @param excludeId
	 * @return
	 * @throws Exception
	 */
	private <T extends JpaObject> String idleNameWithEntity(Business business, String appId, String name,
			Class<T> cls, String excludeId) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(name);
		for (int i = 1; i < 99; i++) {
			list.add(name + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = root.get("name").in(list);
		if(StringUtils.isNotBlank(appId) && cls.getSimpleName().equals(ApplicationDict.class.getSimpleName())){
			p = cb.and(p, cb.equal(root.get("application"), appId));
		}
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("name")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	/**
	 * 根据id 和 alias属性，查询重复的对象，适用Dict、Form、Scrpt
	 * @param business
	 * @param alias
	 * @param cls
	 * @param excludeId
	 * @return
	 * @throws Exception
	 */
	private <T extends JpaObject> String idleAliasWithEntity(Business business, String appId, String alias,
			Class<T> cls, String excludeId) throws Exception {
		if (StringUtils.isEmpty(alias)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(alias);
		for (int i = 1; i < 99; i++) {
			list.add(alias + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = root.get("alias").in( list );
		if(StringUtils.isNotBlank(appId) && cls.getSimpleName().equals(ApplicationDict.class.getSimpleName())){
			p = cb.and(p, cb.equal(root.get("application"), appId));
		}
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("alias")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	public static class Wi extends WrapServiceModule {

	}

	public static class Wo extends WoId {

	}

}
