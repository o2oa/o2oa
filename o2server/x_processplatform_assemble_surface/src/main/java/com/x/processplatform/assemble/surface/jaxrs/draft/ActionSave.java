package com.x.processplatform.assemble.surface.jaxrs.draft;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Draft;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.draft.ActionSaveWi;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionSave extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionSave.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            String identity = this.decideCreatorIdentity(business, effectivePerson, wi.getIdentity());
            String unit = business.organization().unit().getWithIdentity(identity);
            String person = business.organization().person().getWithIdentity(identity);
            List<String> identities = business.organization().identity().listWithPerson(person);
            List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
            Application application = business.application().pick(wi.getWork().getApplication());
            if (null == application) {
                throw new ExceptionEntityNotExist(wi.getWork().getApplication(), Application.class);
            }
            Process process = business.process().pick(wi.getWork().getProcess());
            if (null == process) {
                throw new ExceptionEntityNotExist(wi.getWork().getProcess(), Process.class);
            }
            List<String> groups = business.organization().group().listWithIdentity(identities);
            if (!business.process().startable(effectivePerson, identities, units, groups, process)) {
                throw new ExceptionAccessDenied(effectivePerson, process);
            }
            emc.beginTransaction(Draft.class);
            Draft draft = null;
            if (StringUtils.isEmpty(wi.getWork().getId())) {
                draft = new Draft();
                this.update(draft, wi, application, process, person, identity, unit);
                emc.persist(draft, CheckPersistType.all);
            } else {
                draft = emc.find(wi.getWork().getId(), Draft.class);
                if (null == draft) {
                    throw new ExceptionEntityNotExist(wi.getWork().getId(), Draft.class);
                }
                this.update(draft, wi, application, process, person, identity, unit);
                emc.check(draft, CheckPersistType.all);
            }
            emc.commit();
            Wo wo = new Wo();
            wo.setId(draft.getId());
            result.setData(wo);
            return result;
        }
    }

    private void update(Draft draft, Wi wi, Application application, Process process, String person, String identity,
            String unit) {
        draft.setApplication(application.getId());
        draft.setApplicationAlias(application.getAlias());
        draft.setApplicationName(application.getName());
        draft.setProcess(process.getId());
        draft.setProcessAlias(process.getAlias());
        draft.setProcessName(process.getName());
        draft.setPerson(person);
        draft.setIdentity(identity);
        draft.setUnit(unit);
        String title = wi.getWork().getTitle();
        if (null != wi.getData()) {
            Object value = wi.getData().getOrDefault("subject", null);
            if (null != value) {
                title = Objects.toString(value);
            } else {
                value = wi.getData().getOrDefault("title", null);
                if (null != value) {
                    title = Objects.toString(value);
                }
            }
        }
        draft.setTitle(title);
        draft.getProperties().setData(wi.getData());
    }

    @Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionSave$Wi")
    public static class Wi extends ActionSaveWi {

        private static final long serialVersionUID = -8146304190261571771L;

    }

    @Schema(name = "com.x.processplatform.assemble.surface.jaxrs.draft.ActionSave$Wo")
    public static class Wo extends WoId {

        private static final long serialVersionUID = -7797598703971806334L;

    }

}
