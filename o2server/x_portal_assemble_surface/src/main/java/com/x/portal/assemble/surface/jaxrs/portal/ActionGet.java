package com.x.portal.assemble.surface.jaxrs.portal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;

class ActionGet extends BaseAction {

    /**
     * 1.身份在可使用列表中 2.部门在可使用部门中 3.公司在可使用公司中 4.没有限定身份,部门或者公司 5.个人在应用管理员中
     * 6.是此Portal的创建人员 7.个人有Manage权限 8.个人拥有PortalManager
     */
    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            ActionResult<Wo> result = new ActionResult<>();
            Portal o = business.portal().pick(flag);
            if (null == o) {
                throw new ExceptionPortalNotExist(flag);
            }
            if (!business.portal().visible(effectivePerson, o)) {
                throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), o.getName(), o.getId());
            }
            Wo wo = Wo.copier.copy(o);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends Portal {

        private static final long serialVersionUID = 727247858785981924L;

        static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible, Portal.controllerList_FIELDNAME,
                        Portal.lastUpdatePerson_FIELDNAME, Portal.creatorPerson_FIELDNAME,
                        Portal.availableGroupList_FIELDNAME, Portal.availableIdentityList_FIELDNAME,
                        Portal.availableUnitList_FIELDNAME));

    }
}