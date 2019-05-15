package com.x.message.assemble.communicate.ws.collaboration;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.ThisApplication;

@ServerEndpoint(value = "/ws/collaboration", configurator = WsConfigurator.class)

public class ActionCollaboration {

	private static Logger logger = LoggerFactory.getLogger(ActionCollaboration.class);

	@OnOpen
	public void open(Session session) {
		EffectivePerson effectivePerson = (EffectivePerson) session.getUserProperties().get(HttpToken.X_Person);
		logger.debug("@OnOpen: tokenType:{}, distinguishedName:{}.", effectivePerson.getTokenType(),
				effectivePerson.getDistinguishedName());
		if (TokenType.anonymous.equals(effectivePerson.getTokenType())) {
			return;
		}
		ThisApplication.connections.put(effectivePerson.getDistinguishedName(), session);
	}

	@OnClose
	public void close(Session session, CloseReason reason) throws IOException {
		EffectivePerson effectivePerson = (EffectivePerson) session.getUserProperties().get(HttpToken.X_Person);
		logger.debug("@OnOpen: tokenType:{}, distinguishedName:{}.", effectivePerson.getTokenType(),
				effectivePerson.getDistinguishedName());
		if (TokenType.anonymous.equals(effectivePerson.getTokenType())) {
			return;
		}
		ThisApplication.connections.remove(effectivePerson.getDistinguishedName());
	}

	@OnError
	public void error(Throwable t) throws Throwable {

	}

}
