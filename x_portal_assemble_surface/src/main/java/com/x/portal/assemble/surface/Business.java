package com.x.portal.assemble.surface;

import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.core.express.Organization;
import com.x.portal.assemble.surface.factory.MenuFactory;
import com.x.portal.assemble.surface.factory.PageFactory;
import com.x.portal.assemble.surface.factory.PortalFactory;
import com.x.portal.assemble.surface.factory.ScriptFactory;
import com.x.portal.assemble.surface.factory.SourceFactory;

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

	private PortalFactory portal;

	public PortalFactory portal() throws Exception {
		if (null == this.portal) {
			this.portal = new PortalFactory(this);
		}
		return portal;
	}

	private MenuFactory menu;

	public MenuFactory menu() throws Exception {
		if (null == this.menu) {
			this.menu = new MenuFactory(this);
		}
		return menu;
	}

	private PageFactory page;

	public PageFactory page() throws Exception {
		if (null == this.page) {
			this.page = new PageFactory(this);
		}
		return page;
	}

	private SourceFactory source;

	public SourceFactory source() throws Exception {
		if (null == this.source) {
			this.source = new SourceFactory(this);
		}
		return source;
	}

	private ScriptFactory script;

	public ScriptFactory script() throws Exception {
		if (null == this.script) {
			this.script = new ScriptFactory(this);
		}
		return script;
	}

}