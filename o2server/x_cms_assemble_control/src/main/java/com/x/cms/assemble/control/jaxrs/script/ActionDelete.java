package com.x.cms.assemble.control.jaxrs.script;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.element.Script;


class ActionDelete extends BaseAction {
	ActionResult<Wo> execute( EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Script script = emc.find(id, Script.class);
			if (null == script) {
				throw new Exception("script{id:" + id + "} not existed.");
			}
			AppInfo appInfo = emc.find(script.getAppId(), AppInfo.class);
			Business business = new Business(emc);
			if (!business.isAppInfoManager(effectivePerson, appInfo)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.beginTransaction(Script.class);
			emc.remove(script, CheckRemoveType.all);
			emc.commit();
			// 清除所有的Script缓存
			CacheManager.notify(Script.class);

			// 记录日志
			emc.beginTransaction(Log.class);
			logService.log(emc, effectivePerson.getDistinguishedName(), script.getName(), script.getAppId(), "", "", script.getId(), "SCRIPT", "删除");
			emc.commit();

			Wo wo = new Wo();
			wo.setId( script.getId() );
			result.setData(wo);

		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}
