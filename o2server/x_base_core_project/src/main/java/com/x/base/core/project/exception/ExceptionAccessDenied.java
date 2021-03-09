package com.x.base.core.project.exception;

import java.util.Objects;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.LanguageTools;

public class ExceptionAccessDenied extends PromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public ExceptionAccessDenied(String person) {
		super(LanguageTools.getValue("exception_accessDenied"), person);
	}

	public ExceptionAccessDenied(EffectivePerson effectivePerson) {
		super(LanguageTools.getValue("exception_accessDenied"), effectivePerson.getDistinguishedName());
	}

	public ExceptionAccessDenied(EffectivePerson effectivePerson, JpaObject jpa) {
		super(LanguageTools.getValue("exception_accessDenied_1"), effectivePerson.getDistinguishedName(),
				(null == jpa) ? null : jpa.getClass().getName(), (null == jpa) ? null : jpa.getId());
	}

	public ExceptionAccessDenied(EffectivePerson effectivePerson, String message) {
		super(LanguageTools.getValue("exception_accessDenied"), effectivePerson.getDistinguishedName(), Objects.toString(message, ""));
	}

}
