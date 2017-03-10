package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import com.x.base.core.exception.PromptException;

class PermissionListBySectionException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PermissionListBySectionException( Throwable e, String sectionId ) {
		super("根据指定的版块列示所有的权限信息时时发生异常.Section:" + sectionId, e );
	}
}
