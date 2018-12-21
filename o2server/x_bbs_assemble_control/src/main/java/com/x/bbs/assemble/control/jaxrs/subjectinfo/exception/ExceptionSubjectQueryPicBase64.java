package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectQueryPicBase64 extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectQueryPicBase64( Throwable e, String id ) {
		super("根据指定ID查询主题图片编码内容时发生异常.ID:" + id, e );
	}
}
