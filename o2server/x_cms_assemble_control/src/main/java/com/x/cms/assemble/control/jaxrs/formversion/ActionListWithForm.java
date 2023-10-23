package com.x.cms.assemble.control.jaxrs.formversion;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.FormVersion;

import java.util.ArrayList;
import java.util.List;

class ActionListWithForm extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithForm.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String formId) throws Exception {

        LOGGER.debug("execute:{}, formId:{}.", effectivePerson::getDistinguishedName, () -> formId);

        ActionResult<List<Wo>> result = new ActionResult<>();
        List<Wo> wos = new ArrayList<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Form form = emc.find(formId, Form.class);
            if (null != form) {
                AppInfo application = emc.find(form.getAppId(), AppInfo.class);
                if (null == application) {
                    throw new ExceptionEntityNotExist(form.getAppId(), AppInfo.class);
                }
                if (!business.editable(effectivePerson, application)) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
                wos = emc.fetchEqual(FormVersion.class, Wo.copier, FormVersion.form_FIELDNAME, form.getId());
                SortTools.desc(wos, JpaObject.createTime_FIELDNAME);
            }
            result.setData(wos);
            return result;
        }
    }

    public static class Wo extends FormVersion {

        private static final long serialVersionUID = -3366438437885307310L;
        static WrapCopier<FormVersion, Wo> copier = WrapCopierFactory.wo(FormVersion.class, Wo.class,
                JpaObject.singularAttributeField(FormVersion.class, true, true), null);

    }
}
