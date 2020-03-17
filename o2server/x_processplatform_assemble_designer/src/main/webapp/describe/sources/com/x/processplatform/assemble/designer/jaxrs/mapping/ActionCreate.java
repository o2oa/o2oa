package com.x.processplatform.assemble.designer.jaxrs.mapping;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Mapping;
import com.x.processplatform.core.entity.element.Process;

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

			Mapping mapping = new Mapping();

			Wi.copier.copy(wi, mapping);

			this.empty(mapping);

			this.duplicate(business, mapping);

			try {
				Class.forName(DynamicEntity.CLASS_PACKAGE + "." + mapping.getTableName());
			} catch (Exception e) {
				throw new ExceptionDynamicClassNotExist(mapping.getTableName());
			}
			try {
				gson.fromJson(mapping.getData(), new TypeToken<List<Mapping.Item>>() {
				}.getType());
			} catch (Exception e) {
				throw new ExceptionDataError();
			}

			emc.beginTransaction(Mapping.class);
			emc.persist(mapping, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Mapping.class);
			Wo wo = new Wo();
			wo.setId(mapping.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Mapping {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Mapping> copier = WrapCopierFactory.wi(Wi.class, Mapping.class, null,
				Arrays.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
						JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));

	}

}