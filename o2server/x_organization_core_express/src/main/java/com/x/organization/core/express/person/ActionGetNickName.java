package com.x.organization.core.express.person;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Applications;
import com.x.base.core.project.jaxrs.WrapString;

class ActionGetNickName extends BaseAction {

	public static String execute(AbstractContext context, String flag) throws Exception {
		if(StringUtils.isBlank(flag)){
			return flag;
		}
		Wo wo = context.applications().getQuery(applicationClass, Applications.joinQueryUri("person", "nick", "name", flag)).getData(Wo.class);
		return wo.getValue();
	}

	public static class Wo extends WrapString {
	}
}
