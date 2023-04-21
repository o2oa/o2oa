package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.MessageFactory;
import com.x.processplatform.core.entity.element.Application;

class ActionCreate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            // 这里的角色多一个 RoleDefinition.ProcessPlatformCreator
            if ((effectivePerson.isNotManager()) && (!business.organization().person().hasRole(effectivePerson,
                    OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager,
                    OrganizationDefinition.ProcessPlatformCreator))) {
                throw new ExceptionInsufficientPermission(effectivePerson.getDistinguishedName());
            }
            if (emc.duplicateWithFlags(Application.class, wi.getId())) {
                throw new ExceptionEntityExist(wi.getId());
            }
            emc.beginTransaction(Application.class);
            Application application = new Application();
            Wi.copier.copy(wi, application);
            application.setCreatorPerson(effectivePerson.getDistinguishedName());
            application.setLastUpdatePerson(effectivePerson.getDistinguishedName());
            application.setLastUpdateTime(new Date());
            // 如果是管理员就不加入到管理者
            if (!effectivePerson.isManager()) {
                application.setControllerList(
                        ListTools.add(application.getControllerList(), true, true,
                                effectivePerson.getDistinguishedName()));
            }
            emc.persist(application, CheckPersistType.all);
            emc.commit();
            CacheManager.notify(Application.class);
            Wo wo = new Wo();
            wo.setId(application.getId());
            result.setData(wo);
            MessageFactory.application_create(application);
            return result;
        }
    }

    public static class Wo extends WoId {

    }

    public static class Wi extends Application {

        private static final long serialVersionUID = 6624639107781167248L;

        static WrapCopier<Wi, Application> copier = WrapCopierFactory.wi(Wi.class, Application.class, null,
                FieldsUnmodifyIncludePorperties);

    }

}
