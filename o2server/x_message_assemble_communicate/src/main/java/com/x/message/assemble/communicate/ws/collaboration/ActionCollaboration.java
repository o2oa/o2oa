package com.x.message.assemble.communicate.ws.collaboration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.assemble.communicate.message.WsMessage;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;

/**
 * websocket连接
 * 
 * @author sword
 */
@ServerEndpoint(value = "/ws/collaboration", configurator = WsConfigurator.class)
public class ActionCollaboration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCollaboration.class);

	private static final String TAG_HEARTBEAT = "heartbeat";

	@OnOpen
	public void open(Session session) {
		EffectivePerson effectivePerson = (EffectivePerson) session.getUserProperties().get(HttpToken.X_PERSON);

		LOGGER.debug("webSocket OnOpen: tokenType:{}, distinguishedName:{}.", effectivePerson::getTokenType,
				effectivePerson::getDistinguishedName);

		if (!TokenType.anonymous.equals(effectivePerson.getTokenType())) {
			ThisApplication.wsClients().put(session, effectivePerson.getDistinguishedName());
			try {
				List<Message> messages = this.load(effectivePerson);
				WsMessage ws = null;
				for (Message o : messages) {
					ws = new WsMessage();
					ws.setType(o.getType());
					ws.setPerson(o.getPerson());
					ws.setTitle(o.getTitle());
					JsonElement jsonElement = XGsonBuilder.instance().fromJson(o.getBody(), JsonElement.class);
					ws.setBody(jsonElement);
						session.getBasicRemote().sendText(XGsonBuilder.toJson(ws));
						// session.getAsyncRemote().sendText(XGsonBuilder.toJson(ws));
				}
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
	}

	@OnClose
	public void close(Session session, CloseReason reason) {
		ThisApplication.wsClients().remove(session);
	}

	@OnError
	public void error(Throwable t) {
		// nothing
	}

	@OnMessage
	public void message(String input, Session session) throws IOException {
		EffectivePerson effectivePerson = (EffectivePerson) session.getUserProperties().get(HttpToken.X_PERSON);
		LOGGER.debug("webSocket OnMessage receive: message {}, person:{}, ip:{}, client:{}.", () -> input,
				effectivePerson::getDistinguishedName, effectivePerson::getRemoteAddress,
				effectivePerson::getUserAgent);
		// 建立心跳，维持websocket链接
		if (StringUtils.equals(input, TAG_HEARTBEAT)) {
			session.getBasicRemote().sendText(TAG_HEARTBEAT);
		}
	}

	private List<Message> load(EffectivePerson effectivePerson) {
		List<Message> os = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EntityManager em = emc.get(Message.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Message> cq = cb.createQuery(Message.class);
			Root<Message> root = cq.from(Message.class);
			Predicate p = cb.equal(root.get(Message_.person), effectivePerson.getDistinguishedName());
			p = cb.and(p, cb.equal(root.get(Message_.consumer), MessageConnector.CONSUME_WS));
			p = cb.and(p, cb.isFalse(root.get(Message_.consumed)));
			cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.createTime)));
			os = em.createQuery(cq).getResultList();
			emc.beginTransaction(Message.class);
			for (Message o : os) {
				o.setConsumed(true);
			}
			emc.commit();
			int maxCount = 10;
			if (os.size() > maxCount) {
				os = os.subList(0, maxCount);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return os;
	}

}
