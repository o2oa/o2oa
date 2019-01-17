package com.x.organization.assemble.authentication.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.organization.assemble.authentication.jaxrs.authentication.AuthenticationAction;
import com.x.organization.assemble.authentication.jaxrs.bind.BindAction;
import com.x.organization.assemble.authentication.jaxrs.dingding.DingdingAction;
import com.x.organization.assemble.authentication.jaxrs.oauth.OauthAction;
import com.x.organization.assemble.authentication.jaxrs.qiyeweixin.QiyeweixinAction;
import com.x.organization.assemble.authentication.jaxrs.sso.SsoAction;
import com.x.organization.assemble.authentication.jaxrs.zhengwudingding.ZhengwuDingdingAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(AuthenticationAction.class);
		classes.add(SsoAction.class);
		classes.add(BindAction.class);
		classes.add(OauthAction.class);
		classes.add(QiyeweixinAction.class);
		classes.add(DingdingAction.class);
		classes.add(ZhengwuDingdingAction.class);
		return classes;
	}

}