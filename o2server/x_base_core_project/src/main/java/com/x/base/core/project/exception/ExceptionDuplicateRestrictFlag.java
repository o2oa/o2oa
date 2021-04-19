package com.x.base.core.project.exception;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;

public class ExceptionDuplicateRestrictFlag extends LanguagePromptException {

	private static final long serialVersionUID = 1051687204485351261L;

	public static String defaultMessage = "实体类:{}, 标识:{}, 与指定类型下的已有值重复.";

	public <T extends JpaObject> ExceptionDuplicateRestrictFlag(Class<T> cls, String value) {
		super(defaultMessage, cls.getName(), value);
	}

	public <T extends JpaObject> ExceptionDuplicateRestrictFlag(Class<T> cls, List<String> values) {
		super(defaultMessage, cls.getName(), StringUtils.join(values, ","));
	}

}
