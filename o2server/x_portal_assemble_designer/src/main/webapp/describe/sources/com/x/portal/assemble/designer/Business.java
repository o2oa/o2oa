package com.x.portal.assemble.designer;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.core.express.Organization;
import com.x.portal.assemble.designer.factory.FileFactory;
import com.x.portal.assemble.designer.factory.PageFactory;
import com.x.portal.assemble.designer.factory.PortalFactory;
import com.x.portal.assemble.designer.factory.ScriptFactory;
import com.x.portal.assemble.designer.factory.TemplatePageFactory;
import com.x.portal.assemble.designer.factory.WidgetFactory;
import com.x.portal.core.entity.Portal;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	public boolean isPortalManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()
				|| (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.PortalManager))) {
			return true;
		}
		return false;
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

	private TemplatePageFactory templatePage;

	public TemplatePageFactory templatePage() throws Exception {
		if (null == this.templatePage) {
			this.templatePage = new TemplatePageFactory(this);
		}
		return templatePage;
	}

	public boolean editable(EffectivePerson effectivePerson, Portal o) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager()
				|| (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.PortalManager))) {
			result = true;
		}
		if (!result && (null != o)) {
			if (effectivePerson.isPerson(o.getControllerList()) || effectivePerson.isPerson(o.getCreatorPerson())) {
				result = true;
			}
		}
		return result;
	}
}