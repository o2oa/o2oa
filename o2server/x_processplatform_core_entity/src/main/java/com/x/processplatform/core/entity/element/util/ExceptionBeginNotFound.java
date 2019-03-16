package com.x.processplatform.core.entity.element.util;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBeginNotFound extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionBeginNotFound(Integer count) {
		super("没有找到开始节点,开始节点数量:{}.", count);
	}
}
