package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionFileInfoIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFileInfoIdEmpty() {
		super("附件文件信息ID为空，无法进行查询操作。" );
	}
}
