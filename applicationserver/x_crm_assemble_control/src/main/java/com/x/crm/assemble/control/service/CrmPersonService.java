package com.x.crm.assemble.control.service;

import com.x.base.core.project.http.EffectivePerson;

public class CrmPersonService {

	public String getLoginName(EffectivePerson effectivePerson) {
		return effectivePerson.getDistinguishedName();
	}
}
