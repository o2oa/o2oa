package com.x.cms.assemble.control.jaxrs.output;

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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryExt;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.AppDictItem_;
import com.x.cms.core.entity.element.File;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.wrap.WrapAppDict;
import com.x.cms.core.entity.element.wrap.WrapCategoryExt;
import com.x.cms.core.entity.element.wrap.WrapCategoryInfo;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.cms.core.entity.element.wrap.WrapFile;
import com.x.cms.core.entity.element.wrap.WrapForm;
import com.x.cms.core.entity.element.wrap.WrapScript;

class ActionSelect extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String appInfoFlag, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			AppInfo appInfo = emc.flag(appInfoFlag, AppInfo.class);
			if (null == appInfo) {
				throw new ExceptionAppInfoNotExist(appInfoFlag);
			}
			if (!business.editable(effectivePerson, appInfo)) {
				throw new ExceptionAppInfoAccessDenied(effectivePerson.getDistinguishedName(), appInfo.getAppName(), appInfo.getId());
			}

			WrapCms wrapAppInfo = this.get(business, appInfo, wi);

			//2020年1月16日 O2LEE 查询栏目信息对应的配置支持信息JSON以字符串形式放到WrapCms对象中输出 ---->start
			if( wrapAppInfo != null ){
				wrapAppInfo.setConfig( business.appInfoConfigFactory().getContent( appInfo.getId() ) );
			}
			//2020年1月16日 O2LEE 查询每个栏目信息对应的配置支持信息JSON以字符串形式放到WrapCms对象中输出 ---->end

			OutputCacheObject outputCacheObject = new OutputCacheObject();
			outputCacheObject.setName(appInfo.getAppName());
			outputCacheObject.setCmsAppInfo(wrapAppInfo);

			String flag = StringTools.uniqueToken();

			Wo wo = gson.fromJson(gson.toJson(wrapAppInfo), Wo.class);
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}
	}

	private WrapCms get(Business business, AppInfo appInfo, Wi wi) throws Exception {
		WrapCms wo = WrapCms.outCopier.copy(appInfo);
		wo.setCategoryInfoList(WrapCategoryInfo.outCopier.copy(business.entityManagerContainer().list(CategoryInfo.class, wi.listCategoryInfoId())));
		wo.setFormList(WrapForm.outCopier.copy(business.entityManagerContainer().list(Form.class, wi.listFormId())));
		wo.setScriptList( WrapScript.outCopier.copy(business.entityManagerContainer().list(Script.class, wi.listScriptId())));
		wo.setAppDictList(this.listAppDict(business, appInfo, wi));
		wo.setFileList(WrapFile.outCopier.copy(business.entityManagerContainer().list(File.class, wi.listFileId())));

		//添加CategoryExt信息导出内容逻辑
		if( ListTools.isNotEmpty( wo.getCategoryInfoList() )){
//			CategoryExt categoryExt = null;
//			WrapCategoryExt wrapCategoryExt = null;
			for( WrapCategoryInfo wrapCategoryInfo : wo.getCategoryInfoList() ){
				//查询Category对应的CategoryExt
				wrapCategoryInfo.setCategoryExt( WrapCategoryExt.outCopier.copy( business.entityManagerContainer().find( wrapCategoryInfo.getId(), CategoryExt.class )));
			}
		}
		return wo;
	}

	private List<WrapAppDict> listAppDict(Business business, AppInfo appInfo, Wi wi) throws Exception {
		List<WrapAppDict> wos = new ArrayList<>();
		for (String id : ListTools.trim(wi.listAppDictId(), true, true)) {
			DataItemConverter<AppDictItem> converter = new DataItemConverter<>(AppDictItem.class);
			AppDict appDict = business.entityManagerContainer().find(id, AppDict.class);
			if (null == appDict) {
				throw new ExceptionAppDictNotExist(id);
			}
			WrapAppDict wo = WrapAppDict.outCopier.copy(appDict);
			List<AppDictItem> items = this.listAppDictItem(business, appDict);
			JsonElement json = converter.assemble(items);
			wo.setData(json);
			wos.add(wo);
		}
		return wos;
	}

	private List<AppDictItem> listAppDictItem(Business business, AppDict appInfoDict) throws Exception {
		EntityManager em = business.entityManagerContainer().get(AppDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppDictItem> cq = cb.createQuery(AppDictItem.class);
		Root<AppDictItem> root = cq.from(AppDictItem.class);
		Predicate p = cb.equal(root.get(AppDictItem_.bundle), appInfoDict.getId());
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public static class Wi extends WrapCms {

		private static final long serialVersionUID = 5479791382604619116L;

	}

	public static class Wo extends WrapCms {

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