package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionConfigSettingNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigSettingNotExists( String id ) {
		super("指定ID的BBS系统设置不存在.ID:" + id );
	}
}
