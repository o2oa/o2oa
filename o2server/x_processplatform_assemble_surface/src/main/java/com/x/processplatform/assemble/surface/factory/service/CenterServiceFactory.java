package com.x.processplatform.assemble.surface.factory.service;

import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;

public class CenterServiceFactory extends AbstractFactory {

	public CenterServiceFactory(Business abstractBusiness) throws Exception {
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
