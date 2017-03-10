package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.exception.PromptException;

class FileInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	FileInfoNotExistsException( String id ) {
		super("文件信息不存在，无法继续进行操作。Id:" + id );
	}
}
