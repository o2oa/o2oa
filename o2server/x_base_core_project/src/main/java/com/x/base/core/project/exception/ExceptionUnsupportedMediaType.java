package com.x.base.core.project.exception;

import com.x.base.core.entity.JpaObject;

public class ExceptionUnsupportedMediaType extends LanguagePromptException {

	private static final long serialVersionUID = 1051687204485351261L;

	public static String defaultMessage = "不支持的文件类型:{}.";

	public <T extends JpaObject> ExceptionUnsupportedMediaType(String type) {
		super(defaultMessage,type);
	}

}
