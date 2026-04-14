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

/**
 * @author sword
 */
public class ActionGetFileDiff extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionGetFileDiff.class);

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer version) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            DocumentManager.Init(request);
            EntityManager em = emc.get(OnlyOfficeFile.class);
            OnlyOfficeFile record = em.find(OnlyOfficeFile.class, id);

            //断判url参数xtoken
            if (DocumentManager.tokenEnabled() && effectivePerson.isAnonymous()) {
                String xtoken = request.getParameter(Business.TOKEN_NAME);
                if (!record.getFileToken().equalsIgnoreCase(xtoken)) {
                    throw new Exception("不允许访问,xtoken值不对");
                }
            }

            if (record != null) {
				OnlyOfficeFileVersion diffVersion = emc.firstEqualAndEqualAndEqual(OnlyOfficeFileVersion.class, OnlyOfficeFileVersion.fileId_FIELDNAME, id,
                        OnlyOfficeFileVersion.fileVersion_FIELDNAME, version, OnlyOfficeFileVersion.fileType_FIELDNAME, OnlyOfficeFileVersion.FILE_TYPE_DIFF);
				if(diffVersion!=null){
				    String fileName = "changes.zip";
					StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFileVersion.class,
                            diffVersion.getStorage());

					byte[] bt = diffVersion.readContent(mapping);
					Wo wo = new Wo(bt, this.contentType(true, fileName), this.contentDisposition(true, fileName));
					result.setData(wo);
				}else{
					throw new ExceptionEntityNotExist(id + ",版本为："+version);
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
