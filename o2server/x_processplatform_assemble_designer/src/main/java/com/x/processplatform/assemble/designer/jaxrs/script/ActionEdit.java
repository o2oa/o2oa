package com.x.processplatform.assemble.designer.jaxrs.script;

import java.util.Arrays;
import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.ScriptVersion;

class ActionEdit extends BaseAction {
    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            Script script = emc.find(id, Script.class);
            if (null == script) {
                throw new ExceptionScriptNotExist(id);
            }
            Application application = emc.find(script.getApplication(), Application.class);
            if (null == application) {
                throw new ExceptionApplicationNotExist(script.getApplication());
            }
            if (!business.editable(effectivePerson, application)) {
                throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
                        application.getName(), application.getId());
            }
            emc.beginTransaction(Script.class);
            Wi.copier.copy(wi, script);
            script.setLastUpdatePerson(effectivePerson.getDistinguishedName());
            script.setLastUpdateTime(new Date());
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

        static WrapCopier<Wi, Script> copier = WrapCopierFactory.wi(Wi.class, Script.class, null,
                Arrays.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
                        JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));

    }
}
