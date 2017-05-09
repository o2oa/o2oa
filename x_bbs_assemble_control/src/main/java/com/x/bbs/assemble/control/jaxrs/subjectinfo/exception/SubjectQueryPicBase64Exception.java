package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.exception.PromptException;

public class SubjectQueryPicBase64Exception extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SubjectQueryPicBase64Exception( Throwable e, String id ) {
		super("根据指定ID查询主题图片编码内容时发生异常.ID:" + id, e );
	}
}
