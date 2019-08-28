package com.x.processplatform.assemble.designer.jaxrs.projection;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionUnknowValue;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Projection;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			Business business = new Business(emc);

			Application application = emc.flag(wi.getApplication(), Application.class);

			if (null == application) {
				throw new ExceptionEntityNotExist(wi.getApplication(), Application.class);
			}

			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			if (StringUtils.isNotEmpty(wi.getProcess())) {
				Process process = emc.flag(wi.getProcess(), Process.class);
				if (null == process) {
					throw new ExceptionEntityNotExist(wi.getProcess(), Process.class);
				}
			}

			Projection projection = new Projection();
			Wi.copier.copy(wi, projection);

			this.empty(projection);

			this.duplicate(business, projection);

			try {
				gson.fromJson(projection.getData(), new TypeToken<List<Projection.Item>>() {
				}.getType());
			} catch (Exception e) {
				throw new ExceptionDataError();
			}

			emc.beginTransaction(Projection.class);
			emc.persist(projection, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Projection.class);
			Wo wo = new Wo();
			wo.setId(projection.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Projection {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Projection> copier = WrapCopierFactory.wi(Wi.class, Projection.class, null,
				Arrays.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
						JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));

	}

}