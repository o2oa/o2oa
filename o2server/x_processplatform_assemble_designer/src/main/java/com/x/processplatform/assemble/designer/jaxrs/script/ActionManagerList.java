package com.x.processplatform.assemble.designer.jaxrs.script;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

import java.util.List;

class ActionManagerList extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		if(!effectivePerson.isManager()){
			throw new ExceptionAccessDenied(effectivePerson);
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = emc.fetchAll(Script.class,  Wo.copier);
			wos.stream().forEach(wo -> {
				try {
					Application app = emc.find(wo.getApplication(), Application.class);
					if(app != null){
						wo.setAppId(app.getId());
						wo.setAppName(app.getName());
					}
				} catch (Exception e) {
				}
			});
			result.setData(wos);
			result.setCount((long)wos.size());
			return result;
		}
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = -8095369685452823624L;

		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class,
				JpaObject.singularAttributeField(Script.class, true, false),null);

		@FieldDescribe("应用Id.")
		private String appId;

		@FieldDescribe("应用名称.")
		private String appName;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}
	}
}
