package com.x.organization.assemble.control.jaxrs.unitduty;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
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
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;

class ActionEdit extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            UnitDuty o = business.unitDuty().pick(id);
            if (null == o) {
                throw new ExceptionUnitDutyNotExist(id);
            }
            Unit unit = business.unit().pick(o.getUnit());
            if (null == unit) {
                throw new ExceptionUnitNotExist(o.getUnit());
            }
            if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, unit)) {
                throw new ExceptionDenyEditUnit(effectivePerson, unit.getName());
            }
            if (StringUtils.isEmpty(wi.getName())) {
                throw new ExceptionNameEmpty();
            }
            if (this.duplicateOnUnit(business, unit, wi.getName(), o)) {
                throw new ExceptionDuplicateOnUnit(wi.getName(), unit.getName());
            }
            /** pick出来的需要重新find */
            o = emc.find(o.getId(), UnitDuty.class);

            Gson gsontool = new Gson();
            String strDuty = gsontool.toJson(o);

            emc.beginTransaction(UnitDuty.class);
            Wi.copier.copy(wi, o);
            /** 如果唯一标识不为空,要检查唯一标识是否唯一 */
            if (uniqueDuplicateWhenNotEmpty(business, o)) {
                throw new ExceptionDuplicateUnique(o.getName(), o.getUnique());
            }
            o.setUnit(unit.getId());
            o.setIdentityList(
                    ListTools.extractProperty(business.identity().pick(o.getIdentityList()), JpaObject.id_FIELDNAME,
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

    public static class Wi extends UnitDuty {

        private static final long serialVersionUID = -7527954993386512109L;

        static WrapCopier<Wi, UnitDuty> copier = WrapCopierFactory.wi(Wi.class, UnitDuty.class, null,
                ListTools.toList(JpaObject.FieldsUnmodify, "pinyin", "pinyinInitial", "unit"));

    }

}
