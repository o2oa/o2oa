package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.exception.PromptException;

class FileInfoIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	FileInfoIdEmptyException() {
		super("附件文件信息ID为空，无法进行查询操作。" );
	}
}
