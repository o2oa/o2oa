package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

import com.x.base.core.project.exception.PromptException;

class ProjectTemplateNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectTemplateNotExistsException( String id ) {
		super("指定ID的项目模板信息不存在。ID:" + id );
	}
}
