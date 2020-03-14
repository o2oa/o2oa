package com.x.organization.assemble.personal.jaxrs.exmail;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;

class ActionPost extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPost.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String msg_signature, String timestamp, String nonce,
			String body) throws Exception {

		logger.debug("腾讯企业邮收到,msg_signature:{}, timestamp:{}, nonce:{}, body:{}.", msg_signature, timestamp, nonce,
				body);
		if (!Config.exmail().getEnable()) {
			throw new ExceptionExmailDisable();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			wo.setValue(false);

			WXBizMsgCrypt crypt = new WXBizMsgCrypt(Config.exmail().getToken(), Config.exmail().getEncodingAesKey(),
					Config.exmail().getCorpId());
			String msg = crypt.DecryptMsg(msg_signature, timestamp, nonce, body);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(msg);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);
			Element root = document.getDocumentElement();

			NodeList userIDNodeList = root.getElementsByTagName("UserID");
			NodeList toUserIDNodeList = root.getElementsByTagName("ToUserID");

			if (userIDNodeList.getLength() != 0) {
				/* 未读邮件提醒 */
				String mail = userIDNodeList.item(0).getTextContent();
				Person person = emc.firstEqual(Person.class, Person.mail_FIELDNAME, mail);
				if (null == person) {
					throw new ExceptionEntityNotExist(mail, Person.class);
				}
				emc.beginTransaction(PersonAttribute.class);
				String unReadCount = root.getElementsByTagName("UnReadCount").item(0).getTextContent();
				this.updateNewCount(business, person, unReadCount);
				emc.commit();
			} else if (toUserIDNodeList.getLength() != 0) {
				/* 新邮件提醒 */
				String mail = toUserIDNodeList.item(0).getTextContent();
				Person person = emc.firstEqual(Person.class, Person.mail_FIELDNAME, mail);
				if (null == person) {
					throw new ExceptionEntityNotExist(mail, Person.class);
				}
				emc.beginTransaction(PersonAttribute.class);
				String fromUser = root.getElementsByTagName("FromUser").item(0).getTextContent();
				String title = root.getElementsByTagName("Title").item(0).getTextContent();
				String time = root.getElementsByTagName("Time").item(0).getTextContent();
				String newCount = root.getElementsByTagName("NewCount").item(0).getTextContent();
				this.updateNewCount(business, person, newCount);
				this.updateTitle(business, person, time, title, fromUser);
				emc.commit();
				wo.setValue(true);
			}
			result.setData(wo);
			return result;
		}
	}

	private void updateNewCount(Business business, Person person, String newCount) throws Exception {
		PersonAttribute attribute = business.entityManagerContainer().firstEqualAndEqual(PersonAttribute.class,
				PersonAttribute.name_FIELDNAME, Config.exmail().getPersonAttributeNewCountName(),
				PersonAttribute.person_FIELDNAME, person.getId());
		if (null == attribute) {
			attribute = new PersonAttribute();
			attribute.setPerson(person.getId());
			attribute.setName(Config.exmail().getPersonAttributeNewCountName());
			attribute.setAttributeList(new ArrayList<String>());
			business.entityManagerContainer().persist(attribute, CheckPersistType.all);
		}
		attribute.getAttributeList().clear();
		attribute.getAttributeList().add(newCount);
	}

	private void updateTitle(Business business, Person person, String time, String title, String fromUser)
			throws Exception {
		Date date = new Date();
		date.setTime(Long.parseLong(time) * 1000);
		String text = DateTools.format(date, "MM-dd HH:mm") + " " + (StringUtils.isBlank(title) ? "..." : title) + " "
				+ fromUser;
		text = StringTools.utf8SubString(text, JpaObject.length_255B);
		PersonAttribute attribute = business.entityManagerContainer().firstEqualAndEqual(PersonAttribute.class,
				PersonAttribute.name_FIELDNAME, Config.exmail().getPersonAttributeTitleName(),
				PersonAttribute.person_FIELDNAME, person.getId());
		if (null == attribute) {
			attribute = new PersonAttribute();
			attribute.setPerson(person.getId());
			attribute.setName(Config.exmail().getPersonAttributeTitleName());
			attribute.setAttributeList(new ArrayList<String>());
			business.entityManagerContainer().persist(attribute, CheckPersistType.all);
		}
		List<String> list = attribute.getAttributeList();
		list.add(text);
		for (int i = list.size() - 1; i >= 20; i--) {
			list.remove(i);
		}
	}

	public static class Wo extends WrapBoolean {
	}

}
