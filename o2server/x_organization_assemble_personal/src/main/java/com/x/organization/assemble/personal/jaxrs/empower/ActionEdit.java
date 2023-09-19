package com.x.organization.assemble.personal.jaxrs.empower;

import org.apache.commons.lang3.StringUtils;

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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.accredit.Empower;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionEdit extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Empower empower = emc.find(id, Empower.class);
			if (null == empower) {
				throw new ExceptionEntityNotExist(id, Empower.class);
			}
			Wi.copier.copy(wi, empower);
			this.check(business, empower);
			String fromPerson = this.getPersonDNWithIdentityDN(business, empower.getFromIdentity());
			if (StringUtils.isEmpty(fromPerson)) {
				throw new ExceptionPersonNotExistWithIdentity(empower.getFromIdentity());
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(empower.getFromPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, empower);
			}
			empower.setFromPerson(fromPerson);
			String toPerson = this.getPersonDNWithIdentityDN(business, empower.getToIdentity());
			if (StringUtils.isEmpty(toPerson)) {
				throw new ExceptionPersonNotExistWithIdentity(empower.getToIdentity());
			} else {
				empower.setToPerson(toPerson);
			}
			emc.beginTransaction(Empower.class);
			emc.check(empower, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Empower.class);
			Wo wo = new Wo();
			wo.setId(empower.getId());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.organization.assemble.personal.jaxrs.empower.ActionEdit$Wi")
	public static class Wi extends Empower {

		private static final long serialVersionUID = 1571810726944802231L;

		static WrapCopier<Wi, Empower> copier = WrapCopierFactory.wi(Wi.class, Empower.class, null,
				JpaObject.FieldsUnmodify);
	}

	@Schema(name = "com.x.organization.assemble.personal.jaxrs.empower.ActionEdit$Wo")
	public static class Wo extends WoId {

		private static final long serialVersionUID = -2647895266325454861L;

	}

}
