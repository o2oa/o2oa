package com.x.organization.assemble.control.jaxrs.unitduty;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

class ActionUpdateMember extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateMember.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if(StringUtils.isBlank(wi.getUnitDuty())){
                throw new ExceptionFieldEmpty("unitDuty");
            }
            if(wi.getIdentityList() == null){
                throw new ExceptionFieldEmpty("identityList");
            }
            Business business = new Business(emc);
            UnitDuty o = business.unitDuty().pick(wi.getUnitDuty());
            Unit unit;
            if (null == o) {
                if(StringUtils.isBlank(wi.getUnit())){
                    throw new ExceptionFieldEmpty("unit");
                }
                unit = business.unit().pick(wi.getUnit());
                if (null == unit) {
                    throw new ExceptionUnitNotExist(wi.getUnit());
                }
                o = emc.firstEqualAndEqual(UnitDuty.class, UnitDuty.unit_FIELDNAME, unit.getId(), UnitDuty.name_FIELDNAME, wi.getUnitDuty());
                if(o == null){
                    throw new ExceptionUnitDutyNotExist(wi.getUnitDuty());
                }
            }else{
                unit = business.unit().pick(o.getUnit());
                if (null == unit) {
                    throw new ExceptionUnitNotExist(o.getUnit());
                }
            }
            if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, unit)) {
                throw new ExceptionDenyEditUnit(effectivePerson, unit.getName());
            }
            o = emc.find(o.getId(), UnitDuty.class);
            emc.beginTransaction(UnitDuty.class);
            o.setIdentityList(
                    ListTools.extractProperty(business.identity().pick(wi.getIdentityList()), JpaObject.id_FIELDNAME,
                            String.class, true, true));
            emc.check(o, CheckPersistType.all);
            emc.commit();
            CacheManager.notify(UnitDuty.class);

            Wo wo = new Wo();
            wo.setId(o.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -7527954993386512109L;

        @FieldDescribe("职务所属组织：组织的dn、unique或id.")
        private String unit;

        @FieldDescribe("职务：职务名称(此时所属组织不能为空)、dn、unique或id.")
        private String unitDuty;

        @FieldDescribe("职务身份成员列表，身份的dn、unique或id.")
        private List<String> identityList;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getUnitDuty() {
            return unitDuty;
        }

        public void setUnitDuty(String unitDuty) {
            this.unitDuty = unitDuty;
        }

        public List<String> getIdentityList() {
            return identityList == null ? Collections.emptyList() : identityList ;
        }

        public void setIdentityList(List<String> identityList) {
            this.identityList = identityList;
        }
    }

}
