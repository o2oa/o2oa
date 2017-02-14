package com.x.common.core.application.jaxrs;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;

public abstract class AbstractJaxrsAction {

	protected EffectivePerson effectivePerson(HttpServletRequest request) {
		Object o = request.getAttribute(HttpToken.X_Person);
		if (null != o) {
			return (EffectivePerson) o;
		} else {
			return null;
		}
	}

}