package com.x.processplatform.assemble.designer.jaxrs.itemaccess;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
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

class ActionListWithPath extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionListWithPath.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String path)
            throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();

            List<ItemAccess> itemAccessList = emc.listEqual(ItemAccess.class,
                    ItemAccess.path_FIELDNAME, path);
            List<Wo> wos = Wo.copier.copy(itemAccessList);
            for (Wo wo : wos) {
                Process process = emc.find(wo.getItemCategoryId(), Process.class);
                if (null != process) {
                    wo.setProcessName(process.getName());
                }
            }
            result.setData(wos);
            return result;
        }
    }

    public static class Wo extends ItemAccess {
        public static final WrapCopier<ItemAccess, Wo> copier = WrapCopierFactory.wo(ItemAccess.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisibleIncludeProperites));

        @FieldDescribe("流程名称.")
        private String processName;

        public String getProcessName() {
            return processName;
        }

        public void setProcessName(String processName) {
            this.processName = processName;
        }
    }

}
