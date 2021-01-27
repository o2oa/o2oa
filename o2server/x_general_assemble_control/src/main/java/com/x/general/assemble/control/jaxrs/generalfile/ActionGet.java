package com.x.general.assemble.control.jaxrs.generalfile;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.general.assemble.control.ThisApplication;
import com.x.general.core.entity.GeneralFile;


public class ActionGet extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

    protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
        logger.debug(effectivePerson, "flag:{}.", flag);
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            GeneralFile generalFile = emc.find(flag, GeneralFile.class);
            if(generalFile!=null){
                StorageMapping gfMapping = ThisApplication.context().storageMappings().get(GeneralFile.class,
                        generalFile.getStorage());
                wo = new Wo(generalFile.readContent(gfMapping), this.contentType(false, generalFile.getName()),
                        this.contentDisposition(false, generalFile.getName()));
                result.setData(wo);
            } else {
                throw new ExceptionInputFileObject(flag);
            }

        }
        result.setData(wo);
        return result;
    }

    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }

}
