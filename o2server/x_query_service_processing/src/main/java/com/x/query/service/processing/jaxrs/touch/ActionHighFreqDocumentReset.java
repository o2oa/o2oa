package com.x.query.service.processing.jaxrs.touch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.entity.index.State;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * (0) AbstractJaxrsAction.EMPTY_SYMBOL 表示在有所节点上执行
 * 
 * @author ray
 *
 */
class ActionHighFreqDocumentReset extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionHighFreqDocumentReset.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String node) throws Exception {

        LOGGER.info("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setValue(false);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<State> list = new ArrayList<>();
            if (!StringUtils.equals(node, EMPTY_SYMBOL)) {
                list = emc.listEqualAndEqualAndEqual(State.class, State.NODE_FIELDNAME, node,
                        State.FREQ_FIELDNAME, State.FREQ_HIGH, State.TYPE_FIELDNAME, State.TYPE_DOCUMENT);
            } else {
                list = emc.listEqualAndEqual(State.class,
                        State.FREQ_FIELDNAME, State.FREQ_HIGH, State.TYPE_FIELDNAME, State.TYPE_DOCUMENT);
            }
            if (!list.isEmpty()) {
                emc.beginTransaction(State.class);
                for (State state : list) {
                    emc.check(state, CheckRemoveType.all);
                    emc.remove(state);
                }
                emc.commit();
                wo.setValue(true);
            }
        }

        result.setData(wo);
        return result;
    }

    @Schema(name = "com.x.query.service.processing.jaxrs.touch.ActionHighFreqDocumentReset$Wo")
    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -6815067359344499966L;

    }

}