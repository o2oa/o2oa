package com.x.processplatform.assemble.surface.jaxrs.process;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

class ActionListWithPersonWithApplication extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithPersonWithApplication.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag) throws Exception {

        LOGGER.debug("execute:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName, () -> applicationFlag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            List<Wo> wos = new ArrayList<>();
            Application application = business.application().pick(applicationFlag);
            if (null == application) {
                throw new ExceptionEntityNotExist(applicationFlag, Application.class);
            }
            List<String> roles = business.organization().role().listWithPerson(effectivePerson);
            List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
            List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
            if (!business.application().allowRead(effectivePerson, roles, identities, units, application)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            List<String> groups = business.organization().group().listWithIdentity(identities);
            List<String> ids = business.process().listStartableWithApplication(effectivePerson, identities, units,
                    groups, application, "");
            for (String id : ids) {
                wos.add(Wo.copier.copy(business.process().pick(id)));
            }
            wos = business.process().sort(wos);
            result.setData(wos);
            return result;
        }
    }

    @Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionListWithPersonWithApplication$Wo")
    public static class Wo extends Process {

        private static final long serialVersionUID = 1521228691441978462L;

        static WrapCopier<Process, Wo> copier = WrapCopierFactory.wo(Process.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
