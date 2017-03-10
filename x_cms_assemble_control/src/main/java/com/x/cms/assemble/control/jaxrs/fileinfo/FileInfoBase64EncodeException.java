package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.exception.PromptException;

class FileInfoBase64EncodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	FileInfoBase64EncodeException( Throwable e, String id ) {
		super("系统将文件转换为Base64编码发生异常。Id:" + id );
	}
}
