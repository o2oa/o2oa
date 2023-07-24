package com.x.cms.assemble.control.factory.portal;

import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.ElementFactory;
import com.x.portal.core.entity.Script;

public class ScriptFactory extends ElementFactory {

	public ScriptFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Script pick(String flag) throws Exception {
		return this.pick(flag, Script.class);
	}

}
