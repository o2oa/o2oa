package com.x.portal.assemble.surface.factory.service;

import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;

/**
 * @author sword
 */
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
