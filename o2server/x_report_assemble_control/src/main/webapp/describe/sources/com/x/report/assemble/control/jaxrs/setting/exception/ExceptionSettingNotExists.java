package com.x.report.assemble.control.jaxrs.setting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSettingNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSettingNotExists( String id ) {
		super("指定的设置信息不存在.ID:" + id );
	}
}
