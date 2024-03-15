package com.x.portal.assemble.designer.jaxrs.pageversion;

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
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.PageVersion;
import com.x.portal.core.entity.Portal;

import java.util.ArrayList;
import java.util.List;

class ActionListWithScript extends BaseAction {

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String pageId) throws Exception {

        ActionResult<List<Wo>> result = new ActionResult<>();
        List<Wo> wos = new ArrayList<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Page page = emc.find(pageId, Page.class);
            if (null != page) {
                Portal portal = emc.find(page.getPortal(), Portal.class);
                if (null == portal) {
                    throw new ExceptionEntityNotExist(page.getPortal(), Portal.class);
                }
                if (!business.editable(effectivePerson, portal)) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
                wos = emc.fetchEqual(PageVersion.class, Wo.copier, PageVersion.page_FIELDNAME,
                        page.getId());
            }
            result.setData(wos);
            return result;
        }
    }

    public static class Wo extends PageVersion {

        private static final long serialVersionUID = -603437844275649558L;
        static WrapCopier<PageVersion, Wo> copier = WrapCopierFactory.wo(PageVersion.class, Wo.class,
                JpaObject.singularAttributeField(PageVersion.class, true, true), null);

    }
}
