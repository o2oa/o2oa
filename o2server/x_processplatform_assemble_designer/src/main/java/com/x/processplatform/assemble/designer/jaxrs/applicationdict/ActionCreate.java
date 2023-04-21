package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;

class ActionCreate extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Business business = new Business(emc);
            Application application = emc.find(wi.getApplication(), Application.class);
            if (null == application) {
                throw new ApplicationDictNotExistedException(wi.getApplication());
            }
            if (!business.editable(effectivePerson, application)) {
                throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
                        application.getName(), application.getId());
            }
            if (emc.duplicateWithFlags(ApplicationDict.class, wi.getId())) {
                throw new ExceptionEntityExist(wi.getId());
            }
            emc.beginTransaction(ApplicationDict.class);
            emc.beginTransaction(ApplicationDictItem.class);
            ApplicationDict applicationDict = new ApplicationDict();
            Wi.copier.copy(wi, applicationDict);
            applicationDict.setApplication(application.getId());
            emc.persist(applicationDict, CheckPersistType.all);
            DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
            List<ApplicationDictItem> list = converter.disassemble(wi.getData());
            for (ApplicationDictItem o : list) {
                o.setBundle(applicationDict.getId());
                o.setDistributeFactor(applicationDict.getDistributeFactor());
                o.setApplication(application.getId());
                o.setItemCategory(ItemCategory.pp_dict);
                emc.persist(o, CheckPersistType.all);
            }
            emc.commit();
            CacheManager.notify(ApplicationDict.class);
            Wo wo = new Wo();
            wo.setId(applicationDict.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = -2714867252705825023L;
    }

    public static class Wi extends ApplicationDict {

        private static final long serialVersionUID = 7020926328082641485L;

        static WrapCopier<Wi, ApplicationDict> copier = WrapCopierFactory.wi(Wi.class, ApplicationDict.class, null,
                Arrays.asList(JpaObject.createTime_FIELDNAME, JpaObject.updateTime_FIELDNAME,
                        JpaObject.sequence_FIELDNAME, JpaObject.distributeFactor_FIELDNAME));

        @FieldDescribe("字典数据(json格式).")
        private JsonElement data;

        public JsonElement getData() {
            return data;
        }

        public void setData(JsonElement data) {
            this.data = data;
        }

    }

}