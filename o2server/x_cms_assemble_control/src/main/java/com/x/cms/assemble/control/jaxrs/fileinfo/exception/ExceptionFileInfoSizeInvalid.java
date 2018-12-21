package com.x.cms.assemble.control.jaxrs.fileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFileInfoSizeInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFileInfoSizeInvalid() {
		super("附件文件信息大小size参数不合法，不是数字，无法进行查询操作。" );
	}
}
