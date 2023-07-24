package com.x.organization.assemble.personal.jaxrs.exmail;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

/**
 * 
 * @author ray
 *
 */
abstract class BaseAction extends StandardJaxrsAction {

	protected void checkEnable() throws Exception {
		if (BooleanUtils.isNotTrue(Config.exmail().getEnable())) {
			throw new ExceptionExmailDisable();
		}
	}
}