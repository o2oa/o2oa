package com.x.processplatform.service.processing.jaxrs.work;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.message.WorkEvent;
import com.x.processplatform.service.processing.Business;

/**
 * 
 * @author Rui
 *
 */
class ActionDelete extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {

        LOGGER.debug("execute:{}, id:{}.", effectivePerson::getDistinguishedName, () -> id);

        String executorSeed = null;

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
            if (null == work) {
                throw new ExceptionEntityNotExist(id, Work.class);
            }
            executorSeed = work.getJob();
        }

        Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
            public ActionResult<Wo> call() throws Exception {
                try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                    Business business = new Business(emc);
                    Work work = emc.find(id, Work.class);
                    if (null == work) {
                        throw new ExceptionEntityNotExist(id, Work.class);
                    }
                    cascadeDeleteWorkBeginButNotCommit(business, work);
                    emc.commit();
                    // 创建删除事件
                    emc.beginTransaction(WorkEvent.class);
                    emc.persist(WorkEvent.deleteEventInstance(work), CheckPersistType.all);
                    emc.commit();
                    ActionResult<Wo> result = new ActionResult<>();
                    Wo wo = new Wo();
                    wo.setId(work.getId());
                    result.setData(wo);
                    return result;
                }
            }
        };

        return ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get(300, TimeUnit.SECONDS);

    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = 7042621469119019501L;

    }

}