package com.x.ai.assemble.control.jaxrs;

import com.x.ai.assemble.control.jaxrs.chat.ChatAction;
import com.x.ai.assemble.control.jaxrs.config.ConfigAction;
import com.x.ai.assemble.control.jaxrs.file.FileAction;
import com.x.ai.assemble.control.jaxrs.index.IndexAction;
import com.x.base.core.project.jaxrs.AbstractActionApplication;
import java.util.Set;
import javax.ws.rs.ApplicationPath;

/**
 * Jaxrs服务注册类，在此类中注册的Action会向外提供服务
 * @author sword
 */
@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	@Override
	public Set<Class<?>> getClasses() {

		//提供服务的Action类需要在这里注册，不然无法向外提供服务
		this.classes.add( ConfigAction.class);
		this.classes.add( ChatAction.class);
		this.classes.add( IndexAction.class);
		this.classes.add( FileAction.class);

		return this.classes;
	}

}
