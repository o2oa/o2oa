package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

/**
 * @author sword
 */
public class ActionGetInfo extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionGetInfo.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            EntityManager em = emc.get(OnlyOfficeFile.class);
            OnlyOfficeFile record = em.find(OnlyOfficeFile.class, id);
            if(record == null){
                throw new ExceptionEntityNotExist(id);
            }
            result.setData(Wo.copier.copy(record));
            return result;
        }
    }


    public static class Wo extends OnlyOfficeFile {
        private static final long serialVersionUID = 4354729965721763291L;

        public static final WrapCopier<OnlyOfficeFile, Wo> copier = WrapCopierFactory.wo( OnlyOfficeFile.class, Wo.class, null, JpaObject.FieldsInvisible);
    }

    public static class Wi extends GsonPropertyObject {

    }
}
