package com.x.processplatform.assemble.surface.factory.portal;

import com.x.portal.core.entity.Script;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.factory.element.ElementFactory;

public class ScriptFactory extends ElementFactory {

	public ScriptFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Script pick(String flag) throws Exception {
		return this.pick(flag, Script.class);
	}

}