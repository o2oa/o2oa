package com.x.collaboration.assemble.websocket;

import com.x.base.core.container.EntityManagerContainer;
import com.x.collaboration.assemble.websocket.factory.DialogFactory;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private DialogFactory component;

	public DialogFactory component() throws Exception {
		if (null == this.component) {
			this.component = new DialogFactory(this);
		}
		return component;
	}
}
