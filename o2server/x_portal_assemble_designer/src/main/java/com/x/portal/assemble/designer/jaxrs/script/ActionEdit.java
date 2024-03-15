package com.x.portal.assemble.designer.jaxrs.script;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.ThisApplication;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.ScriptVersion;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Script script = emc.find(id, Script.class);
			if (null == script) {
				throw new ScriptNotExistedException(id);
			}
			Portal portal = emc.find(script.getPortal(), Portal.class);
			if (null == portal) {
				throw new PortalNotExistedException(script.getPortal());
			}
			if (!business.editable(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getDistinguishedName());
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
			// 保存历史版本
			ThisApplication.scriptVersionQueue.send(new ScriptVersion(script.getId(), jsonElement, effectivePerson.getDistinguishedName()));
			Wo wo = new Wo();
			wo.setId(script.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Script {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Script> copier = WrapCopierFactory.wi(Wi.class, Script.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {
	}
}
