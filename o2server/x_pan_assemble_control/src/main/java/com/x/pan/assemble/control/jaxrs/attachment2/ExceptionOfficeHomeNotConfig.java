package com.x.pan.assemble.control.jaxrs.attachment2;


import com.x.base.core.project.exception.PromptException;

class ExceptionOfficeHomeNotConfig extends PromptException {

	private static final long serialVersionUID = 3741062298910905169L;

	ExceptionOfficeHomeNotConfig() {
		super("未设置office预览服务.");
	}
}
