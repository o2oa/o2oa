package com.x.program.center.jaxrs.script;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Script;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Script script = emc.find(id, Script.class);
			if (null == script) {
				throw new ExceptionEntityNotExist(id);
			}
			if(!business.serviceControlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Script.class);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wi.copier.copy(wi, script);
			script.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			script.setLastUpdateTime(new Date());
			this.checkName(business, script);
			this.checkAlias(business, script);
			this.checkDepend(business, script);
			emc.check(script, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Script.class);
			Wo wo = new Wo();
			wo.setId(script.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Script {

		private static final long serialVersionUID = -569577281379312696L;
		static WrapCopier<Wi, Script> copier = WrapCopierFactory.wi(Wi.class, Script.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {
	}
}
