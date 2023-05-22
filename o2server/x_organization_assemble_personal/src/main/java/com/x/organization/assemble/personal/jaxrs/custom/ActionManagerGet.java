package com.x.organization.assemble.personal.jaxrs.custom;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Person;

class ActionManagerGet extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionManagerGet.class);

    ActionResult<String> execute(EffectivePerson effectivePerson, String person, String name) throws Exception {

        LOGGER.debug("execute:{}, person:{}, name:{}.", effectivePerson::getDistinguishedName, () -> person,
                () -> name);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<String> result = new ActionResult<>();
            Business business = new Business(emc);
            if (effectivePerson.isManager() && StringUtils.isNotEmpty(person)) {
                Person p = business.person().pick(person);
                if (p != null) {
                    Custom o = this.getWithName(emc, p.getDistinguishedName(), name);
                    if (null != o) {
                        result.setData(o.getData());
                    }
                }
            }
            return result;
        }
    }
}
