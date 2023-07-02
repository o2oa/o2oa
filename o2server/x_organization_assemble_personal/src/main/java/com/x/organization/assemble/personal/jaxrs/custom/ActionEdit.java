package com.x.organization.assemble.personal.jaxrs.custom;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Custom;

class ActionEdit extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String name, String wi) throws Exception {

        LOGGER.debug("execute:{}, name:{}, wi:{}.", effectivePerson::getDistinguishedName, () -> name, () -> wi);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            Custom custom = this.getWithName(emc, effectivePerson.getDistinguishedName(), name);
            emc.beginTransaction(Custom.class);
            if (null != custom) {
                custom.setData(wi);
                emc.check(custom, CheckPersistType.all);
            } else {
                custom = new Custom();
                custom.setPerson(effectivePerson.getDistinguishedName());
                custom.setName(name);
                custom.setData(wi);
                emc.persist(custom, CheckPersistType.all);
            }
            emc.commit();
            wo.setId(custom.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }
}
