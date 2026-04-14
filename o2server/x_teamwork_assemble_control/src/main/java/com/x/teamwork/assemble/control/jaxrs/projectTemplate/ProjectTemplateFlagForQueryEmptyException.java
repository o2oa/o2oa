package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

import com.x.base.core.project.exception.PromptException;

class ProjectTemplateFlagForQueryEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ProjectTemplateFlagForQueryEmptyException() {
		super("查询的项目模板信息ID为空，无法继续查询数据。" );
	}
}
