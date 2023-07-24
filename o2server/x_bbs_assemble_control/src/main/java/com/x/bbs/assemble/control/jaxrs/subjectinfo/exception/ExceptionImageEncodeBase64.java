package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionImageEncodeBase64 extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionImageEncodeBase64( Throwable e, String id ) {
		super("将图片文件编码为Base64编码时发生异常.ID:" + id, e );
	}
}
