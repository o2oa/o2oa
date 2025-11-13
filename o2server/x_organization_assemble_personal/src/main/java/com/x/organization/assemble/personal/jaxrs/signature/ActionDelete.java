package com.x.organization.assemble.personal.jaxrs.signature;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Custom;

class ActionDelete extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

        LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            Custom o = emc.find(id, Custom.class);
            if (null == o) {
                throw new ExceptionEntityNotExist(id);
            }
            if(!o.getPerson().equals(effectivePerson.getDistinguishedName()) && effectivePerson.isNotManager()){
                throw new ExceptionAccessDenied(effectivePerson);
            }
            emc.beginTransaction(Custom.class);
            emc.remove(o);
            emc.commit();
            wo.setId(o.getId());
            CacheManager.notify(Custom.class, effectivePerson.getDistinguishedName());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }
}
