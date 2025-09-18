package com.x.processplatform.assemble.designer.jaxrs.itemaccess;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.ItemAccess;

class ActionGet extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id)
            throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            ItemAccess itemAccess = emc.find(id, ItemAccess.class);
            if(itemAccess == null){
                throw new ExceptionEntityNotExist(id, ItemAccess.class);
            }

            Wo wo = Wo.copier.copy(itemAccess);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends ItemAccess {
        public static final WrapCopier<ItemAccess, Wo> copier = WrapCopierFactory.wo(ItemAccess.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisibleIncludeProperites));
    }

}
