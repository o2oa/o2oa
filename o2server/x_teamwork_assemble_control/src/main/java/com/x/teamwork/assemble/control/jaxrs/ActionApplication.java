package com.x.teamwork.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.teamwork.assemble.control.jaxrs.attachment.AttachmentAction;
import com.x.teamwork.assemble.control.jaxrs.chat.ChatAction;
import com.x.teamwork.assemble.control.jaxrs.config.SystemConfigAction;
import com.x.teamwork.assemble.control.jaxrs.dynamic.DynamicAction;
import com.x.teamwork.assemble.control.jaxrs.extfield.ProjectExtFieldReleAction;
import com.x.teamwork.assemble.control.jaxrs.list.TaskListAction;
import com.x.teamwork.assemble.control.jaxrs.project.ProjectAction;
import com.x.teamwork.assemble.control.jaxrs.projectgroup.ProjectGroupAction;
import com.x.teamwork.assemble.control.jaxrs.task.TaskAction;
import com.x.teamwork.assemble.control.jaxrs.taskgroup.TaskGroupAction;
import com.x.teamwork.assemble.control.jaxrs.tasktag.TaskTagAction;
import com.x.teamwork.assemble.control.jaxrs.taskview.TaskViewAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		
		this.classes.add( ProjectAction.class );
		this.classes.add( TaskAction.class );
		this.classes.add( TaskTagAction.class );
		this.classes.add( TaskViewAction.class );
		this.classes.add( AttachmentAction.class );
		this.classes.add( ProjectGroupAction.class );
		this.classes.add( TaskGroupAction.class );
		this.classes.add( TaskListAction.class );
		this.classes.add( DynamicAction.class );
		this.classes.add( ChatAction.class );
		this.classes.add( SystemConfigAction.class );
		this.classes.add( ProjectExtFieldReleAction.class );
		return this.classes;
	}

}