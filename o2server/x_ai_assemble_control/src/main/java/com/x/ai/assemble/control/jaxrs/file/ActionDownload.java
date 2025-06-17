package com.x.ai.assemble.control.jaxrs.file;

import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoFile;
import java.io.ByteArrayOutputStream;

class ActionDownload extends StandardJaxrsAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, boolean stream)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

            ActionResult<Wo> result = new ActionResult<>();
            Wo wo;
            File file = emc.find(id, File.class);
            if (null == file) {
                file = emc.flag(id, File.class);
                if (file == null) {
                    throw new ExceptionEntityNotExist(id);
                }
            }
            if (effectivePerson.isNotManager() && !effectivePerson.getDistinguishedName()
                    .equals(file.getCreator())) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            StorageMapping mapping = ThisApplication.context().storageMappings()
                    .get(File.class, file.getStorage());
            if (null == mapping) {
                throw new ExceptionStorageMappingNotExisted(file.getStorage());
            }
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                file.readContent(mapping, os);
                byte[] bs = os.toByteArray();
                String fastETag = file.getId() + file.getUpdateTime().getTime();
                wo = new Wo(bs, this.contentType(stream, file.getName()),
                        this.contentDisposition(stream, file.getName()), fastETag);
            }
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition, String fastETag) {
            super(bytes, contentType, contentDisposition, fastETag);
        }

    }

}
