package com.x.processplatform.assemble.designer.jaxrs.script;

import java.util.Arrays;
import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
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
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.ScriptVersion;

class ActionCreate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            Application application = emc.find(wi.getApplication(), Application.class);
            if (null == application) {
                throw new ExceptionApplicationNotExist(wi.getApplication());
            }
            if (!business.editable(effectivePerson, application)) {
                throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
                        application.getName(), application.getId());
            }
            if (emc.duplicateWithFlags(Script.class, wi.getId())) {
                throw new ExceptionEntityExist(wi.getId());
            }
            emc.beginTransaction(Script.class);
            Script script = new Script();

            Wi.copier.copy(wi, script);
            script.setCreatorPerson(effectivePerson.getDistinguishedName());
            script.setLastUpdatePerson(effectivePerson.getDistinguishedName());
            script.setLastUpdateTime(new Date());
            emc.persist(script, CheckPersistType.all);
            emc.commit();
            CacheManager.notify(Script.class);
            // 保存版本
            ThisApplication.scriptVersionQueue.send(new ScriptVersion(script.getId(), jsonElement));
            Wo wo = new Wo();
            wo.setId(script.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }

    public static class Wi extends Script {

        private static final long serialVersionUID = -5237741099036357033L;

        static WrapCopier<Wi, Script> copier = WrapCopierFactory.wi(Wi.class, Script.class, null, Arrays
                .asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
                        JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));

    }
}
