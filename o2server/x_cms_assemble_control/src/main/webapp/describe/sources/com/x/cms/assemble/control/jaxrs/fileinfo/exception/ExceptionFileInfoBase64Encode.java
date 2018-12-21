package com.x.cms.assemble.control.jaxrs.fileinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFileInfoBase64Encode extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFileInfoBase64Encode( Throwable e, String id ) {
		super("系统将文件转换为Base64编码发生异常。Id:" + id );
	}
}
