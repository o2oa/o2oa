package com.x.processplatform.assemble.surface.factory.cms;

import com.x.cms.core.entity.element.Script;
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