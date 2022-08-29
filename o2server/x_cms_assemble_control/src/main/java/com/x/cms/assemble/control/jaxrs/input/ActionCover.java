package com.x.cms.assemble.control.jaxrs.input;

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
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.AppInfoConfig;
import com.x.cms.core.entity.CategoryExt;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.CategoryInfo_;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.File;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.wrap.WrapAppDict;
import com.x.cms.core.entity.element.wrap.WrapCategoryInfo;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.cms.core.entity.element.wrap.WrapFile;
import com.x.cms.core.entity.element.wrap.WrapForm;
import com.x.cms.core.entity.element.wrap.WrapScript;

class ActionCover extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCover.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			AppInfo appInfo = this.cover(business, wi);
			wo.setId(appInfo.getId());
			result.setData(wo);
			return result;
		}
	}

	private AppInfo cover(Business business, Wi wi) throws Exception {
		List<JpaObject> persistObjects = new ArrayList<>();
		List<JpaObject> removeObjects = new ArrayList<>();
		List<JpaObject> checkPersistObjects = new ArrayList<>();
		AppInfo appInfo = business.entityManagerContainer().find(wi.getId(), AppInfo.class);

		if (null == appInfo) {
			//throw new ExceptionAppInfoNotExist(wi.getId());
			appInfo = WrapCms.inCopier.copy(wi);
			appInfo.setAppName(this.idleAppInfoName(business, appInfo.getAppName(), appInfo.getId()));
			appInfo.setAppAlias(this.idleAppInfoAlias(business, appInfo.getAppAlias(), appInfo.getId()));
			persistObjects.add(appInfo);
		}else{
			WrapCms.inCopier.copy(wi, appInfo);
			appInfo.setAppName(this.idleAppInfoName(business, appInfo.getAppName(), appInfo.getId()));
			appInfo.setAppAlias(this.idleAppInfoAlias(business, appInfo.getAppAlias(), appInfo.getId()));
		}

		//2020年1月16日 O2LEE 保存栏目信息对应的配置支持信息JSON ---->start
		AppInfoConfig appInfoConfig = business.entityManagerContainer().find(wi.getId(), AppInfoConfig.class);
		if (null == appInfoConfig) {
			appInfoConfig = new AppInfoConfig();
			appInfoConfig.setId( wi.getId() );
			appInfoConfig.setConfig( wi.getConfig() );
			persistObjects.add( appInfoConfig );
		}else{
			appInfoConfig.setConfig( wi.getConfig() );
			checkPersistObjects.add( appInfoConfig );
		}
		//2020年1月16日 O2LEE 保存栏目信息对应的配置支持信息JSON ---->end

		for (WrapForm _o : wi.getFormList() ) {
			Form obj = business.entityManagerContainer().find( _o.getId(), Form.class );
			if ( null != obj ) {
				WrapForm.inCopier.copy(_o, obj);
			} else {
				obj = WrapForm.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias( this.idleAliasWithEntity(business, appInfo.getId(), obj.getAlias(), Form.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithEntity(business, appInfo.getId(), obj.getName(), Form.class, obj.getId()));
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
				obj.setAlias(this.idleAliasWithEntity(business, appInfo.getId(), obj.getAlias(), Script.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithEntity(business, appInfo.getId(), obj.getName(), Script.class, obj.getId()));
			}
		}

		for (WrapFile _o : wi.getFileList()) {
			File obj = business.entityManagerContainer().find(_o.getId(), File.class);
			if (null != obj) {
				WrapFile.inCopier.copy(_o, obj);
			} else {
				obj = WrapFile.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithAppInfo(business, appInfo.getId(), obj.getAlias(), File.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithAppInfo(business, appInfo.getId(), obj.getName(), File.class, obj.getId()));
			}
			obj.setAppId(appInfo.getId());
		}

		for (WrapAppDict _o : wi.getAppDictList()) {
			AppDict obj = business.entityManagerContainer().find(_o.getId(), AppDict.class);
			if (null != obj) {
				for (AppDictItem o : business.getAppDictItemFactory().listObjectWithAppDict(obj.getId())) {
					removeObjects.add(o);
				}
				WrapAppDict.inCopier.copy(_o, obj);
			} else {
				obj = WrapAppDict.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			DataItemConverter<AppDictItem> converter = new DataItemConverter<>(AppDictItem.class);
			List<AppDictItem> list = converter.disassemble(_o.getData());
			for (AppDictItem o : list) {
				o.setBundle(obj.getId());
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(obj.getDistributeFactor());
				o.setAppId(obj.getAppId());
				persistObjects.add(o);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithAppInfo(business, appInfo.getId(), obj.getAlias(),
						AppDict.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithAppInfo(business, appInfo.getId(), obj.getName(),
						AppDict.class, obj.getId()));
			}
			obj.setAppId(appInfo.getId());
		}

		for ( WrapCategoryInfo wrapCategoryInfo : wi.getCategoryInfoList() ) {
			CategoryInfo categoryInfo = business.entityManagerContainer().find(wrapCategoryInfo.getId(), CategoryInfo.class);
			if (null != categoryInfo) {
				WrapCategoryInfo.inCopier.copy(wrapCategoryInfo, categoryInfo);
			} else {
				categoryInfo = WrapCategoryInfo.inCopier.copy(wrapCategoryInfo);
				persistObjects.add(categoryInfo);
			}
			if (StringUtils.isNotEmpty(categoryInfo.getCategoryAlias())) {
				categoryInfo.setCategoryAlias(this.idleAliasWithCategory(business, appInfo.getId(), categoryInfo.getCategoryAlias(),
						CategoryInfo.class, categoryInfo.getId()));
			}
			if (StringUtils.isNotEmpty(categoryInfo.getCategoryName())) {
				categoryInfo.setCategoryName(this.idleNameWithCategory(business, appInfo.getId(), categoryInfo.getCategoryName(),
						CategoryInfo.class, categoryInfo.getId()));
			}

			//添加CategoryExt信息更新逻辑
			CategoryExt categoryExt = business.entityManagerContainer().find( categoryInfo.getId(), CategoryExt.class);
			if (null != categoryInfo) {
				if( wrapCategoryInfo.getCategoryExt() != null ){
					if( categoryExt != null ){
						//更新CategoryExt
						wrapCategoryInfo.getCategoryExt().copyTo( categoryExt );
						checkPersistObjects.add( categoryExt );
					}else{
						//原来就没有，需要添加一个
						categoryExt = new CategoryExt();
						wrapCategoryInfo.getCategoryExt().copyTo( categoryExt );
						persistObjects.add( categoryInfo );
					}
				}else{
					//需要删除CategoryExt
					if( categoryExt != null ){
						//删除CategoryExt
						removeObjects.add( categoryExt );
					}
				}
			}
		}

		business.entityManagerContainer().beginTransaction(File.class);
		business.entityManagerContainer().beginTransaction(AppInfo.class);
		business.entityManagerContainer().beginTransaction(Script.class);
		business.entityManagerContainer().beginTransaction(Form.class);
		business.entityManagerContainer().beginTransaction(AppDict.class);
		business.entityManagerContainer().beginTransaction(AppDictItem.class);
		business.entityManagerContainer().beginTransaction(CategoryInfo.class);
		business.entityManagerContainer().beginTransaction(CategoryExt.class);
		//2020年1月16日 O2LEE 保存栏目信息对应的配置支持信息JSON ---->start
		business.entityManagerContainer().beginTransaction(AppInfoConfig.class);
		//2020年1月16日 O2LEE 保存栏目信息对应的配置支持信息JSON ---->end

		for (JpaObject o : removeObjects) {
			business.entityManagerContainer().remove(o);
		}
		for (JpaObject o : persistObjects) {
			business.entityManagerContainer().persist(o);
		}
		for (JpaObject o : checkPersistObjects) {
			business.entityManagerContainer().check(o, CheckPersistType.all);
		}

		business.entityManagerContainer().commit();

		CacheManager.notify(CategoryInfo.class);
		CacheManager.notify(AppDictItem.class);
		CacheManager.notify(AppDict.class);
		CacheManager.notify(Form.class);
		CacheManager.notify(Script.class);
		CacheManager.notify(AppInfo.class);
		//2020年1月16日 O2LEE 保存栏目信息对应的配置支持信息JSON ---->start
		CacheManager.notify(AppInfoConfig.class);
		//2020年1月16日 O2LEE 保存栏目信息对应的配置支持信息JSON ---->end

		return appInfo;
	}

	private <T extends JpaObject> String idleNameWithAppInfo(Business business, String appId, String name,
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
		p = cb.and(p, cb.equal(root.get("appId"), appId));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("name")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	private <T extends JpaObject> String idleAliasWithAppInfo(Business business, String appId, String alias,
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
		Predicate p = root.get("alias").in(list);
		p = cb.and(p, cb.equal(root.get("appId"), appId));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("alias")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	/**
	 * 根据id 和 name属性，查询重复的对象，适用Dict、Form、Scrpt
	 * @param business
	 * @param id
	 * @param name
	 * @param cls
	 * @param excludeId
	 * @return
	 * @throws Exception
	 */
	private <T extends JpaObject> String idleNameWithEntity(Business business, String id, String name,
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
		p = cb.and(p, cb.equal(root.get("id"), id));
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
	 * @param id
	 * @param alias
	 * @param cls
	 * @param excludeId
	 * @return
	 * @throws Exception
	 */
	private <T extends JpaObject> String idleAliasWithEntity(Business business, String id, String alias,
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
		p = cb.and(p, cb.equal(root.get("id"), id));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("alias")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	/**
	 * 根据id 和 name属性，查询重复的对象，适用CategoryInfo
	 * @param business
	 * @param id
	 * @param name
	 * @param cls
	 * @param excludeId
	 * @return
	 * @throws Exception
	 */
	private <T extends JpaObject> String idleNameWithCategory(Business business, String id, String name,
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
		EntityManager em = business.entityManagerContainer().get( CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		Predicate p = root.get( CategoryInfo_.categoryName ).in(list);
		p = cb.and(p, cb.equal(root.get( CategoryInfo_.id ), id));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get( CategoryInfo_.categoryName  )).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	/**
	 * 根据id 和 alias属性，查询重复的对象，适用CategoryInfo
	 * @param business
	 * @param id
	 * @param alias
	 * @param cls
	 * @param excludeId
	 * @return
	 * @throws Exception
	 */
	private <T extends JpaObject> String idleAliasWithCategory(Business business, String id, String alias,
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
		EntityManager em = business.entityManagerContainer().get(CategoryInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CategoryInfo> root = cq.from(CategoryInfo.class);
		Predicate p = root.get( CategoryInfo_.categoryAlias ).in( list );
		p = cb.and(p, cb.equal(root.get(CategoryInfo_.id ), id));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get(CategoryInfo_.categoryAlias)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	public static class Wi extends WrapCms{

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WoId {

	}

}
