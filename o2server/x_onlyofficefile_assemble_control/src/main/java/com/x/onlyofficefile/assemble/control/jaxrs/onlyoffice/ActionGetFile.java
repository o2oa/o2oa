package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

public class ActionGetFile extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionGetFile.class);

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id, String version, String fileName) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            DocumentManager.Init(request);

            EntityManager em = emc.get(OnlyOfficeFile.class);
            OnlyOfficeFile record = em.find(OnlyOfficeFile.class, id);

            if (record != null) {
                StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFile.class,
                        record.getStorage());
                byte[] bt = record.readContent(mapping);
                if(StringUtils.isBlank(fileName)) {
                    fileName = record.getFileName();
                }

                Wo wo = new Wo(bt, this.contentType(true, fileName), this.contentDisposition(true, fileName));
                result.setData(wo);
            } else {
                throw new ExceptionEntityNotExist(id);
            }
            return result;
        }
    }


    public static class Wo extends WoFile {
        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }

    public static class Wi extends GsonPropertyObject {
    }
}
