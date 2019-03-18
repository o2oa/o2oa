package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.element.QueryView_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class QueryViewFactory extends ElementFactory {

	public QueryViewFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public QueryView pick(String flag, Application application) throws Exception {
		return this.pick(flag, application, ExceptionWhen.none);
	}

	public QueryView pick(String flag, Application application, ExceptionWhen exceptionWhen) throws Exception {
		Ehcache cache = ApplicationCache.instance().getCache(QueryView.class);
		String cacheKey = flag + "#" + application.getId();
		Element element = cache.get(cacheKey);
		QueryView o = null;
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (QueryView) element.getObjectValue();
			}
		} else {
			EntityManager em = this.entityManagerContainer().get(QueryView.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QueryView> cq = cb.createQuery(QueryView.class);
			Root<QueryView> root = cq.from(QueryView.class);
			Predicate p = cb.equal(root.get(QueryView_.application), application.getId());
			p = cb.and(p, cb.or(cb.equal(root.get(QueryView_.id), flag), cb.equal(root.get(QueryView_.alias), flag),
					cb.equal(root.get(QueryView_.name), flag)));
			cq.select(root).where(p);
			List<QueryView> list = em.createQuery(cq).getResultList();
			if (list.isEmpty()) {
				cache.put(new Element(cacheKey, o));
			} else if (list.size() == 1) {
				o = list.get(0);
				em.detach(o);
				cache.put(new Element(cacheKey, o));
			} else {
				throw new Exception("multiple queryView with flag:" + flag + ".");
			}
		}
		if (o == null && Objects.equals(ExceptionWhen.not_found, exceptionWhen)) {
			throw new Exception("can not find queryView with flag:" + flag + ".");
		}
		return o;
	}

	public Boolean allowRead(EffectivePerson effectivePerson, QueryView queryView, Application application)
			throws Exception {
		if (!StringUtils.equals(queryView.getApplication(), application.getId())) {
			throw new Exception(
					"queryView:{id:" + queryView.getId() + "} not in application{id:" + application.getId() + "}");
		}
		/* 全部为空，没有设置范围 */
		if (ListTools.isEmpty(queryView.getAvailableUnitList())
				&& ListTools.isEmpty(queryView.getAvailableIdentityList())
				&& ListTools.isEmpty(queryView.getAvailablePersonList())) {
			return true;
		}
		if (effectivePerson.isPerson(queryView.getCreatorPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (effectivePerson.isPerson(application.getControllerList())) {
			return true;
		}
		if (effectivePerson.isPerson(queryView.getControllerList())) {
			return true;
		}
		if (ListTools.isNotEmpty(queryView.getAvailablePersonList())) {
			if (ListTools.contains(queryView.getAvailableIdentityList(), effectivePerson.getDistinguishedName())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryView.getAvailableIdentityList())) {
			List<String> list = this.business().organization().identity()
					.listWithPerson(effectivePerson.getDistinguishedName());
			if (ListTools.containsAny(list, queryView.getAvailableIdentityList())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryView.getAvailableUnitList())) {
			List<String> list = this.business().organization().unit()
					.listWithPerson(effectivePerson.getDistinguishedName());
			if (ListTools.containsAny(list, queryView.getAvailableUnitList())) {
				return true;
			}
		}
		return false;
	}

}