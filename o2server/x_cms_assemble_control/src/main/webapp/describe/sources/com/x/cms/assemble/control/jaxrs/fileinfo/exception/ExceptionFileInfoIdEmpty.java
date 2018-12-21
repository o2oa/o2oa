package com.x.cms.assemble.control.jaxrs.fileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFileInfoIdEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFileInfoIdEmpty() {
		super("附件文件信息ID为空，无法进行查询操作。" );
	}
}
