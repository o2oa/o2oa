package com.x.organization.assemble.express.jaxrs.trustlog;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.accredit.TrustLog;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			if (StringUtils.isEmpty(wi.getApplication())) {
				throw new ExceptionEmptyApplication();
			}

			if (StringUtils.isEmpty(wi.getProcess())) {
				throw new ExceptionEmptyProcess();
			}

			Identity fromIdentity = business.identity().pick(wi.getFromIdentity());

			if (null == fromIdentity) {
				throw new ExceptionEmptyFromIdentity();
			}

			Identity toIdentity = business.identity().pick(wi.getToIdentity());

			if (null == toIdentity) {
				throw new ExceptionEmptyToIdentity();
			}

			TrustLog trustLog = Wi.copier.copy(wi);

			trustLog.setTitle(StringTools.utf8SubString(wi.getTitle(),
					JpaObjectTools.definedLength(TrustLog.class, TrustLog.title_FIELDNAME)));

			emc.beginTransaction(TrustLog.class);
			emc.persist(trustLog, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends TrustLog {

		private static final long serialVersionUID = 4099666546308723776L;

		static WrapCopier<Wi, TrustLog> copier = WrapCopierFactory.wi(Wi.class, TrustLog.class, null,
				JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WrapBoolean {

	}

}