package com.x.cms.assemble.control.jaxrs.script;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Script;

import java.util.ArrayList;
import java.util.List;

class ActionManagerList extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		if(!effectivePerson.isManager()){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos;
			if(ListTools.isEmpty(wi.getAppIdList())){
				wos = emc.fetchAll(Script.class,  Wo.copier);
			}else{
				wos = emc.fetchIn(Script.class, Wo.copier, Script.appId_FIELDNAME, wi.getAppIdList());
			}
			wos.stream().forEach(wo -> {
				try {
					AppInfo appInfo = emc.find( wo.getAppId(), AppInfo.class );
					if(appInfo != null){
						wo.setAppName(appInfo.getAppName());
					}
				} catch (Exception e) {
				}
			});
			result.setData(wos);
			result.setCount((long)wos.size());
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject{
		@FieldDescribe("应用ID列表.")
		private List<String> appIdList = new ArrayList<>();

		public List<String> getAppIdList() {
			return appIdList;
		}

		public void setAppIdList(List<String> appIdList) {
			this.appIdList = appIdList;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = -8095369685452823624L;

		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class,
				JpaObject.singularAttributeField(Script.class, true, false),null);

		@FieldDescribe("应用名称.")
		private String appName;

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}
	}
}
