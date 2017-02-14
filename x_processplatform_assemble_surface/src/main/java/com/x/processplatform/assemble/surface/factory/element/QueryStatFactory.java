package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;
import com.x.processplatform.core.entity.element.QueryStat_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class QueryStatFactory extends ElementFactory {

	public QueryStatFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public QueryStat pick(String flag, Application application, ExceptionWhen exceptionWhen) throws Exception {
		Ehcache cache = ApplicationCache.instance().getCache(QueryStat.class);
		String cacheKey = flag + "#" + application.getId();
		Element element = cache.get(cacheKey);
		QueryStat o = null;
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (QueryStat) element.getObjectValue();
			}
		} else {
			EntityManager em = this.entityManagerContainer().get(QueryStat.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QueryStat> cq = cb.createQuery(QueryStat.class);
			Root<QueryStat> root = cq.from(QueryStat.class);
			Predicate p = cb.equal(root.get(QueryStat_.application), application.getId());
			p = cb.and(p, cb.or(cb.equal(root.get(QueryStat_.id), flag), cb.equal(root.get(QueryStat_.alias), flag),
					cb.equal(root.get(QueryStat_.name), flag)));
			cq.select(root).where(p);
			List<QueryStat> list = em.createQuery(cq).getResultList();
			if (list.isEmpty()) {
				cache.put(new Element(cacheKey, o));
			} else if (list.size() == 1) {
				o = list.get(0);
				em.detach(o);
				cache.put(new Element(cacheKey, o));
			} else {
				throw new Exception("multiple queryStat with flag:" + flag + ".");
			}
		}
		if (o == null && Objects.equals(ExceptionWhen.not_found, exceptionWhen)) {
			throw new Exception("can not find queryStat with flag:" + flag + ".");
		}
		return o;
	}

	public Boolean allowRead(EffectivePerson effectivePerson, QueryStat queryStat, Application application)
			throws Exception {
		if (!StringUtils.equals(queryStat.getApplication(), application.getId())) {
			throw new Exception(
					"queryStat:{id:" + queryStat.getId() + "} not in application{id:" + application.getId() + "}");
		}
		/* 全部为空，没有设置范围 */
		if (ListTools.isEmpty(queryStat.getAvailableCompanyList())
				&& ListTools.isEmpty(queryStat.getAvailableDepartmentList())
				&& ListTools.isEmpty(queryStat.getAvailableIdentityList())
				&& ListTools.isEmpty(queryStat.getAvailablePersonList())) {
			return true;
		}
		if (effectivePerson.isUser(queryStat.getCreatorPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (effectivePerson.isUser(application.getControllerList())) {
			return true;
		}
		if (effectivePerson.isUser(queryStat.getControllerList())) {
			return true;
		}
		if (ListTools.isNotEmpty(queryStat.getAvailablePersonList())) {
			if (ListTools.contains(queryStat.getAvailableIdentityList(), effectivePerson.getName())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryStat.getAvailableIdentityList())) {
			List<WrapIdentity> list = this.business().organization().identity()
					.listWithPerson(effectivePerson.getName());
			if (ListTools.containsAny(ListTools.extractProperty(list, "name", String.class, true, true),
					queryStat.getAvailableIdentityList())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryStat.getAvailableDepartmentList())) {
			List<WrapDepartment> list = this.business().organization().department()
					.listWithPerson(effectivePerson.getName());
			if (ListTools.containsAny(ListTools.extractProperty(list, "name", String.class, true, true),
					queryStat.getAvailableDepartmentList())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryStat.getAvailableCompanyList())) {
			List<WrapCompany> list = this.business().organization().company().listWithPerson(effectivePerson.getName());
			if (ListTools.containsAny(ListTools.extractProperty(list, "name", String.class, true, true),
					queryStat.getAvailableCompanyList())) {
				return true;
			}
		}
		return false;
	}

}