package com.x.general.assemble.control.jaxrs.invoice;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;
import com.x.general.core.entity.Invoice;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

class ActionCreate extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if(StringUtils.isBlank(wi.getFileId())){
                throw new ExceptionFieldEmpty("fileId");
            }
            if(Objects.isNull(wi.getInvoiceDate())){
                throw new ExceptionFieldEmpty("invoiceDate");
            }
            if(Objects.isNull(wi.getTotalAmount())){
                throw new ExceptionFieldEmpty("totalAmount");
            }
            GeneralFile gf = emc.find(wi.getFileId(), GeneralFile.class);
            if(gf == null){
                throw new ExceptionEntityNotExist(wi.getFileId());
            }
            StorageMapping gfMapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
                    gf.getStorage());
            StorageMapping mapping = ThisApplication.context().storageMappings().random(Invoice.class);
            emc.beginTransaction(Invoice.class);
            Invoice invoice = Wi.copier.copy(wi);
            invoice.setPerson(effectivePerson.getDistinguishedName());
            invoice.saveContent(mapping, gf.readContent(gfMapping), gf.getName());
            emc.persist(invoice, CheckPersistType.all);
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

        @FieldDescribe("已上传的文件ID，通过uploadForCreate接口上传.")
        private String fileId;

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }
    }

    public static class Wo extends WoId {

    }

}
