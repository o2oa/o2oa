package com.x.message.assemble.communicate.jaxrs.im;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;

/**
 * Created by fancyLou on 2022/3/8. Copyright © 2022 O2. All rights reserved.
 */
public class ActionConversationFindByBusinessId extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionConversationFindByBusinessId.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String businessId) throws Exception {

		LOGGER.debug("execute:{}, businessId:{}.", effectivePerson::getDistinguishedName, () -> businessId);

		ActionResult<List<Wo>> result = new ActionResult<>();
		if (StringUtils.isEmpty(businessId)) {
			throw new ExceptionEmptyId();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<IMConversation> list = business.imConversationFactory()
					.listConversationWithPersonAndBusinessId(effectivePerson.getDistinguishedName(), businessId);
			if (list != null && !list.isEmpty()) {
				// 存在 返回结果
				List<Wo> wos = Wo.copier.copy(list);
				result.setData(wos);
			} else {
				result.setData(new ArrayList<>());
			}
		}
		return result;
	}

	public static class Wo extends IMConversation {

		private static final long serialVersionUID = -3327091155757964236L;
		static WrapCopier<IMConversation, Wo> copier = WrapCopierFactory.wo(IMConversation.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}
