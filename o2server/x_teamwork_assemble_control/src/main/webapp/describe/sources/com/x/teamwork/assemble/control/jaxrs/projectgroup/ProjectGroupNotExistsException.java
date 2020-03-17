package com.x.teamwork.assemble.control.jaxrs.projectgroup;

import com.x.base.core.project.exception.PromptException;

class ProjectGroupNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectGroupNotExistsException( String id ) {
		super("指定ID的项目组信息不存在。ID:" + id );
	}
}
