package com.x.base.core.project.exception;

import java.util.Objects;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.EffectivePerson;

public class ExceptionAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public static String defaultMessage = "用户:{} 权限不足.";

	public ExceptionAccessDenied(String person) {
		super(defaultMessage, person);
	}

	public ExceptionAccessDenied(EffectivePerson effectivePerson) {
		super(defaultMessage, effectivePerson.getDistinguishedName());
		this.setLanguageKey(this.getClass().getName());
	}

	public ExceptionAccessDenied(EffectivePerson effectivePerson, JpaObject jpa) {
		super("用户:{} 访问对象 class:{}, id:{}, 权限不足.", effectivePerson.getDistinguishedName(),
				(null == jpa) ? null : jpa.getClass().getName(), (null == jpa) ? null : jpa.getId());
		this.setLanguageKey(this.getClass().getName() + "_1");
	}

	public ExceptionAccessDenied(EffectivePerson effectivePerson, String flag) {
		super("用户:{} 访问对象:{}, 权限不足.", effectivePerson.getDistinguishedName(), Objects.toString(flag, ""));
	}

	public ExceptionAccessDenied(String person, String flag) {
		super("用户:{} 访问对象 id:{}, 权限不足.", person, flag);
	}
}
