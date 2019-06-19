package com.x.teamwork.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.teamwork.assemble.control.jaxrs.chat.ChatAction;
import com.x.teamwork.assemble.control.jaxrs.config.SystemConfigAction;
import com.x.teamwork.assemble.control.jaxrs.list.TaskListAction;
import com.x.teamwork.assemble.control.jaxrs.projectgroup.ProjectGroupAction;
import com.x.temwork.assemble.control.jaxrs.extfield.ProjectExtFieldReleAction;
import com.x.temwork.assemble.control.jaxrs.project.ProjectAction;
import com.x.temwork.assemble.control.jaxrs.task.TaskAction;
import com.x.temwork.assemble.control.jaxrs.taskgroup.TaskGroupAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		
		this.classes.add( ProjectAction.class );
		this.classes.add( TaskAction.class );
		this.classes.add( ProjectGroupAction.class );
		this.classes.add( TaskGroupAction.class );
		this.classes.add( TaskListAction.class );
		this.classes.add( ChatAction.class );
		this.classes.add( SystemConfigAction.class );
		this.classes.add( ProjectExtFieldReleAction.class );
		return this.classes;
	}

}