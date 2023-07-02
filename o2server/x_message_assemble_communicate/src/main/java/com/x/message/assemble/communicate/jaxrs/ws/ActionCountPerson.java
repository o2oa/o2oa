package com.x.message.assemble.communicate.jaxrs.ws;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapCount;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.ThisApplication;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCountPerson extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCountPerson.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<Wo> result = new ActionResult<>();

        String node = Config.node();

        Set<String> set = new TreeSet<>();

        ThisApplication.context().applications().get(x_message_assemble_communicate.class).stream()
                .filter(o -> (!StringUtils.equalsIgnoreCase(node, o.getNode()))).forEach(o -> {
                    try {
                        ThisApplication.context().applications()
                                .getQuery(o, "ws/list/person/current/node").getDataAsList(WoPerson.class).stream()
                                .map(WoPerson::getPerson).forEach(set::add);
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                });

        ThisApplication.wsClients().values().stream().forEach(set::add);
        Wo wo = new Wo();
        wo.setCount((long) set.size());
        result.setData(wo);
        return result;
    }

    @Schema(name = "com.x.message.assemble.communicate.jaxrs.ws.ActionCountPerson$Wo")
    public static class Wo extends WrapCount {

    }
}