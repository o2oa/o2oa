package com.x.mail.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.mail.assemble.control.jaxrs.account.AccountAction;
import com.x.mail.assemble.control.jaxrs.uuid.UUIDAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(AccountAction.class);
		this.classes.add(UUIDAction.class);
		return this.classes;
	}

}