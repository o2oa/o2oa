package com.x.teamwork.assemble.control.jaxrs.dynamic;

import com.x.base.core.project.exception.PromptException;

class ProjectNotExistsException extends PromptException {

	private static final long serialVersionUID = -3324939578747953765L;

	ProjectNotExistsException(String id ) {
		super("指定ID的项目信息不存在。ID:" + id );
	}
}
