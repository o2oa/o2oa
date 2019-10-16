package com.x.base.core.project.exception;

import com.x.base.core.entity.JpaObject;

public class ExceptionUnsupportedMediaType extends PromptException {

	private static final long serialVersionUID = 1051687204485351261L;

	public <T extends JpaObject> ExceptionUnsupportedMediaType(String type) {
		super("不支持的文件类型:{}.",type);
	}

}
