package com.x.cms.assemble.control.factory.process;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;

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
