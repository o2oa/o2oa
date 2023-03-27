package com.x.processplatform.service.processing.jaxrs.task;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.express.service.processing.jaxrs.task.V2ResetWi;
import com.x.processplatform.service.processing.Business;

class V2Reset extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2Reset.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, id:{}, jsonElement:{}.", effectivePerson::getDistinguishedName, () -> id,
                () -> jsonElement);

        final Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

        Task task = null;

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            task = emc.fetch(id, Task.class, ListTools.toList(Task.job_FIELDNAME));
            if (null == task) {
                throw new ExceptionEntityNotExist(id, Task.class);
            }
        }

        return ProcessPlatformExecutorFactory.get(task.getJob())
                .submit(new CallableImpl(task.getId(), wi.getIdentityList(), wi.getKeep())).get(300, TimeUnit.SECONDS);

    }

    private class CallableImpl implements Callable<ActionResult<Wo>> {

        private String id;
        private List<String> identityList;
        private boolean keep;

        private CallableImpl(String id, List<String> identityList, boolean keep) {
            this.id = id;
            this.identityList = identityList;
            this.keep = keep;
        }

        @Override
        public ActionResult<Wo> call() throws Exception {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                Task task = emc.find(id, Task.class);
                if (null == task) {
                    throw new ExceptionEntityNotExist(id, Task.class);
                }
                Work work = emc.find(task.getWork(), Work.class);
                if (null == work) {
                    throw new ExceptionEntityNotExist(task.getWork(), Work.class);
                }
                Manual manual = (Manual) business.element().get(work.getActivity(), ActivityType.manual);
                if (null == manual) {
                    throw new ExceptionEntityNotExist(work.getActivity(), Manual.class);
                }
                List<String> identities = business.organization().identity().list(identityList);
                if (!ListTools.isEmpty(identities)) {
                    emc.beginTransaction(Work.class);
                    ManualTaskIdentityMatrix matrix = work.getManualTaskIdentityMatrix();
                    matrix.reset(task.getIdentity(), null, identities, null, !keep);
                    emc.beginTransaction(TaskCompleted.class);
                    emc.listEqualAndEqualAndNotEqual(TaskCompleted.class, TaskCompleted.job_FIELDNAME, work.getJob(),
                            TaskCompleted.activityToken_FIELDNAME,
                            work.getActivityToken(), TaskCompleted.joinInquire_FIELDNAME, false).stream()
                            .filter(o -> identities.contains(o.getIdentity())).forEach(p -> {
                                try {
                                    p.setJoinInquire(false);
                                    emc.check(p, CheckPersistType.all);
                                } catch (Exception e) {
                                    LOGGER.error(e);
                                }
                            });
                    work.setManualTaskIdentityMatrix(matrix);
                    emc.check(work, CheckPersistType.all);
                    emc.commit();
                }
            }
            Wo wo = new Wo();
            wo.setValue(true);
            ActionResult<Wo> result = new ActionResult<>();
            result.setData(wo);
            return result;
        }

    }

    public static class Wi extends V2ResetWi {

        private static final long serialVersionUID = -36317314462442492L;

    }

    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -1577970926042381340L;

    }

}