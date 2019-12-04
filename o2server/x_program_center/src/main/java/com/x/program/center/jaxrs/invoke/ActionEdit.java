package com.x.program.center.jaxrs.invoke;

import com.x.base.core.project.cache.ApplicationCache;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.core.entity.Invoke;

class ActionEdit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Invoke invoke = emc.flag(flag, Invoke.class );
			if (StringUtils.isEmpty(wi.getName())) {
				throw new ExceptionEmptyName();
			}
			if (emc.duplicateWithFlags(invoke.getId(), Invoke.class, wi.getName())) {
				throw new ExceptionDuplicateName(wi.getName());
			}
			if (!StringUtils.isEmpty(wi.getAlias())) {
				if (emc.duplicateWithFlags(invoke.getId(), Invoke.class, wi.getAlias())) {
					throw new ExceptionDuplicateAlias(wi.getAlias());
				}
			}
			emc.beginTransaction(Invoke.class);
			Wi.copier.copy(wi, invoke);
			this.addComment(invoke);
			emc.check(invoke, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Invoke.class);
			Wo wo = new Wo();
			wo.setId(invoke.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Invoke {

		private static final long serialVersionUID = -6314932919066148113L;

		static WrapCopier<Wi, Invoke> copier = WrapCopierFactory.wi(Wi.class, Invoke.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify));
	}

	public static class Wo extends WoId {

	}

}