package com.x.message.assemble.communicate.ws.collaboration;

import java.util.List;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.tools.ListTools;

public class WsConfigurator extends ServerEndpointConfig.Configurator {

	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		try {
			EffectivePerson effectivePerson = this.getEffectivePerson(request);
			config.getUserProperties().put(HttpToken.X_Person, effectivePerson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private EffectivePerson getEffectivePerson(HandshakeRequest request) {
		try {
			List<String> list = request.getParameterMap().get(HttpToken.X_Token);
			String token = null;
			if (ListTools.isNotEmpty(list)) {
				token = list.get(0);
			}
			if (StringUtils.isNotEmpty(token)) {
				HttpToken httpToken = new HttpToken();
				return httpToken.who(token, Config.token().getCipher());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EffectivePerson.anonymous();
	}

}