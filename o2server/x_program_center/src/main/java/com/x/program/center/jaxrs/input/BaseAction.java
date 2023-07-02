package com.x.program.center.jaxrs.input;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {

	public enum Method {
		cover, create, ignore;
	}

}