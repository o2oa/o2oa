package com.x.organization.assemble.personal.jaxrs.trust;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.accredit.Trust;

class ActionEdit extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionEdit.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Trust trust = emc.find(id, Trust.class);
			if (null == trust) {
				throw new ExceptionEntityNotExist(id, Trust.class);
			}
			if (StringUtils.isEmpty(wi.getFromIdentity())) {
				throw new ExceptionEmptyFromIdentity();
			}
			if (StringUtils.isEmpty(wi.getToIdentity())) {
				throw new ExceptionEmptyToIdentity();
			}
			if (BooleanUtils.isTrue(wi.getWhole())) {
				if (!this.checkWhole(business, wi)) {
					throw new ExceptionWholeExist(wi.getFromIdentity());
				}
			} else {
				if (StringUtils.isNotEmpty(wi.getApplication())) {
					if (StringUtils.isNotEmpty(wi.getProcess())) {
						if (!this.checkProcess(business, wi)) {
							throw new ExceptionProcessExist(wi.getFromIdentity(), wi.getApplication(), wi.getProcess());
						}
					} else if (!this.checkApplication(business, wi)) {
						throw new ExceptionApplicationExist(wi.getFromIdentity(), wi.getApplication());
					}
				}
			}
			Wi.copier.copy(wi, trust);
			String fromPerson = this.getPersonDNWithIdentityDN(business, trust.getFromIdentity());
			if (StringUtils.isEmpty(fromPerson)) {
				throw new ExceptionPersonNotExistWithIdentity(trust.getFromIdentity());
			} else {
				trust.setFromPerson(fromPerson);
			}
			String toPerson = this.getPersonDNWithIdentityDN(business, trust.getToIdentity());
			if (StringUtils.isEmpty(toPerson)) {
				throw new ExceptionPersonNotExistWithIdentity(trust.getToIdentity());
			} else {
				trust.setToPerson(toPerson);
			}
			emc.beginTransaction(Trust.class);
			emc.persist(trust, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Trust.class);
			Wo wo = new Wo();
			wo.setId(trust.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Trust {

		private static final long serialVersionUID = 1571810726944802231L;

		static WrapCopier<Wi, Trust> copier = WrapCopierFactory.wi(Wi.class, Trust.class, null,
				JpaObject.FieldsUnmodify);
	}

	public static class Wo extends WoId {

	}

}
