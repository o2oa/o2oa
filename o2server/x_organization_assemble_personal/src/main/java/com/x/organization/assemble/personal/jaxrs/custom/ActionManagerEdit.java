package com.x.organization.assemble.personal.jaxrs.custom;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Person;

class ActionManagerEdit extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionManagerEdit.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String person, String name, String wi) throws Exception {

        LOGGER.debug("execute:{}, person:{}, name:{}, wi:{}.", effectivePerson::getDistinguishedName, () -> person,
                () -> name, () -> wi);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            if (effectivePerson.isManager() && StringUtils.isNotEmpty(person)) {
                Person p = business.person().pick(person);
                if (p != null) {
                    Custom custom = this.getWithName(emc, p.getDistinguishedName(), name);
                    emc.beginTransaction(Custom.class);
                    if (null != custom) {
                        custom.setData(wi);
                        emc.check(custom, CheckPersistType.all);
                    } else {
                        custom = new Custom();
                        custom.setPerson(p.getDistinguishedName());
                        custom.setName(name);
                        custom.setData(wi);
                        emc.persist(custom, CheckPersistType.all);
                    }
                    emc.commit();
                    wo.setId(custom.getId());
                }
            }
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }
}
