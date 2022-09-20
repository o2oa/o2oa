package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidIpAddress extends LanguagePromptException {

	private static final long serialVersionUID = -4915257511363100070L;

	/**
	 * 修改客户端IP限制 返回文字
	 * 修改 Commons文件夹下 center_zh_CN.properties文件
	 * com.x.organization.assemble.authentication.jaxrs.authentication.ExceptionInvalidIpAddress=当前人员已离职.
	 * @author sy
	 */
	public static String defaultMessage = "客户端IP限制，当前IP：{}.";

	ExceptionInvalidIpAddress(String ip) {
		super(defaultMessage, ip);
	}
}
