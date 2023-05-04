package com.x.organization.assemble.personal.jaxrs.custom;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Custom;

class ActionDelete extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String name) throws Exception {

        LOGGER.debug("execute:{}, name:{}.", effectivePerson::getDistinguishedName, () -> name);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            Custom o = this.getWithName(emc, effectivePerson.getDistinguishedName(), name);
            if (null != o) {
                emc.beginTransaction(Custom.class);
                emc.remove(o);
                emc.commit();
                wo.setId(o.getId());
                CacheManager.notify(Custom.class, effectivePerson.getDistinguishedName());
            }
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }
}
