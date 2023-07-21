package com.x.organization.assemble.control.jaxrs.personcard;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;



class ActionListMyGroupTypes extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListMyGroupTypes.class);

	// 列出当前登录用户的所有分组。
	ActionResult<String[]> MyExecute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<String[]> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> groupTypeList = business.personCard().fetchAllGroupTypeByCreator(effectivePerson.getDistinguishedName());
			String[] groupStrings = new String[groupTypeList.size()];
			result.setData(groupTypeList.toArray(groupStrings));
			result.setCount(new Long((long)groupTypeList.size()));
			return result;
		}
	}

	public static class Wo extends WoId {
	}
	

}
