package com.x.program.center;

import com.x.base.core.project.script.AbstractResources;
import com.x.organization.core.express.Organization;

/**
 * 用于在脚本执行中注入对象
 */
public class AgentEvalResources extends AbstractResources {
	private Organization organization;

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}