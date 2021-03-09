package com.x.base.core.project.exception;

import java.util.List;

import com.x.base.core.project.tools.LanguageTools;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;

public class ExceptionEntityNotExist extends PromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public ExceptionEntityNotExist(String flag, JpaObject jpa) {
		super(LanguageTools.getValue("exception_entityNotExist"), flag, (null == jpa) ? null : jpa.nameOfEntity());
	}

	public <T extends JpaObject> ExceptionEntityNotExist(String flag, Class<T> cls) {
		super(LanguageTools.getValue("exception_entityNotExist"), flag, (null == cls) ? null : cls.getSimpleName());
	}

	public <T extends JpaObject> ExceptionEntityNotExist(List<String> ids, Class<T> cls) {
		super(LanguageTools.getValue("exception_entityNotExist"), (null == ids) ? null : StringUtils.join(ids, ","),
				(null == cls) ? null : cls.getSimpleName());
	}

	public <T extends JpaObject> ExceptionEntityNotExist(String flag, String name) {
		super(LanguageTools.getValue("exception_entityNotExist"), flag, name);
	}

	public <T extends JpaObject> ExceptionEntityNotExist(String flag) {
		super(LanguageTools.getValue("exception_entityNotExist_1"), flag);
	}

	public <T extends JpaObject> ExceptionEntityNotExist(Class<T> cls) {
		super(LanguageTools.getValue("exception_entityNotExist_2"), cls.getSimpleName());
	}

}
