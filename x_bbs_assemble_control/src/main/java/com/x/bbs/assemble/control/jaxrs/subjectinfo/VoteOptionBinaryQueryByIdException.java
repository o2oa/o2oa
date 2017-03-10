package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.x.base.core.exception.PromptException;

class VoteOptionBinaryQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	VoteOptionBinaryQueryByIdException( Throwable e, String id ) {
		super("系统在根据选项ID查询选项的二进制内容时发生异常.ID:" + id, e );
	}
}
