package com.x.pan.assemble.control.jaxrs.folder3;

import com.x.base.core.project.exception.PromptException;

class ExceptionMoveDenied extends PromptException {

	private static final long serialVersionUID = -5921306337885212054L;

	ExceptionMoveDenied() {
		super("禁止转移到该目录.");
	}
}
