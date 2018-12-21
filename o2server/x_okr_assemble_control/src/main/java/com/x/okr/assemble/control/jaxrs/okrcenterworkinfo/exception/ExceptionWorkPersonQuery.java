package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionWorkPersonQuery extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWorkPersonQuery( Throwable e, String person, String processIdentity ) {
		super("根据用户身份份和在工作中的处理身份查询工作干系人信息列表时发生异常。Person:" + person + ", ProcessIdentity:" + processIdentity, e );
	}
	
	public ExceptionWorkPersonQuery( Throwable e, String centerId, String person, String processIdentity ) {
		super("根据用户身份份和在工作中的处理身份查询工作干系人信息列表时发生异常。CenterId: "+ centerId +", Person:" + person + ", ProcessIdentity:" + processIdentity, e );
	}
}
