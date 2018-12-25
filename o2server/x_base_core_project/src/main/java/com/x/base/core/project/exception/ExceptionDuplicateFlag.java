package com.x.base.core.project.exception;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;

public class ExceptionDuplicateFlag extends PromptException {

	private static final long serialVersionUID = 1051687204485351261L;

	public <T extends JpaObject> ExceptionDuplicateFlag(Class<T> cls, String value) {
		super("实体类:{}, 标识:{}, 与已有值重复.", cls.getName(), value);
	}

	public <T extends JpaObject> ExceptionDuplicateFlag(Class<T> cls, List<String> values) {
		super("实体类:{}, 标识:{}, 与已有值重复.", cls.getName(), StringUtils.join(values, ","));
	}

}
