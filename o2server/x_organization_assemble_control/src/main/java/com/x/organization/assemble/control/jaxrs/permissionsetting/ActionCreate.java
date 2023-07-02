package com.x.organization.assemble.control.jaxrs.permissionsetting;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.PermissionSetting;

class ActionCreate extends BaseAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();

            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            PermissionSetting permission = new PermissionSetting();
            Wi.copier.copy(wi, permission);
            emc.beginTransaction(PermissionSetting.class);
            emc.persist(permission, CheckPersistType.all);
            emc.commit();
            CacheManager.notify(PermissionSetting.class);

            Wo wo = new Wo();
            wo.setId(permission.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }

    public static class Wi extends PermissionSetting {

        private static final long serialVersionUID = -6314932919066148113L;

        static WrapCopier<Wi, PermissionSetting> copier = WrapCopierFactory.wi(Wi.class, PermissionSetting.class, null,
                ListTools.toList(JpaObject.FieldsUnmodify));

    }

}
