package com.x.portal.assemble.designer.jaxrs.scriptversion;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.ScriptVersion;

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
                Portal portal = emc.find(script.getPortal(), Portal.class);
                if (null == portal) {
                    throw new ExceptionEntityNotExist(script.getPortal(), Portal.class);
                }
                if (!business.editable(effectivePerson, portal)) {
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

        private static final long serialVersionUID = 989354587617110385L;
        static WrapCopier<ScriptVersion, Wo> copier = WrapCopierFactory.wo(ScriptVersion.class, Wo.class,
                JpaObject.singularAttributeField(ScriptVersion.class, true, true), null);

    }
}
