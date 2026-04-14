package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.file;

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
import com.x.onlyofficefile.assemble.control.Business;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import com.x.onlyofficefile.core.entity.OnlyOfficeFileVersion;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class ActionGetFile extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionGetFile.class);

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer version, String fileName) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            DocumentManager.Init(request);
            EntityManager em = emc.get(OnlyOfficeFile.class);
            OnlyOfficeFile record = em.find(OnlyOfficeFile.class, id);
            if (DocumentManager.tokenEnabled() && effectivePerson.isAnonymous()) {
                String xtoken = request.getParameter(Business.TOKEN_NAME);
                if (xtoken == null) {
                    throw new Exception("不允许访问,xtoken值为空");
                }
                if (!record.getFileToken().equalsIgnoreCase(xtoken)) {
                    throw new Exception("不允许访问,xtoken值不对");
                }
            }

            if (record != null) {
                if(version.intValue() == 0) {
                    if(StringUtils.isBlank(fileName)) {
                        fileName = record.getFileName();
                    }
                    StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFile.class,
                            record.getStorage());

                    byte[] bt = record.readContent(mapping);
                    Wo wo = new Wo(bt, this.contentType(true, fileName), this.contentDisposition(true, fileName));
                    result.setData(wo);
                }else{
                    OnlyOfficeFileVersion fileVersion = emc.firstEqualAndEqualAndEqual(OnlyOfficeFileVersion.class, OnlyOfficeFileVersion.fileId_FIELDNAME, id,
                            OnlyOfficeFileVersion.fileVersion_FIELDNAME, version, OnlyOfficeFileVersion.fileType_FIELDNAME, OnlyOfficeFileVersion.FILE_TYPE_FILE);
                    if(fileVersion!=null){
                        if(StringUtils.isBlank(fileName)) {
                            fileName = fileVersion.getFileName();
                        }
                        StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFileVersion.class,
                                fileVersion.getStorage());
                        byte[] bt = fileVersion.readContent(mapping);
                        Wo wo = new Wo(bt, this.contentType(true, fileName), this.contentDisposition(true, fileName));
                        result.setData(wo);
                    }else{
                        if(StringUtils.isBlank(fileName)) {
                            fileName = record.getFileName();
                        }
                        StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFile.class,
                                record.getStorage());
                        byte[] bt = record.readContent(mapping);
                        Wo wo = new Wo(bt, this.contentType(true, fileName), this.contentDisposition(true, fileName));
                        result.setData(wo);
                    }
                }
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
