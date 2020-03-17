package com.x.teamwork.assemble.control.jaxrs.attachment;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.teamwork.assemble.control.service.AttachmentPersistService;
import com.x.teamwork.assemble.control.service.AttachmentQueryService;
import com.x.teamwork.assemble.control.service.DynamicPersistService;
import com.x.teamwork.assemble.control.service.ProjectQueryService;
import com.x.teamwork.assemble.control.service.TaskPersistService;
import com.x.teamwork.assemble.control.service.TaskQueryService;

public class BaseAction extends StandardJaxrsAction {		
	
	protected AttachmentPersistService attachmentPersistService = new AttachmentPersistService();
	
	protected AttachmentQueryService attachmentQueryService = new AttachmentQueryService();
	
	protected 	TaskQueryService taskQueryService = new TaskQueryService();
	
	protected 	ProjectQueryService projectQueryService = new ProjectQueryService();
	
	protected DynamicPersistService dynamicPersistService = new DynamicPersistService();
	
	protected 	TaskPersistService taskPersistService = new TaskPersistService();
	
}
