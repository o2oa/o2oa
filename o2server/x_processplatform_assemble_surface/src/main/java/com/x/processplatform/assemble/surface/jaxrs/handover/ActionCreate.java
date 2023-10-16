package com.x.processplatform.assemble.surface.jaxrs.handover;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Handover;
import com.x.processplatform.core.entity.content.HandoverStatusEnum;
import org.apache.commons.lang3.StringUtils;

class ActionCreate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            if (!business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")) {
                throw new ExceptionAccessDenied(effectivePerson);
            }
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

            wi.setCreator(effectivePerson.getDistinguishedName());
            if(StringUtils.isBlank(wi.getType())){
                throw new ExceptionFieldEmpty(Handover.type_FIELDNAME);
            }
            if(StringUtils.isBlank(wi.getScheme())){
                throw new ExceptionFieldEmpty(Handover.scheme_FIELDNAME);
            }
            if(StringUtils.isBlank(wi.getPerson())){
                throw new ExceptionFieldEmpty(Handover.person_FIELDNAME);
            }
            if(!OrganizationDefinition.isPersonDistinguishedName(wi.getPerson())){
                throw new ExceptionPersonInvalid();
            }
            if(StringUtils.isBlank(wi.getTargetIdentity())){
                throw new ExceptionFieldEmpty(Handover.person_FIELDNAME);
            }
            String identity = business.organization().identity().get(wi.getTargetIdentity());
            if(StringUtils.isBlank(identity)){
                throw new ExceptionPersonNotExist(wi.getTargetIdentity());
            }
            wi.setTargetIdentity(identity);
            wi.setTargetPerson(business.organization().person().getWithIdentity(identity));

            emc.beginTransaction(Handover.class);
            Handover handover = new Handover();
            wi.copyTo(handover, JpaObject.FieldsUnmodifyIncludePorperties);
            handover.setStatus(HandoverStatusEnum.WAIT.getValue());
            emc.persist(handover, CheckPersistType.all);
            emc.commit();
            Wo wo = new Wo();
            wo.setId(handover.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }

    public static class Wi extends Handover {

        static WrapCopier<Wi, Handover> copier = WrapCopierFactory.wi(Wi.class, Handover.class, null,
                ListTools.toList(JpaObject.FieldsUnmodifyIncludePorperties, Handover.creator_FIELDNAME,
                        Handover.targetPerson_FIELDNAME, Handover.status_FIELDNAME, Handover.handoverJobList_FIELDNAME));

    }
}
