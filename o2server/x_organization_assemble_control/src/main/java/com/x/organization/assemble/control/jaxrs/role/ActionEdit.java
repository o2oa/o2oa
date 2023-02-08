package com.x.organization.assemble.control.jaxrs.role;

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
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Role;

class ActionEdit extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            Role role = business.role().pick(flag);
            if (null == role) {
                throw new ExceptionRoleNotExist(flag);
            }
            if (!business.editable(effectivePerson, role)) {
                throw new ExceptionDenyEditRole(effectivePerson, role.getName());
            }
            if (StringUtils.isEmpty(wi.getName())) {
                throw new ExceptionNameEmpty();
            }

            role = emc.find(role.getId(), Role.class);
            if (OrganizationDefinition.DEFAULTROLES.contains(role.getName()) && !role.getName().equals(wi.getName())) {
                throw new ExceptionDenyUpdateDefaultRole(role.getName());
            }

            Gson gsontool = new Gson();
            String strRole = gsontool.toJson(role);

            Wi.copier.copy(wi, role);
            /** 如果唯一标识不为空,要检查唯一标识是否唯一 */
            if (this.uniqueDuplicateWhenNotEmpty(business, role)) {
                throw new ExceptionDuplicateUnique(role.getName(), role.getUnique());
            }
            role.setPersonList(
                    ListTools.extractProperty(business.person().pick(ListTools.trim(role.getPersonList(), true, true)),
                            JpaObject.id_FIELDNAME, String.class, true, true));
            role.setGroupList(
                    ListTools.extractProperty(business.group().pick(ListTools.trim(role.getGroupList(), true, true)),
                            JpaObject.id_FIELDNAME, String.class, true, true));
            emc.beginTransaction(Role.class);
            emc.check(role, CheckPersistType.all);
            emc.commit();
            CacheManager.notify(Role.class);

            Wo wo = new Wo();
            wo.setId(role.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }

    public static class Wi extends Role {

        private static final long serialVersionUID = -6314932919066148113L;

        static WrapCopier<Wi, Role> copier = WrapCopierFactory.wi(Wi.class, Role.class, null,
                ListTools.toList(JpaObject.FieldsUnmodify, "pinyin", "pinyinInitial"));

    }

}
