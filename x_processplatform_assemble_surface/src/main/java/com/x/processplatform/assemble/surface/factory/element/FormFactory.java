package com.x.processplatform.assemble.surface.factory.element;

import com.x.base.core.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Form;

public class FormFactory extends ElementFactory {

	public FormFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Form pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Form pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Form.class, exceptionWhen, Form.FLAGS);
	}

}