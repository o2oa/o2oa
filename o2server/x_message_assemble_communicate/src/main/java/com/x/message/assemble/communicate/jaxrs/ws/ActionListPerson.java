package com.x.message.assemble.communicate.jaxrs.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.ThisApplication;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListPerson extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListPerson.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<List<Wo>> result = new ActionResult<>();

        List<Wo> wos = new ArrayList<>();

        String node = Config.node();

        ThisApplication.context().applications().get(x_message_assemble_communicate.class).stream()
                .filter(o -> !StringUtils.equalsIgnoreCase(node, o.getNode())).forEach(o -> {
                    try {
                        List<WoPerson> people = ThisApplication.context().applications()
                                .getQuery(o, "ws/list/person/current/node").getDataAsList(WoPerson.class);
                        Wo wo = new Wo();
                        wo.setNode(o.getNode());
                        wo.setPersonList(people);
                        wos.add(wo);
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                });
        wos.add(currentNode(node));
        result.setData(wos);
        return result;
    }

    private Wo currentNode(String node) {
        Wo wo = new Wo();
        wo.setNode(node);
        wo.setPersonList(
                ThisApplication.wsClients().values().stream().map(WoPerson::new).collect(Collectors.toList()));
        return wo;
    }

    @Schema(name = "com.x.message.assemble.communicate.jaxrs.ws.ActionListPerson$Wo")
    public static class Wo extends GsonPropertyObject {

        private String node;

        private List<WoPerson> personList = new ArrayList<>();

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public List<WoPerson> getPersonList() {
            return personList;
        }

        public void setPersonList(List<WoPerson> personList) {
            this.personList = personList;
        }

    }
}