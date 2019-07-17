package com.x.base.core.project.exception;

import com.x.base.core.entity.JpaObject;

public class ExceptionEntityFieldEmpty extends PromptException {

	private static final long serialVersionUID = -5285504041469792111L;

	public <T extends JpaObject> ExceptionEntityFieldEmpty(Class<T> cls, String field) {
		
		
		
		super("实体类: {} , 字段: {} 不能为空.", cls.getName(), field);
	}

}
