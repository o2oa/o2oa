package com.x.message.assemble.communicate.jaxrs.message;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.x_message_assemble_communicate;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.Message;

class ActionCustomCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCustomCreate.class);

	private static final String CUSTOM_PREFIX = "custom_";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (!StringUtils.startsWith(wi.getType(), CUSTOM_PREFIX)) {
				throw new ExceptionNotCustomMessage(wi.getType());
			}
			if (!Config.messages().containsKey(wi.getType())) {
				throw new ExceptionUndefinedMessageType(wi.getType());
			}
			Wo wo = ThisApplication.context().applications()
					.postQuery(x_message_assemble_communicate.class, "connector", wi).getData(Wo.class);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		static WrapCopier<Wi, MessageConnector.Wrap> copier = WrapCopierFactory.wi(Wi.class,
				MessageConnector.Wrap.class, null, JpaObject.FieldsUnmodify);
		
		@FieldDescribe("类型")
		private String type;

		@FieldDescribe("人员")
		private String person;

		@FieldDescribe("标题")
		private String title;

		@FieldDescribe("推送内容")
		private JsonElement body;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public JsonElement getBody() {
			return body;
		}

		public void setBody(JsonElement body) {
			this.body = body;
		}

	}

	public static class Wo extends WrapBoolean {

	}

}