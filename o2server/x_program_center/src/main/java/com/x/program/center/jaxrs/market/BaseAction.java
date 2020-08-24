package com.x.program.center.jaxrs.market;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

abstract class BaseAction extends StandardJaxrsAction {

	public boolean hasAuth(EffectivePerson effectivePerson, String person){
		if(effectivePerson.isManager()){
			return true;
		}
		if(effectivePerson.getDistinguishedName().equals(person)){
			return true;
		}
		return false;
	}

}
