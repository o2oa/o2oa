package com.x.cms.assemble.control.jaxrs.file;

import java.util.Date;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.core.entity.element.File;

abstract class BaseAction extends StandardJaxrsAction {
	
	protected void updateCreator(File file,EffectivePerson effectivePerson){
		file.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		file.setLastUpdateTime(new Date());
	}

}
