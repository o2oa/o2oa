package com.x.cms.assemble.control.jaxrs.scriptversion;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.ScriptVersion;

import java.util.ArrayList;
import java.util.List;

class ActionListWithScript extends BaseAction {

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String scriptId) throws Exception {

        ActionResult<List<Wo>> result = new ActionResult<>();
        List<Wo> wos = new ArrayList<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Script script = emc.find(scriptId, Script.class);
            if (null != script) {
                AppInfo application = emc.find(script.getAppId(), AppInfo.class);
                if (null == application) {
                    throw new ExceptionEntityNotExist(script.getAppId(), AppInfo.class);
                }
                if (!business.editable(effectivePerson, application)) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
                wos = emc.fetchEqual(ScriptVersion.class, Wo.copier, ScriptVersion.script_FIELDNAME,
                        script.getId());
            }
            result.setData(wos);
            return result;
        }
    }

    public static class Wo extends ScriptVersion {

        private static final long serialVersionUID = -2398096870126935605L;
        static WrapCopier<ScriptVersion, Wo> copier = WrapCopierFactory.wo(ScriptVersion.class, Wo.class,
                JpaObject.singularAttributeField(ScriptVersion.class, true, true), null);

    }
}
