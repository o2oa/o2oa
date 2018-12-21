package com.x.component.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.component.assemble.control.factory.ComponentFactory;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private ComponentFactory component;

	public ComponentFactory component() throws Exception {
		if (null == this.component) {
			this.component = new ComponentFactory(this);
		}
		return component;
	}

	public boolean componentEditAvailable(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		return false;
	}
}
