package com.x.processplatform.assemble.designer.jaxrs.itemaccess;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.ItemAccess;
import org.apache.commons.lang3.StringUtils;

class ActionDeleteWithProcessWithPath extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionDeleteWithProcessWithPath.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String processId, String path)
            throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Process process = emc.find(processId, Process.class);
            if (process == null) {
                throw new ExceptionProcessNotExisted(processId);
            }
            Application application = emc.find(process.getApplication(), Application.class);
            if (null == application) {
                throw new ExceptionApplicationNotExist(process.getApplication());
            }
            if (!business.editable(effectivePerson, application)) {
                throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
                        application.getName(), application.getId());
            }
            String itemCategoryId = StringUtils.isNoneEmpty(process.getEdition()) ? process.getEdition() : process.getId();
            ItemAccess itemAccess = emc.firstEqualAndEqual(ItemAccess.class,
                    ItemAccess.itemCategoryId_FIELDNAME, itemCategoryId,
                    ItemAccess.path_FIELDNAME, path);
            if(itemAccess != null){
                emc.beginTransaction(ItemAccess.class);
                emc.remove(itemAccess);
                emc.commit();
                CacheManager.notify(ItemAccess.class);
            }

            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WrapBoolean {

    }

}
