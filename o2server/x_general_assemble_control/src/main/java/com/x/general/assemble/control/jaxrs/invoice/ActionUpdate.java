package com.x.general.assemble.control.jaxrs.invoice;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.general.core.entity.Invoice;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

class ActionUpdate extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if(Objects.isNull(wi.getInvoiceDate())){
                throw new ExceptionFieldEmpty("invoiceDate");
            }
            if(Objects.isNull(wi.getTotalAmount())){
                throw new ExceptionFieldEmpty("totalAmount");
            }
            Invoice invoice = emc.find(id, Invoice.class);
            if(invoice == null){
                throw new ExceptionEntityNotExist(id);
            }
            if (effectivePerson.isNotPerson(invoice.getPerson()) && effectivePerson.isNotManager()) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            emc.beginTransaction(Invoice.class);
            Wi.copier.copy(wi, invoice);
            emc.commit();
            Wo wo = new Wo();
            wo.setId(invoice.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wi extends Invoice {

        private static final long serialVersionUID = 6624639107781167248L;

        static WrapCopier<Wi, Invoice> copier = WrapCopierFactory.wi(Wi.class, Invoice.class, null,
                ListTools.toList(JpaObject.FieldsUnmodifyIncludePorperties,
                        Invoice.applyStatus_FIELDNAME, Invoice.lastUpdateTime_FIELDNAME,
                        Invoice.name_FIELDNAME, Invoice.length_FIELDNAME, Invoice.storage_FIELDNAME,
                        Invoice.deepPath_FIELDNAME, Invoice.extension_FIELDNAME, Invoice.person_FIELDNAME));

    }

    public static class Wo extends WoId {

    }

}
