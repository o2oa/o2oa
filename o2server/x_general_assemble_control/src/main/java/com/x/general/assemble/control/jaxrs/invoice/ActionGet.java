package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.general.core.entity.Invoice;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Invoice invoice = emc.find(id, Invoice.class);
			if (null == invoice) {
				throw new ExceptionEntityNotExist(id);
			}
			if (effectivePerson.isNotManager() && effectivePerson.isNotPerson(invoice.getPerson())) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Wo wo = Wo.copier.copy(invoice);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Invoice {

		private static final long serialVersionUID = 100904116457932549L;

		static WrapCopier<Invoice, Wo> copier = WrapCopierFactory.wo(Invoice.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}
