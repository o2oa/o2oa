package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.ActionProcessingWo;

class ActionProcessing extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionProcessing.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

        LOGGER.debug("execute;{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

        ActionResult<Wo> result = new ActionResult<>();
        Work work = null;

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            work = emc.find(id, Work.class);
            if (null == work) {
                throw new ExceptionWorkNotExist(id);
            }
            emc.beginTransaction(Work.class);
            /* 标识数据被修改 */
            work.setDataChanged(true);
            emc.commit();
        }
        Wo wo = ThisApplication.context().applications()
                .putQuery(effectivePerson.getDebugger(), x_processplatform_service_processing.class,
                        Applications.joinQueryUri("work", work.getId(), "processing"), null, work.getJob())
                .getData(Wo.class);
        result.setData(wo);
        return result;
    }

    public static class Wo extends ActionProcessingWo {

		private static final long serialVersionUID = -2704637716301253584L;

    }

}