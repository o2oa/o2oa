package com.x.query.service.processing.jaxrs.table;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionBundleEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -87643358931771164L;

	public ExceptionBundleEmpty() {
		super("标识不能为空.");
	}

}
