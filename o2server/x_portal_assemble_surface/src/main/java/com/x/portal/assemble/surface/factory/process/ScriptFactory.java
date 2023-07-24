package com.x.portal.assemble.surface.factory.process;

import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.factory.ElementFactory;
import com.x.processplatform.core.entity.element.Script;

public class ScriptFactory extends ElementFactory {

	public ScriptFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Script pick(String flag) throws Exception {
		return this.pick(flag, Script.class);
	}

}