package com.x.cms.assemble.control.factory.element;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.element.QueryView_;

public class QueryViewFactory extends ElementFactory {

	public QueryViewFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public QueryView pick(String flag) throws Exception {
		return this.pick(flag, QueryView.class);
	}

	public Boolean allowRead(EffectivePerson effectivePerson, QueryView queryView) throws Exception {
		if (queryView == null) {
			throw new Exception("queryView is null!");
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
		if (effectivePerson.isPerson(queryView.getControllerList())) {
			return true;
		}
		if (ListTools.isNotEmpty(queryView.getAvailablePersonList())) {
			if (ListTools.contains(queryView.getAvailableIdentityList(), effectivePerson.getDistinguishedName())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryView.getAvailableIdentityList())) {
			List<String> identityNameList = this.business().organization().identity()
					.listWithPerson(effectivePerson.getDistinguishedName());
			if (ListTools.containsAny(identityNameList, queryView.getAvailableIdentityList())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryView.getAvailableUnitList())) {
			List<String> unitNames = this.business().organization().unit()
					.listWithPerson(effectivePerson.getDistinguishedName());
			if (ListTools.containsAny(unitNames, queryView.getAvailableUnitList())) {
				return true;
			}
		}
		return false;
	}

	public List<String> listWithAppId(String appId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(QueryView.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryView> root = cq.from(QueryView.class);
		Predicate p = cb.equal(root.get(QueryView_.appId), appId);
		cq.select(root.get(QueryView_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public boolean allowRead(EffectivePerson effectivePerson, QueryView queryView, AppInfo application) {
		return true;
	}

}