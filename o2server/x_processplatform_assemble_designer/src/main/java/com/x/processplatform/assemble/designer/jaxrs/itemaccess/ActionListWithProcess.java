package com.x.processplatform.assemble.designer.jaxrs.itemaccess;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.ItemAccess;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

class ActionListWithProcess extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionListWithProcess.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String processId)
            throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Process process = emc.find(processId, Process.class);
            if (process == null) {
                throw new ExceptionProcessNotExisted(processId);
            }
            String itemCategoryId = StringUtils.isNoneEmpty(process.getEdition()) ? process.getEdition() : process.getId();
            List<ItemAccess> itemAccessList = emc.listEqual(ItemAccess.class,
                    ItemAccess.itemCategoryId_FIELDNAME, itemCategoryId);
            List<Wo> wos = Wo.copier.copy(itemAccessList);
            result.setData(wos);
            return result;
        }
    }

    public static class Wo extends ItemAccess {
        public static final WrapCopier<ItemAccess, Wo> copier = WrapCopierFactory.wo(ItemAccess.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisibleIncludeProperites));
    }

}
