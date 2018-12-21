package com.x.query.assemble.designer;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.designer.factory.QueryFactory;
import com.x.query.assemble.designer.factory.RevealFactory;
import com.x.query.assemble.designer.factory.StatFactory;
import com.x.query.assemble.designer.factory.ViewFactory;
import com.x.query.core.entity.Query;

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
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	private QueryFactory query;

	public QueryFactory query() throws Exception {
		if (null == this.query) {
			this.query = new QueryFactory(this);
		}
		return query;
	}

	private ViewFactory view;

	public ViewFactory view() throws Exception {
		if (null == this.view) {
			this.view = new ViewFactory(this);
		}
		return view;
	}

	private StatFactory stat;

	public StatFactory stat() throws Exception {
		if (null == this.stat) {
			this.stat = new StatFactory(this);
		}
		return stat;
	}

	private RevealFactory reveal;

	public RevealFactory reveal() throws Exception {
		if (null == this.reveal) {
			this.reveal = new RevealFactory(this);
		}
		return reveal;
	}

	public boolean editable(EffectivePerson effectivePerson, Query o) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager()
				|| (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.QueryManager))) {
			result = true;
		}
		if (!result && (null != o)) {
			if (effectivePerson.isUser(o.getControllerList()) || effectivePerson.isUser(o.getCreatorPerson())) {
				result = true;
			}
		}
		return result;
	}
}