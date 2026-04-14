package com.x.teamwork.assemble.control.jaxrs.projectgroup;

import com.x.base.core.project.exception.PromptException;

class ProjectGroupFlagForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectGroupFlagForQueryEmptyException() {
		super("查询的项目组信息ID为空，无法继续查询数据。" );
	}
}
