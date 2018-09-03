package com.x.crm.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.crm.assemble.control.jaxrs.clue.ClueBaseInfoAction;
import com.x.crm.assemble.control.jaxrs.crmbaseconfig.CrmBaseConfigAction;
import com.x.crm.assemble.control.jaxrs.crmbaseconfig.RegionConfigAction;
import com.x.crm.assemble.control.jaxrs.customer.CustomerBaseInfoAction;
import com.x.crm.assemble.control.jaxrs.opportunity.OpportunityBaseInfoAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		this.classes.add(CustomerBaseInfoAction.class);
		this.classes.add(OpportunityBaseInfoAction.class);
		this.classes.add(ClueBaseInfoAction.class);
		this.classes.add(CrmBaseConfigAction.class);
		this.classes.add(RegionConfigAction.class);
		return this.classes;
	}

}