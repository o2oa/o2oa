package com.x.query.assemble.designer.jaxrs.statement;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.assemble.designer.ThisApplication;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.express.statement.ExecuteTarget;
import com.x.query.core.express.statement.ExecuteTargetBuilder;
import com.x.query.core.express.statement.Executor;
import com.x.query.core.express.statement.Runtime;

/**
 * DATA,COUNT分别执行
 * <p>
 * sql: select xid, xjob FROM PP_C_TASK WHERE xtitle =?1 and xperson=:person and
 * xcreateTime > ?2
 *
 * <p>
 * jqpl: select o.id, o.job FROM TASK WHERE o.title =:title and o.person=:person
 * and o.createTime > ?1
 * 
 * @author ray
 *
 */
class ActionExecuteV2 extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecuteV2.class);

    ActionResult<Object> execute(EffectivePerson effectivePerson, String flag, String mode, Integer page, Integer size,
            JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, flag:{}, mode:{}, page:{}, size:{}, jsonElement:{}.",
                effectivePerson::getDistinguishedName, () -> flag, () -> mode, () -> page, () -> size,
                () -> jsonElement);
        ClassLoader classLoader = Business.getDynamicEntityClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        ActionResult<Object> result = new ActionResult<>();
        Statement statement;
        Runtime runtime;
        Pair<ExecuteTarget, Optional<ExecuteTarget>> executeTargetPair;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            statement = emc.flag(flag, Statement.class);
            if (null == statement) {
                throw new ExceptionEntityNotExist(flag, Statement.class);
            }
            if (!business.executable(effectivePerson, statement)) {
                throw new ExceptionAccessDenied(effectivePerson, statement);
            }
            runtime = Runtime.concrete(effectivePerson, jsonElement, business.organization(), page, size);
            ExecuteTargetBuilder builder = new ExecuteTargetBuilder(ThisApplication.context(), effectivePerson,
                    business.organization(), statement, runtime);
            executeTargetPair = builder.build();
        }
        ExecuteTarget dataExecuteTarget = executeTargetPair.first();
        Optional<ExecuteTarget> optionalCountExecuteTarget = executeTargetPair.second();
        result.setData(Executor.executeData(statement, runtime, dataExecuteTarget));
        if (optionalCountExecuteTarget.isPresent()) {
            result.setCount(Executor.executeCount(statement, optionalCountExecuteTarget.get()));
        }
        return result;
    }

}
