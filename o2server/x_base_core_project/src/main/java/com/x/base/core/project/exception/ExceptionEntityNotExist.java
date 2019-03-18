package com.x.base.core.project.exception;

import com.x.base.core.entity.JpaObject;

public class ExceptionEntityNotExist extends PromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public ExceptionEntityNotExist(String flag, JpaObject jpa) {
		super("标识为:{} 的 {} 对象不存在.", flag, (null == jpa) ? null : jpa.nameOfEntity());
	}

	public <T extends JpaObject> ExceptionEntityNotExist(String flag, Class<T> cls) {
		super("标识为:{} 的 {} 对象不存在.", flag, (null == cls) ? null : cls.getSimpleName());
	}

	public <T extends JpaObject> ExceptionEntityNotExist(String flag, String name) {
		super("标识为:{} 的 {} 对象不存在.", flag, name);
	}

	public <T extends JpaObject> ExceptionEntityNotExist(String flag) {
		super("标识为:{} 的对象不存在.", flag);
	}

}
