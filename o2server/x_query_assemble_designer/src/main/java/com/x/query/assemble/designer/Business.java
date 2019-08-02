package com.x.query.assemble.designer;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.scripting.Scripting;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.webservices.WebservicesClient;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.designer.factory.QueryFactory;
import com.x.query.assemble.designer.factory.RevealFactory;
import com.x.query.assemble.designer.factory.StatFactory;
import com.x.query.assemble.designer.factory.StatementFactory;
import com.x.query.assemble.designer.factory.TableFactory;
import com.x.query.assemble.designer.factory.ViewFactory;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

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

	private TableFactory table;

	public TableFactory table() throws Exception {
		if (null == this.table) {
			this.table = new TableFactory(this);
		}
		return table;
	}

	private StatementFactory statement;

	public StatementFactory statement() throws Exception {
		if (null == this.statement) {
			this.statement = new StatementFactory(this);
		}
		return statement;
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

	public ScriptingEngine createScriptEngine() {
		ScriptingEngine engine = Scripting.getEngine();
		engine.bindingOrganization(this.organization);
		engine.bindingWebservicesClient(new WebservicesClient());
		return engine;
	}

	public boolean controllable(EffectivePerson effectivePerson) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
			result = true;
		}
		return result;
	}

	public boolean editable(EffectivePerson effectivePerson, Query o) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
			result = true;
		}
		if (!result && (null != o)) {
			if (effectivePerson.isPerson(o.getControllerList()) || effectivePerson.isPerson(o.getCreatorPerson())) {
				result = true;
			}
		}
		return result;
	}

	public boolean editable(EffectivePerson effectivePerson, Table o) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
			result = true;
		}
		if (!result && (null != o)) {
			if (ListTools.isEmpty(o.getEditPersonList()) && ListTools.isEmpty(o.getEditUnitList())) {
				result = true;
				if (!result) {
					if (effectivePerson.isPerson(o.getEditPersonList())) {
						result = true;
					}
					if (!result && ListTools.isNotEmpty(o.getEditUnitList())) {
						List<String> units = this.organization().unit()
								.listWithPerson(effectivePerson.getDistinguishedName());
						if (ListTools.containsAny(units, o.getEditUnitList())) {
							result = true;
						}
					}
				}
			}
		}
		return result;
	}

	public boolean executable(EffectivePerson effectivePerson, Statement o) throws Exception {
		boolean result = false;
		if (null != o) {
			if (ListTools.isEmpty(o.getExecutePersonList()) && ListTools.isEmpty(o.getExecuteUnitList())) {
				result = true;
			}
			if (!result) {
				if (effectivePerson.isManager()
						|| (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
								OrganizationDefinition.QueryManager))
						|| effectivePerson.isPerson(o.getExecutePersonList())) {
					result = true;
				}
				if ((!result) && ListTools.isNotEmpty(o.getExecuteUnitList())) {
					List<String> units = this.organization().unit()
							.listWithPerson(effectivePerson.getDistinguishedName());
					if (ListTools.containsAny(units, o.getExecuteUnitList())) {
						result = true;
					}
				}
			}
		}
		return result;
	}
}