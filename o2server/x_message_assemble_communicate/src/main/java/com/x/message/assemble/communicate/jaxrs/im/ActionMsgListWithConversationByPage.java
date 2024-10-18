package com.x.message.assemble.communicate.jaxrs.im;

import java.util.Date;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversationExt;
import com.x.message.core.entity.IMMsg;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class ActionMsgListWithConversationByPage extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionMsgListWithConversationByPage.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
			throws Exception {

		LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (wi == null) {
				wi = new Wi();
			}
			if (wi.getConversationId() == null || wi.getConversationId().isEmpty()) {
				throw new ExceptionMsgEmptyConversationId();
			}
			IMConversationExt ext = business.imConversationFactory().getConversationExt(effectivePerson.getDistinguishedName(), wi.getConversationId());
			Date lastDeleteTime = null;
			if (ext != null) {
				lastDeleteTime = ext.getLastDeleteTime();
			}
			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);
			List<IMMsg> msgList = business.imConversationFactory().listMsgWithConversationByPage(adjustPage,
					adjustPageSize, wi.getConversationId(), lastDeleteTime);
			List<Wo> wos = Wo.copier.copy(msgList);
			for (Wo wo : wos) {
				if (StringUtils.isNotEmpty(wo.getQuoteMessageId()) ) {
					IMMsg quoteMessage =  emc.find(wo.getQuoteMessageId(), IMMsg.class);
					if (quoteMessage != null) {
						wo.setQuoteMessage(quoteMessage);
					}
				}
			}
			result.setData(wos);
			result.setCount(business.imConversationFactory().count(wi.getConversationId(), lastDeleteTime));
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 33404493425589133L;

		@FieldDescribe("会话id")
		private String conversationId;

		public String getConversationId() {
			return conversationId;
		}

		public void setConversationId(String conversationId) {
			this.conversationId = conversationId;
		}
	}

	public static class Wo extends IMMsg {

		private static final long serialVersionUID = 3434938936805201380L;
		static WrapCopier<IMMsg, Wo> copier = WrapCopierFactory.wo(IMMsg.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("引用消息.")
		private IMMsg quoteMessage;

		public IMMsg getQuoteMessage() {
			return quoteMessage;
		}

		public void setQuoteMessage(IMMsg quoteMessage) {
			this.quoteMessage = quoteMessage;
		}
	}
}
