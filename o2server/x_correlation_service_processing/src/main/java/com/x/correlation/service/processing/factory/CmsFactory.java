package com.x.correlation.service.processing.factory;

import com.x.correlation.service.processing.AbstractFactory;
import com.x.correlation.service.processing.Business;

public class CmsFactory extends AbstractFactory {

	public CmsFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	private ScriptFactory script;

	public ScriptFactory script() throws Exception {
		if (null == this.script) {
			this.script = new ScriptFactory(this.business());
		}
		return script;
	}

}
