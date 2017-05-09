package com.x.organization.assemble.control.alpha.jaxrs.inputperson;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class ActionBase extends StandardJaxrsAction {
	protected static List<String> genderTypeFemaleItems = Arrays.asList(new String[] { "f", "女", "female" });
	protected static List<String> genderTypeMaleItems = Arrays.asList(new String[] { "m", "男", "male" });
}