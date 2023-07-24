package com.x.message.assemble.communicate.jaxrs.ws;

import java.util.List;
import java.util.stream.Collectors;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.ThisApplication;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListPersonCurrentNode extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListPersonCurrentNode.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<List<Wo>> result = new ActionResult<>();

        result.setData(
                ThisApplication.wsClients().values().stream().map(Wo::new).collect(Collectors.toList()));

        return result;
    }

    @Schema(name = "com.x.message.assemble.communicate.jaxrs.ws.ActionListPersonCurrentNode$Wo")
    public class Wo extends WoPerson {

        public Wo(String person) {
            super(person);
        }

    }

}