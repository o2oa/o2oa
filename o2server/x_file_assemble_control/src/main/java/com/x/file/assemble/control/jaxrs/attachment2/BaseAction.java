package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Attachment2_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

abstract class BaseAction extends StandardJaxrsAction {

	protected void message_send_attachment_share(Attachment attachment, String person) throws Exception {
		String title = "收到来自(" + OrganizationDefinition.name(attachment.getPerson()) + ")的共享文件:" + attachment.getName()
				+ ".";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_SHARE, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_shareCancel(Attachment attachment, String person) throws Exception {
		String title = "(" + OrganizationDefinition.name(attachment.getPerson()) + ")取消了对:" + attachment.getName()
				+ ",文件的共享.";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_SHARECANCEL, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_editor(Attachment attachment, String person) throws Exception {
		String title = "收到来自(" + OrganizationDefinition.name(attachment.getPerson()) + ")的可编辑共享文件:"
				+ attachment.getName() + ".";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_EDITOR, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_editorCancel(Attachment attachment, String person) throws Exception {
		String title = "(" + OrganizationDefinition.name(attachment.getPerson()) + ")取消了对:" + attachment.getName()
				+ ",文件的共享编辑.";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_EDITORCANCEL, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected void message_send_attachment_editorModify(Attachment attachment, String editor, String person)
			throws Exception {
		String title = "(" + OrganizationDefinition.name(editor) + ")对文件:" + attachment.getName() + ",进行了修改.";
		MessageConnector.send(MessageConnector.TYPE_ATTACHMENT_EDITORMODIFY, title, person,
				XGsonBuilder.convert(attachment, Attachment.class));
	}

	protected Boolean exist(Business business, String fileName, String folderId, String person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.name), fileName);
		p = cb.and(p, cb.equal(root.get(Attachment2_.person), person));
		p = cb.and(p, cb.equal(root.get(Attachment2_.folder), folderId));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}
}