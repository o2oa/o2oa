package com.x.portal.assemble.surface;

import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.core.express.Organization;
import com.x.portal.assemble.surface.factory.FileFactory;
import com.x.portal.assemble.surface.factory.PageFactory;
import com.x.portal.assemble.surface.factory.PortalFactory;
import com.x.portal.assemble.surface.factory.ScriptFactory;
import com.x.portal.assemble.surface.factory.WidgetFactory;

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

	private WidgetFactory widget;

	public WidgetFactory widget() throws Exception {
		if (null == this.widget) {
			this.widget = new WidgetFactory(this);
		}
		return widget;
	}

	private PageFactory page;

	public PageFactory page() throws Exception {
		if (null == this.page) {
			this.page = new PageFactory(this);
		}
		return page;
	}

	private ScriptFactory script;

	public ScriptFactory script() throws Exception {
		if (null == this.script) {
			this.script = new ScriptFactory(this);
		}
		return script;
	}

	private FileFactory file;

	public FileFactory file() throws Exception {
		if (null == this.file) {
			this.file = new FileFactory(this);
		}
		return file;
	}

}