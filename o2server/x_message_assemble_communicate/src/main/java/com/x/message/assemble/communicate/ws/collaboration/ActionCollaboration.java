package com.x.message.assemble.communicate.ws.collaboration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.message.WsMessage;
import com.x.message.core.entity.Message;
import com.x.message.core.entity.Message_;
import org.apache.commons.lang3.StringUtils;

@ServerEndpoint(value = "/ws/collaboration", configurator = WsConfigurator.class)
public class ActionCollaboration {

	private static Logger logger = LoggerFactory.getLogger(ActionCollaboration.class);

	public static final ConcurrentHashMap<Session, String> clients = new ConcurrentHashMap<Session, String>();

	@OnOpen
	public void open(Session session) {
		EffectivePerson effectivePerson = (EffectivePerson) session.getUserProperties().get(HttpToken.X_Person);
		logger.debug("@OnOpen: tokenType:{}, distinguishedName:{}.", effectivePerson.getTokenType(),
				effectivePerson.getDistinguishedName());
		if (TokenType.anonymous.equals(effectivePerson.getTokenType())) {
			return;
		} else {
			clients.put(session, effectivePerson.getDistinguishedName());
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
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	@OnClose
	public void close(Session session, CloseReason reason) throws IOException {
		clients.remove(session);
	}

	@OnError
	public void error(Throwable t) throws Throwable {

	}

	@OnMessage
	public void message(String input, Session session) throws Exception {
		EffectivePerson effectivePerson = (EffectivePerson) session.getUserProperties().get(HttpToken.X_Person);
		logger.debug("@OnMessage1 receive: message {}, person:{}, ip:{}, client:{} .", input,
				effectivePerson.getDistinguishedName(), effectivePerson.getRemoteAddress(), effectivePerson.getUserAgent());
		if (StringUtils.isBlank(input)) {
			return;
		}
		// 建立心跳，维持websocket链接
		if (input.equalsIgnoreCase("heartbeat")) {
			session.getBasicRemote().sendText("heartbeat");
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
			p = cb.and(p, cb.equal(root.get(Message_.consumed), false));
			cq.select(root).where(p).orderBy(cb.asc(root.get(Message_.createTime)));
			os = em.createQuery(cq).setMaxResults(100).getResultList();
			emc.beginTransaction(Message.class);
			for (Message o : os) {
				o.setConsumed(true);
			}
			emc.commit();
		} catch (Exception e) {
			logger.error(e);
		}
		return os;
	}

}
