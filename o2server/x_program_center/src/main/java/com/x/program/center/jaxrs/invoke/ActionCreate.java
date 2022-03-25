package com.x.program.center.jaxrs.invoke;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Invoke;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			/* 判断当前用户是否有权限访问 */
			if(!business.serviceControlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Invoke invoke = Wi.copier.copy(wi);
			if (StringUtils.isEmpty(invoke.getName())) {
				throw new ExceptionEmptyName();
			}
			if (emc.duplicateWithFlags(Invoke.class, invoke.getName())) {
				throw new ExceptionDuplicateName(invoke.getName());
			}
			if (!StringUtils.isEmpty(wi.getAlias())) {
				if (emc.duplicateWithFlags(Invoke.class, invoke.getAlias())) {
					throw new ExceptionDuplicateAlias(invoke.getAlias());
				}
			}
			emc.beginTransaction(Invoke.class);
			emc.persist(invoke, CheckPersistType.all);
			emc.commit();
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
