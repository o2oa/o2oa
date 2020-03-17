package com.x.jpush.assemble.control.jaxrs.sample;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSampleEntityClassNameNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSampleEntityClassNameNotExists( String id ) {
		super("指定的示例信息不存在.ID:" + id );
	}
}
