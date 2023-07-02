package com.x.cms.assemble.control.factory.service;


import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;

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
