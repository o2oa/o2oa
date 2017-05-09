package com.x.organization.assemble.control.alpha.jaxrs.companyattribute;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.CompanyAttribute;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			CompanyAttribute o = business.companyAttribute().pick(id);
			if (null == o) {
				throw new ExceptionCompanyAttributeNotExist(id);
			}
			Wo wo = Wo.copier.copy(o);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends CompanyAttribute {

		private static final long serialVersionUID = -127291000673692614L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static WrapCopier<CompanyAttribute, Wo> copier = WrapCopierFactory.wi(CompanyAttribute.class, Wo.class, null,
				Excludes);

	}

}