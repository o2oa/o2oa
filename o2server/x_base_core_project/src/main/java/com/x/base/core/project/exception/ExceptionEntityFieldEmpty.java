package com.x.base.core.project.exception;

import com.x.base.core.entity.JpaObject;

public class ExceptionEntityFieldEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -5285504041469792111L;

	public static String defaultMessage = "实体类: {} , 字段: {} 值无效.";

	public <T extends JpaObject> ExceptionEntityFieldEmpty(Class<T> cls, String field) {
		super(defaultMessage, cls.getName(), field);
	}

}
