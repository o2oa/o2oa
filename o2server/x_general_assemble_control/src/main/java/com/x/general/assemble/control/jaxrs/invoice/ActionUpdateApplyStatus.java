package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.general.core.entity.Invoice;

class ActionUpdateApplyStatus extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Invoice invoice = emc.find(id, Invoice.class);
            if(invoice == null){
                throw new ExceptionEntityNotExist(id);
            }
            if (effectivePerson.isNotPerson(invoice.getPerson()) && effectivePerson.isNotManager()) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            emc.beginTransaction(Invoice.class);
            invoice.setApplyStatus(Invoice.APPLY_STATUS_1);
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
