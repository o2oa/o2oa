package com.x.processplatform.service.processing.jaxrs.job;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;

public class V2View extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(V2View.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, String person) throws Exception {

        LOGGER.debug("execute:{}, job:{}, person:{}.", effectivePerson::getDistinguishedName, () -> job,
                () -> person);

        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setValue(false);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<Read> reads = emc.listEqualAndEqualAndEqual(Read.class, Read.job_FIELDNAME, job,
                    Read.person_FIELDNAME,
                    person, Read.VIEWTIME_FIELDNAME, null);
            if (!reads.isEmpty()) {
                emc.beginTransaction(Read.class);
                for (Read read : reads) {
                    read.setViewTime(new Date());
                    emc.check(read, CheckPersistType.all);
                }
                emc.commit();
            }
            List<Task> tasks = emc.listEqualAndEqualAndEqual(Task.class, Task.job_FIELDNAME, job,
                    Task.person_FIELDNAME,
                    person, Task.VIEWTIME_FIELDNAME, null);
            if (!tasks.isEmpty()) {
                emc.beginTransaction(Task.class);
                for (Task task : tasks) {
                    task.setViewTime(new Date());
                    emc.check(task, CheckPersistType.all);
                }
                emc.commit();
            }
        }
        wo.setValue(true);
        result.setData(wo);
        return result;
    }

    public static class Wo extends WrapBoolean {

    }

}