package com.x.ai.assemble.control.jaxrs.config;

import com.x.ai.core.entity.AiModel;
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
import java.util.List;

class ActionListEnableModel extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionListEnableModel.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        ActionResult<List<Wo>> result = new ActionResult<>();

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<Wo> list = emc.fetchEqual(AiModel.class, Wo.copier, AiModel.enable_FIELDNAME,  true);
            result.setData(list);
        }
        return result;
    }


    public static class Wo extends AiModel {
        static WrapCopier<AiModel, Wo> copier = WrapCopierFactory.wo(AiModel.class, Wo.class,
                ListTools.toList(AiModel.name_FIELDNAME, AiModel.type_FIELDNAME, AiModel.asDefault_FIELDNAME, AiModel.desc_FIELDNAME),
                JpaObject.FieldsInvisible);
    }

}
