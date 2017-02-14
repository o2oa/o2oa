package x.collaboration.service.message.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;

import x.collaboration.service.message.jaxrs.message.MessageAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(MessageAction.class);
		classes.add(MessageAction.class);
		return classes;
	}

}