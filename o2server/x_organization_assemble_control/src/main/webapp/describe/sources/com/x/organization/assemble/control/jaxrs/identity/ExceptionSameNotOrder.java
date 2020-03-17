package com.x.organization.assemble.control.jaxrs.identity;

import com.x.base.core.project.exception.PromptException;

class ExceptionSameNotOrder extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionSameNotOrder(String flag, String otherFlag) {
		super("无法对同一个身份进行排序:{}, :{}.", flag, otherFlag);
	}
}
