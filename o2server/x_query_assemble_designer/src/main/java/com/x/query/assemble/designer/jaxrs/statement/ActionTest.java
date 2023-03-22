package com.x.query.assemble.designer.jaxrs.statement;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.dynamic.DynamicBaseEntity;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest.class);

    ActionResult<Object> result = new ActionResult<>();

    ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
        String sql = "SELECT xid,xperson FROM PP_C_TASK WHERE xidentity= :identity";
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(DynamicBaseEntity.class);
            Query query = em.createNativeQuery(sql);
            String p = "a@c1_a@I";
            System.out.println(query.getClass());
            query.setParameter("identity", p);
            List<?> list = query.getResultList();
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(gson.toJson(list));
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!");
            result.setData(list);
        }
        return result;
    }
}
