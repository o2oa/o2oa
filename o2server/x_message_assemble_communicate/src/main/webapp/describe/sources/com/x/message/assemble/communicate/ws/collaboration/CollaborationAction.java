//package com.x.message.assemble.communicate.ws.collaboration;
//
//import java.awt.Dialog;
//import java.awt.JobAttributes.DialogType;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.jms.TextMessage;
//import javax.persistence.EntityManager;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//import javax.websocket.CloseReason;
//import javax.websocket.OnClose;
//import javax.websocket.OnError;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.Session;
//import javax.websocket.server.ServerEndpoint;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.sun.nio.sctp.Notification;
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.project.x_collaboration_assemble_websocket;
//import com.x.base.core.project.gson.XGsonBuilder;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.http.HttpToken;
//import com.x.base.core.project.http.TokenType;
//import com.x.base.core.project.http.WrapOutBoolean;
//import com.x.base.core.project.tools.SortTools;
//import com.x.collaboration.core.entity.Dialog_;
//import com.x.collaboration.core.entity.Notification_;
//import com.x.collaboration.core.message.BaseMessage;
//import com.x.collaboration.core.message.MessageCategory;
//import com.x.collaboration.core.message.dialog.DialogMessage;
//import com.x.message.assemble.communicate.ThisApplication;
//
//@ServerEndpoint(value = "/ws/collaboration", configurator = WsConfigurator.class)
//public class CollaborationAction {
//
//	private final static Gson gson = XGsonBuilder.instance();
//
//	@OnOpen
//	public void open(Session session) {
//		EffectivePerson effectivePerson = (EffectivePerson) session.getUserProperties().get(HttpToken.X_Person);
//		if (TokenType.anonymous.equals(effectivePerson.getTokenType())) {
//			return;
//		}
//		ThisApplication.connections.put(effectivePerson.getDistinguishedName(), session);
//		try {
//			List<Notification> notifications = this.loadNotifications(effectivePerson.getDistinguishedName());
//			for (Notification o : notifications) {
//				session.getBasicRemote().sendText(o.getBody());
//			}
//			List<Dialog> dialogs = this.loadDialogs(effectivePerson.getDistinguishedName());
//			for (Dialog o : dialogs) {
//				session.getBasicRemote().sendText(o.getBody());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@OnClose
//	public void close(Session session, CloseReason reason) throws IOException {
//		EffectivePerson effectivePerson = (EffectivePerson) session.getUserProperties().get(HttpToken.X_Person);
//		if (TokenType.anonymous.equals(effectivePerson.getTokenType())) {
//			return;
//		}
//		ThisApplication.connections.remove(effectivePerson.getDistinguishedName());
//	}
//
//	@OnError
//	public void error(Throwable t) throws Throwable {
//
//	}
//
//	@OnMessage
//	public void handlingText(String text, Session session) {
//		EffectivePerson effectivePerson = (EffectivePerson) session.getUserProperties().get(HttpToken.X_Person);
//		if (TokenType.anonymous.equals(effectivePerson.getTokenType())) {
//			return;
//		}
//		try {
//			JsonElement jsonElement = gson.fromJson(text, JsonElement.class);
//			MessageCategory category = BaseMessage.extractCategory(jsonElement);
//			if (null != category) {
//				switch (category) {
//				case notification:
//					break;
//				case dialog:
//					this.dialog(jsonElement, effectivePerson);
//					break;
//				case operation:
//					break;
//				default:
//					break;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@OnMessage
//	public void handlingBinary(byte[] binary, boolean last, Session session) {
//		System.out.println("received binary:" + binary.toString());
//	}
//
//	private List<Notification> loadNotifications(String person) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			EntityManager em = emc.get(Notification.class);
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Notification> cq = cb.createQuery(Notification.class);
//			Root<Notification> root = cq.from(Notification.class);
//			Predicate p = cb.equal(root.get(Notification_.person), person);
//			cq.select(root).where(p);
//			List<Notification> list = em.createQuery(cq).getResultList();
//			emc.beginTransaction(Notification.class);
//			for (Notification o : list) {
//				emc.remove(o);
//			}
//			emc.commit();
//			List<Notification> messages = new ArrayList<>(list);
//			SortTools.asc(messages, false, "createTime");
//			return messages;
//		}
//	}
//
//	private List<Dialog> loadDialogs(String person) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			EntityManager em = emc.get(Dialog.class);
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<Dialog> cq = cb.createQuery(Dialog.class);
//			Root<Dialog> root = cq.from(Dialog.class);
//			Predicate p = cb.equal(root.get(Dialog_.person), person);
//			p = cb.and(p, cb.notEqual(root.get(Dialog_.arrived), true));
//			cq.select(root).where(p);
//			List<Dialog> list = em.createQuery(cq).getResultList();
//			emc.beginTransaction(Dialog.class);
//			for (Dialog o : list) {
//				o.setArrived(true);
//			}
//			emc.commit();
//			List<Dialog> messages = new ArrayList<>(list);
//			SortTools.asc(messages, false, "createTime");
//			return messages;
//		}
//	}
//
//	private void dialog(JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
//		DialogType type = DialogMessage.extractType(jsonElement);
//		if (null != type) {
//			switch (type) {
//			case text:
//				dialogText(jsonElement, effectivePerson);
//				break;
//			default:
//				break;
//			}
//		}
//	}
//
//	private WrapOutBoolean dialogText(JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
//		TextMessage message = gson.fromJson(jsonElement, TextMessage.class);
//		if (StringUtils.isEmpty(message.getPerson())) {
//			throw new Exception("invaild message:" + message);
//		}
//		message.setFrom(effectivePerson.getDistinguishedName());
//		message.setTime(new Date());
//		WrapOutBoolean wrap = ThisApplication.context().applications()
//				.postQuery(x_collaboration_assemble_websocket.class, "message", message).getData(WrapOutBoolean.class);
//		return wrap;
//
//	}
//}
