package com.x.portal.assemble.surface.factory.cms;

import com.x.cms.core.entity.element.Script;
import com.x.portal.assemble.surface.Business;
import com.x.portal.assemble.surface.factory.ElementFactory;

public class ScriptFactory extends ElementFactory {

	public ScriptFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Script pick(String flag) throws Exception {
		return this.pick(flag, Script.class);
	}

}