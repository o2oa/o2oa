package com.x.processplatform.assemble.designer.jaxrs.itemaccess;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.core.entity.ItemAccess;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

class ActionSave extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionSave.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getItemCategoryId())) {
                throw new ExceptionFieldEmpty("itemCategoryId");
            }
            if (StringUtils.isEmpty(wi.getPath())) {
                throw new ExceptionFieldEmpty("path");
            }
            Business business = new Business(emc);
            Process process = emc.find(wi.getItemCategoryId(), Process.class);
            if (process == null) {
                throw new ExceptionProcessNotExisted(wi.getItemCategoryId());
            }
            Application application = emc.find(process.getApplication(), Application.class);
            if (null == application) {
                throw new ExceptionApplicationNotExist(process.getApplication());
            }
            if (!business.editable(effectivePerson, application)) {
                throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
                        application.getName(), application.getId());
            }
            wi.setItemCategoryId(
                    StringUtils.isNoneEmpty(process.getEdition()) ? process.getEdition()
                            : process.getId());
            ItemAccess itemAccess = emc.firstEqualAndEqual(ItemAccess.class,
                    ItemAccess.itemCategoryId_FIELDNAME, wi.getItemCategoryId(),
                    ItemAccess.path_FIELDNAME, wi.getPath());
            boolean update = true;
            emc.beginTransaction(ItemAccess.class);
            if (itemAccess == null) {
                itemAccess = new ItemAccess();
                update = false;
            }

            Wi.copier.copy(wi, itemAccess);
            itemAccess.setAppId(process.getApplication());
            itemAccess.setItemCategory(ItemCategory.pp);
            itemAccess.getProperties().setReadActivityIdList(
                    ListTools.extractField(itemAccess.getReaderList(), "activity", String.class,
                            true, true));
            itemAccess.getProperties().setEditActivityIdList(
                    ListTools.extractField(itemAccess.getEditorList(), "activity", String.class,
                            true, true));
            itemAccess.setUpdateTime(new Date());
            if (update) {
                emc.check(itemAccess, CheckPersistType.all);
            } else {
                emc.persist(itemAccess, CheckPersistType.all);
            }
            emc.commit();
            CacheManager.notify(Process.class);
            Wo wo = new Wo();
            wo.setId(itemAccess.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {

    }

    public static class Wi extends ItemAccess {

        private static final long serialVersionUID = -5237741099036357033L;

        static WrapCopier<Wi, ItemAccess> copier = WrapCopierFactory.wi(Wi.class, ItemAccess.class, null,
                ListTools.toList(FieldsUnmodifyIncludePorperties, ItemAccess.appId_FIELDNAME,
                        ItemAccess.itemCategory_FIELDNAME));

    }
}
