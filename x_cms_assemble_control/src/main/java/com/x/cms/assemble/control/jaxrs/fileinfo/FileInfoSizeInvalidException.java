package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.exception.PromptException;

class FileInfoSizeInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	FileInfoSizeInvalidException() {
		super("附件文件信息大小size参数不合法，不是数字，无法进行查询操作。" );
	}
}
