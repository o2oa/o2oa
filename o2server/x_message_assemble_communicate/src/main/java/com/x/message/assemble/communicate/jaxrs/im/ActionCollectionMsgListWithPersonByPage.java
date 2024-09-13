package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.IMMsgCollection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ActionCollectionMsgListWithPersonByPage extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionCollectionMsgListWithPersonByPage.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size)
			throws Exception {

		LOGGER.debug("execute ActionCollectionMsgListWithPersonByPage :{}, page:{}, size:{}.", effectivePerson::getDistinguishedName, () -> page, () -> size);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);

			Integer adjustPage = this.adjustPage(page);
			Integer adjustPageSize = this.adjustSize(size);
			List<IMMsgCollection> msgList = business.imConversationFactory().listCollectionWithPersonByPage(adjustPage, adjustPageSize, effectivePerson.getDistinguishedName());
			List<Wo> wos = Wo.copier.copy(msgList);
			for (Wo wo : wos) {
				if (StringUtils.isNotEmpty(wo.getMessageId()) ) {
					IMMsg message =  emc.find(wo.getMessageId(), IMMsg.class);
					if (message != null) {
						wo.setMessage(message);
					}
				}
			}
			result.setData(wos);
			result.setCount(business.imConversationFactory().listCollectionCount(effectivePerson.getDistinguishedName()));
			return result;
		}
	}


	public static class Wo extends IMMsgCollection {


		private static final long serialVersionUID = -2010022893676369028L;
		static WrapCopier<IMMsgCollection, Wo> copier = WrapCopierFactory.wo(IMMsgCollection.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("关联消息.")
		private IMMsg message;

		public IMMsg getMessage() {
			return message;
		}

		public void setMessage(IMMsg message) {
			this.message = message;
		}
	}
}
