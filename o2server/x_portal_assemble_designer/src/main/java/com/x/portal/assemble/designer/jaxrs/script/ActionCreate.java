package com.x.portal.assemble.designer.jaxrs.script;

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

import java.util.Date;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Portal portal = emc.find(wi.getPortal(), Portal.class);
			if (!business.editable(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Script.class);
			Script script = Wi.copier.copy(wi);
			script.setCreatorPerson(effectivePerson.getDistinguishedName());
			script.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			script.setLastUpdateTime(new Date());
			this.checkName(business, script);
			this.checkAlias(business, script);
			this.checkDepend(business, script);
			emc.persist(script, CheckPersistType.all);
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

	public static class Wo extends WoId {

	}

	public static class Wi extends Script {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Script> copier = WrapCopierFactory.wi(Wi.class, Script.class, null,
				JpaObject.FieldsUnmodifyExcludeId);

	}

}
