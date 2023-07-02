package com.x.portal.assemble.surface.factory.process;

import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;

public class ProcessFactory extends AbstractFactory {

	public ProcessFactory(Business abstractBusiness) throws Exception {
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
