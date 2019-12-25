package com.x.query.assemble.surface;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.express.Organization;
import com.x.query.assemble.surface.factory.QueryFactory;
import com.x.query.assemble.surface.factory.RevealFactory;
import com.x.query.assemble.surface.factory.StatFactory;
import com.x.query.assemble.surface.factory.ViewFactory;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Reveal;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class Business {

	private static Ehcache cache = ApplicationCache.instance().getCache(Query.class, View.class, Stat.class,
			Reveal.class, Table.class, Statement.class);

	public Ehcache cache() {
		return cache;
	}

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

	@SuppressWarnings("unchecked")
	public <T extends JpaObject> T pick(String flag, Class<T> cls) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey(cls, flag);
		Element element = cache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			return (T) element.getObjectValue();
		} else {
			T t = this.entityManagerContainer().flag(flag, cls);
			if (null != t) {
				entityManagerContainer().get(cls).detach(t);
				cache.put(new Element(cacheKey, t));
				return t;
			}
			return null;
		}
	}

 
	public boolean readable(EffectivePerson effectivePerson, Query query) throws Exception {
		if (null == query) {
			return false;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (StringUtils.equals(effectivePerson.getDistinguishedName(), query.getCreatorPerson())
				|| query.getControllerList().contains(effectivePerson.getDistinguishedName())) {
			return true;
		}
		if (query.getAvailableIdentityList().isEmpty() && query.getAvailableUnitList().isEmpty()) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.QueryManager)) {
			return true;
		}
		if (CollectionUtils.containsAny(query.getAvailableIdentityList(),
				organization().identity().listWithPerson(effectivePerson))) {
			return true;
		}
		if (CollectionUtils.containsAny(query.getAvailableUnitList(),
				organization().unit().listWithPersonSupNested(effectivePerson))) {
			return true;
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, View view) throws Exception {
		if (null == view) {
			return false;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (view.getAvailableIdentityList().isEmpty() && view.getAvailableUnitList().isEmpty()) {
			return true;
		}
		if (CollectionUtils.containsAny(view.getAvailableIdentityList(),
				organization().identity().listWithPerson(effectivePerson))) {
			return true;
		}
		if (CollectionUtils.containsAny(view.getAvailableUnitList(),
				organization().unit().listWithPersonSupNested(effectivePerson))) {
			return true;
		}
		Query query = this.entityManagerContainer().find(view.getQuery(), Query.class);
		/** 在所属query的管理人员中 */
		if (null != query && ListTools.contains(query.getControllerList(), effectivePerson.getDistinguishedName())) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.QueryManager)) {
			return true;
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, Stat stat) throws Exception {
		if (null == stat) {
			return false;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (stat.getAvailableIdentityList().isEmpty() && stat.getAvailableUnitList().isEmpty()) {
			return true;
		}
		if (CollectionUtils.containsAny(stat.getAvailableIdentityList(),
				organization().identity().listWithPerson(effectivePerson))) {
			return true;
		}
		if (CollectionUtils.containsAny(stat.getAvailableUnitList(),
				organization().unit().listWithPersonSupNested(effectivePerson))) {
			return true;
		}
		Query query = this.entityManagerContainer().find(stat.getQuery(), Query.class);
		/** 在所属query的管理人员中 */
		if (null != query && ListTools.contains(query.getControllerList(), effectivePerson.getDistinguishedName())) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.QueryManager)) {
			return true;
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, Reveal reveal) throws Exception {
		if (null == reveal) {
			return false;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (reveal.getAvailableIdentityList().isEmpty() && reveal.getAvailableUnitList().isEmpty()) {
			return true;
		}
		if (CollectionUtils.containsAny(reveal.getAvailableIdentityList(),
				organization().identity().listWithPerson(effectivePerson))) {
			return true;
		}
		if (CollectionUtils.containsAny(reveal.getAvailableUnitList(),
				organization().unit().listWithPersonSupNested(effectivePerson))) {
			return true;
		}
		Query query = this.entityManagerContainer().find(reveal.getQuery(), Query.class);
		/** 在所属query的管理人员中 */
		if (null != query && ListTools.contains(query.getControllerList(), effectivePerson.getDistinguishedName())) {
			return true;
		}
		if (organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.QueryManager)) {
			return true;
		}
		return false;
	}

	public boolean readable(EffectivePerson effectivePerson, Table o) throws Exception {
		boolean result = false;
		if (null != o) {
			if (ListTools.isEmpty(o.getReadPersonList()) && ListTools.isEmpty(o.getReadUnitList())) {
				result = true;
			}
			if (!result) {
				if (effectivePerson.isManager() || (this.organization().person().hasRole(effectivePerson,
						OrganizationDefinition.Manager, OrganizationDefinition.QueryManager))) {
					result = true;
				}
				if (!result) {
					if (effectivePerson.isPerson(o.getEditPersonList())
							|| effectivePerson.isPerson(o.getReadPersonList())) {
						result = true;
					}
					if (!result && (ListTools.isNotEmpty(o.getEditUnitList())
							|| ListTools.isNotEmpty(o.getReadUnitList()))) {
						List<String> units = this.organization().unit()
								.listWithPerson(effectivePerson.getDistinguishedName());
						if (ListTools.containsAny(units, o.getEditUnitList())
								|| ListTools.containsAny(units, o.getReadUnitList())) {
							result = true;
						}
					}
				}
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