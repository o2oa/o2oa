package com.x.base.core.project.exception;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;

public class ExceptionEntityNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public static String defaultMessage = "标识为:{} 的 {} 对象不存在.";

	public ExceptionEntityNotExist(String flag, JpaObject jpa) {
		super(defaultMessage, flag, (null == jpa) ? null : jpa.nameOfEntity());
	}

	public <T extends JpaObject> ExceptionEntityNotExist(String flag, Class<T> cls) {
		super(defaultMessage, flag, (null == cls) ? null : cls.getSimpleName());
	}

	public <T extends JpaObject> ExceptionEntityNotExist(List<String> ids, Class<T> cls) {
		super(defaultMessage, (null == ids) ? null : StringUtils.join(ids, ","),
				(null == cls) ? null : cls.getSimpleName());
	}

	public <T extends JpaObject> ExceptionEntityNotExist(String flag, String name) {
		super(defaultMessage, flag, name);
	}

	public <T extends JpaObject> ExceptionEntityNotExist(String flag) {
		super("标识为:{} 的对象不存在.", flag);
		this.setLanguageKey(this.getClass().getName()+"_1");
	}

	public <T extends JpaObject> ExceptionEntityNotExist(Class<T> cls) {
		super("类型为: {} 的对象不存在.", cls.getSimpleName());
		this.setLanguageKey(this.getClass().getName()+"_2");
	}

}
