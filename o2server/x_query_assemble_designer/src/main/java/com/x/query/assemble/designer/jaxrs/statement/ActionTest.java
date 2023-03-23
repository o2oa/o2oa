package com.x.query.assemble.designer.jaxrs.statement;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.core.entity.content.Task;

class ActionTest extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest.class);

    ActionResult<Object> result = new ActionResult<>();

    ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(Task.class);
            Query query = em.createQuery("select o FROM Task o WHERE o.identity=?1");
            query.setParameter(1, "a@c1_a@I");
            List<Task> os = query.getResultList();
            result.setData(os);
        }
        return result;
    }
}
