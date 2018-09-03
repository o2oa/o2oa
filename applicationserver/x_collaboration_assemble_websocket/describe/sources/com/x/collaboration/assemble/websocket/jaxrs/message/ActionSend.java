package com.x.collaboration.assemble.websocket.jaxrs.message;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Application;
import com.x.base.core.project.x_collaboration_assemble_websocket;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.collaboration.assemble.websocket.ThisApplication;
import com.x.collaboration.core.entity.Dialog;
import com.x.collaboration.core.entity.Notification;
import com.x.collaboration.core.entity.Talk;
import com.x.collaboration.core.entity.Talk_;
import com.x.collaboration.core.message.BaseMessage;
import com.x.collaboration.core.message.MessageCategory;
import com.x.collaboration.core.message.dialog.DialogMessage;
import com.x.collaboration.core.message.notification.NotificationMessage;
import com.x.organization.core.express.Organization;

public class ActionSend extends BaseAction {

	private Organization org = new Organization(ThisApplication.context());

	protected WrapOutBoolean execute(JsonElement jsonElement) throws Exception {
		MessageCategory category = BaseMessage.extractCategory(jsonElement);
		WrapOutBoolean wrap = new WrapOutBoolean();
		if (null != category) {
			switch (category) {
			case notification:
				this.sendNotification(jsonElement);
				break;
			case dialog:
				this.sendDialog(jsonElement);
			default:
				break;
			}
		}
		return wrap;
	}

	private WrapOutBoolean sendNotification(JsonElement jsonElement) throws Exception {
		boolean arrived = this.sendNotificationOnLocal(jsonElement);
		arrived = arrived || this.forwardOnRemote(jsonElement);
		if (!arrived) {
			this.storeNotification(jsonElement);
		}
		WrapOutBoolean wrap = new WrapOutBoolean();
		wrap.setValue(arrived);
		return wrap;
	}

	private WrapOutBoolean sendDialog(JsonElement jsonElement) throws Exception {
		boolean arrived = this.sendDialogOnLocal(jsonElement);
		arrived = arrived || this.forwardOnRemote(jsonElement);
		this.storeDialog(jsonElement, arrived);
		WrapOutBoolean wrap = new WrapOutBoolean();
		wrap.setValue(arrived);
		return wrap;
	}

	protected boolean forwardOnRemote(JsonElement jsonElement) throws Exception {
		boolean sent = false;
		List<Application> list = ThisApplication.context().applications().get(x_collaboration_assemble_websocket.class);
		if (ListTools.isNotEmpty(list)) {
			for (Application application : list) {
				if (!StringUtils.equals(application.getToken(), ThisApplication.context().token())) {
					WrapOutBoolean wrap = CipherConnectionAction
							.put(false, application.getUrlRoot() + "message", jsonElement)
							.getData(WrapOutBoolean.class);
					sent = sent || wrap.getValue();
				}
			}
		}
		return sent;
	}

	private void storeNotification(JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String name = NotificationMessage.extractPerson(jsonElement);
			String person = org.person().get(name);
			if (StringUtils.isNotEmpty(person)) {
				emc.beginTransaction(Notification.class);
				Notification o = new Notification();
				o.setPerson(person);
				o.setBody(jsonElement.toString());
				emc.persist(o, CheckPersistType.all);
				emc.commit();
			}
		}
	}

	private void storeDialog(JsonElement jsonElement, Boolean arrived) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			String personName = DialogMessage.extractPerson(jsonElement);
			String from = DialogMessage.extractFrom(jsonElement);
			String person = org.person().get(personName);
			if (StringUtils.isNotEmpty(person)) {
				emc.beginTransaction(Dialog.class);
				Dialog o = new Dialog();
				o.setPerson(person);
				o.setFrom(from);
				o.setBody(jsonElement.toString());
				o.setArrived(arrived);
				emc.persist(o, CheckPersistType.all);
				this.storeTalk(emc, o);
				emc.commit();
			}
		}
	}

	private void storeTalk(EntityManagerContainer emc, Dialog dialog) throws Exception {
		EntityManager em = emc.beginTransaction(Talk.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Talk> cq = cb.createQuery(Talk.class);
		Root<Talk> root = cq.from(Talk.class);
		Predicate p = cb.and(cb.equal(root.get(Talk_.person), dialog.getPerson()),
				cb.equal(root.get(Talk_.from), dialog.getFrom()));
		p = cb.or(p, cb.and(cb.equal(root.get(Talk_.person), dialog.getFrom()),
				cb.equal(root.get(Talk_.from), dialog.getPerson())));
		cq.select(root).where(p);
		List<Talk> list = em.createQuery(cq).getResultList();
		Talk talk = null;
		if (!list.isEmpty()) {
			talk = list.get(0);
			this.updateTalk(talk, dialog);
		} else {
			talk = new Talk();
			this.updateTalk(talk, dialog);
			emc.persist(talk, CheckPersistType.all);
		}
	}

	private void updateTalk(Talk talk, Dialog dialog) {
		talk.setArrived(dialog.getArrived());
		talk.setBody(dialog.getBody());
		talk.setFrom(dialog.getFrom());
		talk.setPerson(dialog.getPerson());
	}
}