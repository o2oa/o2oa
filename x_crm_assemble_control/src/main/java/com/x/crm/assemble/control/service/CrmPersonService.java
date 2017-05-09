package com.x.crm.assemble.control.service;

import com.x.base.core.http.EffectivePerson;

public class CrmPersonService {

	public String getLoginName(EffectivePerson effectivePerson) {
		return effectivePerson.getName();
	}
}
