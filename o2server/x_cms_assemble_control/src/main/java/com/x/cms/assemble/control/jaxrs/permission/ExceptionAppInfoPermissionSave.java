package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoPermissionSave extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoPermissionSave( Throwable e, String id ) {
		super( "保存栏目权限时发生异常。ID:{}" ,id, e );
	}
}
