package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.Invoice;
import org.apache.commons.lang3.StringUtils;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Invoice invoice = emc.find(id, Invoice.class);
			if (null == invoice) {
				throw new ExceptionEntityNotExist(id);
			}
			if (!StringUtils.equals(effectivePerson.getDistinguishedName(), invoice.getPerson()) && effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if(Invoice.APPLY_STATUS_1.equals(invoice.getApplyStatus())){
				throw new ExceptionErrorStatus();
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Invoice.class,
					invoice.getStorage());
			invoice.deleteContent(mapping);
			emc.beginTransaction(Invoice.class);
			emc.remove(invoice);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(invoice.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
