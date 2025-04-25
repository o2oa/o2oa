package com.x.processplatform.assemble.surface.jaxrs.taskprocessmode;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.TaskProcessMode;

class ActionDelete extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

        LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);
        ActionResult<Wo> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            TaskProcessMode mode = emc.find(id, TaskProcessMode.class);
            if (null == mode) {
                throw new ExceptionEntityNotExist(id, TaskProcessMode.class);
            }
            if (!effectivePerson.getUnique().equals(mode.getPerson())
                    && !business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")) {
                throw new ExceptionAccessDenied(effectivePerson, mode);
            }
            emc.beginTransaction(TaskProcessMode.class);
            emc.remove(mode);
            emc.commit();
        }

        Wo wo = new Wo();
        wo.setId(id);
        result.setData(wo);
        return result;
    }


    public static class Wo extends WoId {

        private static final long serialVersionUID = -2577413577740827608L;

    }

}
