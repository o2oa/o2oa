package com.x.server.console.action;

import javax.wsdl.Definition;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Bind;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.OauthCode;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonCard;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.accredit.Empower;
import com.x.organization.core.entity.accredit.EmpowerLog;

public class EraseContentOrg extends EraseContent {

	private static Logger logger = LoggerFactory.getLogger(EraseContentOrg.class);
	
	@Override
	public boolean execute() throws Exception {
		this.init("org", null);
		addClass(Group.class);
		addClass(Role.class);
		addClass(UnitDuty.class);
		addClass(UnitAttribute.class);
		addClass(Unit.class);
		addClass(PersonCard.class);
		addClass(Bind.class);
		addClass(Definition.class);
		addClass(OauthCode.class);
		addClass(Empower.class);
		addClass(EmpowerLog.class);
		addClass(Identity.class);
		addClass(Custom.class);
		addClass(PersonAttribute.class);
		addClass(Person.class);
		this.run();
		return true;
	}
}