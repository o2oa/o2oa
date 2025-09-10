package com.x.processplatform.assemble.designer.jaxrs.itemaccess;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapIdList;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.ItemAccess;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

class ActionBachSave extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionBachSave.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            if (!business.editable(effectivePerson, null)) {
                throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
            }
            List<String> pathList = new ArrayList<>();
            for(WiItemAccess wiItemAccess : wi.getItemAccessList()) {
                if (StringUtils.isEmpty(wiItemAccess.getItemCategoryId()) || StringUtils.isEmpty(wiItemAccess.getPath())) {
                    throw new ExceptionFieldEmpty("itemCategoryId or path");
                }
                String appId = wiItemAccess.getItemCategoryId();
                Process process = emc.find(wiItemAccess.getItemCategoryId(), Process.class);
                if (process != null) {
                    wiItemAccess.setItemCategoryId(
                            StringUtils.isNoneEmpty(process.getEdition()) ? process.getEdition()
                                    : process.getId());
                    appId = process.getApplication();
                }
                Application application = emc.find(appId, Application.class);
                if (null == application) {
                    throw new ExceptionEntityNotExist(appId);
                }

                ItemAccess itemAccess = emc.firstEqualAndEqual(ItemAccess.class,
                        ItemAccess.itemCategoryId_FIELDNAME, wiItemAccess.getItemCategoryId(),
                        ItemAccess.path_FIELDNAME, wiItemAccess.getPath());
                boolean update = true;
                emc.beginTransaction(ItemAccess.class);
                if (itemAccess == null) {
                    itemAccess = new ItemAccess();
                    update = false;
                }

                WiItemAccess.copier.copy(wiItemAccess, itemAccess);
                itemAccess.setAppId(appId);
                itemAccess.setItemCategory(ItemCategory.pp);
                itemAccess.getProperties().setReadActivityIdList(
                        ListTools.extractField(itemAccess.getReaderList(),
                                Activity.unique_FIELDNAME, String.class,
                                true, true));
                itemAccess.getProperties().setEditActivityIdList(
                        ListTools.extractField(itemAccess.getEditorList(),
                                Activity.unique_FIELDNAME, String.class,
                                true, true));
                itemAccess.setUpdateTime(new Date());
                if (update) {
                    emc.check(itemAccess, CheckPersistType.all);
                } else {
                    emc.persist(itemAccess, CheckPersistType.all);
                }
                emc.commit();
                pathList.add(itemAccess.getPath());
            }
            CacheManager.notify(ItemAccess.class);
            Wo wo = new Wo();
            wo.setValueList(pathList);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WrapStringList {

    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("权限对象列表，对象内容参照单个保存.")
        private List<WiItemAccess> itemAccessList;

        public List<WiItemAccess> getItemAccessList() {
            return itemAccessList == null ? Collections.emptyList() : itemAccessList;
        }

        public void setItemAccessList(
                List<WiItemAccess> itemAccessList) {
            this.itemAccessList = itemAccessList;
        }
    }

    public static class WiItemAccess extends ItemAccess {

        private static final long serialVersionUID = -5237741099036357033L;

        static WrapCopier<WiItemAccess, ItemAccess> copier = WrapCopierFactory.wi(WiItemAccess.class, ItemAccess.class, null,
                ListTools.toList(FieldsUnmodifyIncludePorperties, ItemAccess.appId_FIELDNAME,
                        ItemAccess.itemCategory_FIELDNAME));

    }
}
