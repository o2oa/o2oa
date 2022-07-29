package com.x.cms.assemble.control.factory.process;

import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.ElementFactory;
import com.x.processplatform.core.entity.element.Form;

public class FormFactory extends ElementFactory {

	public FormFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Form pick(String flag) throws Exception {
		return this.pick(flag, Form.class);
	}

}
