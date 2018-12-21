package x.collaboration.service.message.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;

import x.collaboration.service.message.jaxrs.message.MessageAction;
import x.collaboration.service.message.jaxrs.message.SMSAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(MessageAction.class);
		classes.add(SMSAction.class);
		return classes;
	}

}