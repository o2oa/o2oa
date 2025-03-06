package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.Invoice;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

class ActionUpload extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes,
            FormDataContentDisposition disposition) throws Exception {
        StorageMapping mapping = ThisApplication.context().storageMappings().random(Invoice.class);
        Wo wo = new Wo();
        ActionResult<Wo> result = new ActionResult<>();

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            if (null == mapping) {
                throw new ExceptionAllocateStorageMapping();
            }
            String fileName = this.fileName(disposition);
            fileName = FilenameUtils.getName(fileName);
            String extension = StringUtils.lowerCase(FilenameUtils.getExtension(fileName));
            if (StringUtils.isEmpty(extension)) {
                throw new ExceptionEmptyExtension(fileName);
            }
            if (!Invoice.EXT_PDF.equals(extension)) {
                throw new ExceptionErrorExtension(fileName);
            }
            Invoice file = new Invoice(mapping.getName(), fileName,
                    effectivePerson.getDistinguishedName(), extension);
            extractInvoice(file, bytes);
            if(exists(emc, file.getNumber())){
                throw new ExceptionInvoiceExists(file.getNumber());
            }
            emc.check(file, CheckPersistType.all);
            file.saveContent(mapping, bytes, fileName);
            emc.beginTransaction(Invoice.class);
            emc.persist(file);
            emc.commit();
            wo.setId(file.getId());
        }

        result.setData(wo);
        return result;
    }

    public static class Wo extends WoId {

        public Wo() {
        }

        public Wo(String id) throws Exception {
            super(id);
        }
    }
}
