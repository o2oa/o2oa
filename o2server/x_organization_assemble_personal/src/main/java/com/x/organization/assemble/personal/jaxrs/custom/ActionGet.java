package com.x.organization.assemble.personal.jaxrs.custom;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Custom;

class ActionGet extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

    ActionResult<String> execute(EffectivePerson effectivePerson, String name) throws Exception {

        LOGGER.debug("execute:{}, name:{}.", effectivePerson::getDistinguishedName, () -> name);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<String> result = new ActionResult<>();
            Custom o = this.getWithName(emc, effectivePerson.getDistinguishedName(), name);
            if (null != o) {
                result.setData(o.getData());
            }
            return result;
        }
    }
}
