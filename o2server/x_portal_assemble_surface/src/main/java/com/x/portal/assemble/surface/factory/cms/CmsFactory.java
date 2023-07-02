package com.x.portal.assemble.surface.factory.cms;

import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;

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
