package com.x.base.core.project.exception;

import java.util.Objects;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.EffectivePerson;

public class ExceptionAccessDenied extends PromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public ExceptionAccessDenied(String person) {
		super("用户:{} 权限不足.", person);
	}

	public ExceptionAccessDenied(EffectivePerson effectivePerson) {
		super("用户:{} 权限不足.", effectivePerson.getDistinguishedName());
	}

	public ExceptionAccessDenied(EffectivePerson effectivePerson, JpaObject jpa) {
		super("用户:{} 访问对象 class:{}, id:{}, 权限不足.", effectivePerson.getDistinguishedName(),
				(null == jpa) ? null : jpa.getClass().getName(), (null == jpa) ? null : jpa.getId());
	}

	public ExceptionAccessDenied(EffectivePerson effectivePerson, String message) {
		super("用户:{} 权限不足, {}.", effectivePerson.getDistinguishedName(), Objects.toString(message, ""));
	}

}
