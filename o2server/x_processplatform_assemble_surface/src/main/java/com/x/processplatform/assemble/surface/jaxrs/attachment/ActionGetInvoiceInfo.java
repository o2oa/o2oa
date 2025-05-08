package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.core.entity.Invoice;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import org.apache.commons.lang3.BooleanUtils;

class ActionGetInvoiceInfo extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetInvoiceInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, String jobOrWorkOrWorkCompleted) throws Exception {

		LOGGER.debug("execute:{}, id:{}, workId:{}.", effectivePerson::getDistinguishedName, () -> id,
				() -> jobOrWorkOrWorkCompleted);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Invoice invoice = emc.find(id, Invoice.class);
			if (null == invoice) {
				throw new ExceptionEntityNotExist(id, Invoice.class);
			}

			Control control = new JobControlBuilder(effectivePerson, business, jobOrWorkOrWorkCompleted).enableAllowVisit()
					.build();
			if (BooleanUtils.isNotTrue(control.getAllowVisit()) || !invoice.getPerson().equals(control.getCreatorPerson())) {
				throw new ExceptionAccessDenied(effectivePerson, jobOrWorkOrWorkCompleted);
			}

			Wo wo = Wo.copier.copy(invoice);


			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Invoice {

		static WrapCopier<Invoice, Wo> copier = WrapCopierFactory.wo(Invoice.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
