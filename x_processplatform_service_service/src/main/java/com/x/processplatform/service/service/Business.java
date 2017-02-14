package com.x.processplatform.service.service;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.ListTools;
import com.x.organization.core.express.Organization;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.service.service.factory.WorkFactory;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization();
		}
		return organization;
	}

	private WorkFactory work;

	public WorkFactory work() throws Exception {
		if (null == this.work) {
			this.work = new WorkFactory(this);
		}
		return work;
	}

	public void isManager(EffectivePerson effectivePerson, ExceptionWhen exceptionWhen) throws Exception {
		boolean available = this.isManager(effectivePerson);
		if ((!available) && exceptionWhen.equals(ExceptionWhen.not_allow)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
	}

	public Boolean isManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.organization().role().hasAny(effectivePerson.getName(), RoleDefinition.ProcessPlatformManager)) {
			return true;
		}
		return false;
	}

	public void checkServicePermission(HttpServletRequest request, Business business, EffectivePerson effectivePerson,
			Work work, Service service) throws Exception {
		for (;;) {
			if ((null == service.getTrustAddressList()) || (service.getTrustAddressList().isEmpty())) {
				break;
			}
			if (ListTools.contains(service.getTrustAddressList(), request.getRemoteAddr())) {
				break;
			}
			if (business.isManager(effectivePerson)) {
				break;
			}
			throw new Exception("not trust address:" + request.getRemoteHost() + ", or " + effectivePerson.getName()
					+ " has sufficient permissions.");
		}
	}
}
