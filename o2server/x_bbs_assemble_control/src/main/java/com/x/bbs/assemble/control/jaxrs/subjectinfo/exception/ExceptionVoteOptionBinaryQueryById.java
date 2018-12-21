package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionVoteOptionBinaryQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionVoteOptionBinaryQueryById( Throwable e, String id ) {
		super("系统在根据选项ID查询选项的二进制内容时发生异常.ID:" + id, e );
	}
}
