package com.x.message.assemble.communicate.jaxrs.ws;

import java.util.Map.Entry;

import javax.websocket.Session;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.assemble.communicate.message.WsMessage;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionCreate extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreate.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        Wo wo = new Wo();
        wo.setValue(false);

        for (Entry<Session, String> entry : ThisApplication.wsClients().entrySet()) {
            if (StringUtils.equals(entry.getValue(), wi.getPerson())) {
                Session session = entry.getKey();
                if (session != null && session.isOpen()) {
                    LOGGER.debug("send ws, message: {}.", () -> wi);
                    session.getBasicRemote().sendText(jsonElement.toString());
                    wo.setValue(true);
                }
            }
        }

        result.setData(wo);
        return result;
    }

    @Schema(name = "com.x.message.assemble.communicate.jaxrs.ws.ActionCreate$Wi")
    public static class Wi extends WsMessage {

        private static final long serialVersionUID = -8691888252305620999L;
    }

    @Schema(name = "com.x.message.assemble.communicate.jaxrs.ws.ActionCreate$Wo")
    public static class Wo extends WrapBoolean {

        private static final long serialVersionUID = -7273918635205899723L;

    }

}