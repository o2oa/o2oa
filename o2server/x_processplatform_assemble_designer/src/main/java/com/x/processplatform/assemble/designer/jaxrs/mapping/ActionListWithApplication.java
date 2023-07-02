package com.x.processplatform.assemble.designer.jaxrs.mapping;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Mapping;

class ActionListWithApplication extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();

			Business business = new Business(emc);

			Application application = emc.flag(applicationFlag, Application.class);

			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}

			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			wos = emc.fetchEqual(Mapping.class, Wo.copier, Mapping.application_FIELDNAME, application.getId());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Mapping {

		private static final long serialVersionUID = -7495725325510376323L;

		public static WrapCopier<Mapping, Wo> copier = WrapCopierFactory.wo(Mapping.class, Wo.class,
				JpaObject.singularAttributeField(Mapping.class, true, true), null);

	}
}
