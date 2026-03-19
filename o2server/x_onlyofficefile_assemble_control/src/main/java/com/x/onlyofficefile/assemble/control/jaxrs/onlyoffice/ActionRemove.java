package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;

public class ActionRemove extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionRemove.class);

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
        if(effectivePerson.isNotManager()){
            throw new ExceptionAccessDenied(effectivePerson);
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setRequestStatus(true);
            result.setData(wo);
            DocumentManager.Init(request);
			EntityManager em = emc.get(OnlyOfficeFile.class);
			OnlyOfficeFile onlyOfficeFile = em.find(OnlyOfficeFile.class, id);

			if (onlyOfficeFile != null) {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFile.class,
						onlyOfficeFile.getStorage());
				onlyOfficeFile.deleteContent(mapping);

				em.getTransaction().begin();
				em.remove(onlyOfficeFile);
				em.getTransaction().commit();
			} else {
				throw new ExceptionEntityNotExist(id);
			}
            return result;
        }
    }

    public static class Wo extends GsonPropertyObject {

        public boolean requestStatus;
        public String message;

        public boolean isRequestStatus() {
            return requestStatus;
        }

        public void setRequestStatus(boolean requestStatus) {
            this.requestStatus = requestStatus;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Wi extends GsonPropertyObject {


    }
}
