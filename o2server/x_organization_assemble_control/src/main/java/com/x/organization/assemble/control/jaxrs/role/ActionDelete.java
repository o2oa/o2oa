package com.x.organization.assemble.control.jaxrs.role;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Role;

class ActionDelete extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Role role = business.role().pick(flag);
            if (null == role) {
                throw new ExceptionRoleNotExist(flag);
            }
            if (!business.editable(effectivePerson, role)) {
                throw new ExceptionDenyDeleteRole(effectivePerson, flag);
            }
            if (OrganizationDefinition.DEFAULTROLES.contains(role.getName())) {
                throw new ExceptionDenyDeleteDefaultRole(role.getName());

            }

            emc.beginTransaction(Role.class);
            role = emc.find(role.getId(), Role.class);
            emc.remove(role, CheckRemoveType.all);
            emc.commit();
            CacheManager.notify(Role.class);

            Wo wo = new Wo();
            wo.setId(role.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }

}