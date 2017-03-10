package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.exception.PromptException;

class GetCompanyNameByIdentityException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	GetCompanyNameByIdentityException( Throwable e, String identity ) {
		super("系统在根据用户身份信息查询所属公司名称时发生异常。Identity:" + identity );
	}
}
