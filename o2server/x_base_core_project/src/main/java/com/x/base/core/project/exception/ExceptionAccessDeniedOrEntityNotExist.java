package com.x.base.core.project.exception;

import java.util.Objects;

import com.x.base.core.project.http.EffectivePerson;

public class ExceptionAccessDeniedOrEntityNotExist extends PromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public ExceptionAccessDeniedOrEntityNotExist(String person) {
		super("用户:{} 权限不足或者对象不存在.", person);
	}

	public ExceptionAccessDeniedOrEntityNotExist(EffectivePerson effectivePerson) {
		super("用户:{} 权限不足或者对象不存在.", effectivePerson.getDistinguishedName());
	}

	public ExceptionAccessDeniedOrEntityNotExist(EffectivePerson effectivePerson, String message) {
		super("用户:{} 权限不足或者对象不存在, {}.", effectivePerson.getDistinguishedName(), Objects.toString(message, ""));
	}

}
