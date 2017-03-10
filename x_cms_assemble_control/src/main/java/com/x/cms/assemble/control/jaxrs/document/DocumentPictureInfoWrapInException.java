package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.exception.PromptException;

class DocumentPictureInfoWrapInException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	DocumentPictureInfoWrapInException( Throwable e ) {
		super("系统将用户传入的数据转换为一个文档大图信息对象时发生异常。", e );
	}
}
