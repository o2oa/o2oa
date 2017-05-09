package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.exception.PromptException;

public class ImageEncodeBase64Exception extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ImageEncodeBase64Exception( Throwable e, String id ) {
		super("将图片文件编码为Base64编码时发生异常.ID:" + id, e );
	}
}
