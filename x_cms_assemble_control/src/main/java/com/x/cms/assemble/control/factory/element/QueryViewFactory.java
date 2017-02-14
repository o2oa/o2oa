package com.x.cms.assemble.control.factory.element;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.QueryView;
import com.x.cms.core.entity.element.QueryView_;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapIdentity;


public class QueryViewFactory extends ElementFactory {

	public QueryViewFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public QueryView pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public QueryView pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, QueryView.class, exceptionWhen, QueryView.FLAGS);
	}

	public Boolean allowRead(EffectivePerson effectivePerson, QueryView queryView) throws Exception {
		/* 全部为空，没有设置范围 */
		if (ListTools.isEmpty(queryView.getAvailableCompanyList())
				&& ListTools.isEmpty(queryView.getAvailableDepartmentList())
				&& ListTools.isEmpty(queryView.getAvailableIdentityList())
				&& ListTools.isEmpty(queryView.getAvailablePersonList())) {
			return true;
		}
		if (effectivePerson.isUser(queryView.getCreatorPerson())) {
			return true;
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (effectivePerson.isUser(queryView.getControllerList())) {
			return true;
		}
		if (ListTools.isNotEmpty(queryView.getAvailablePersonList())) {
			if (ListTools.contains(queryView.getAvailableIdentityList(), effectivePerson.getName())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryView.getAvailableIdentityList())) {
			List<WrapIdentity> list = this.business().organization().identity()
					.listWithPerson(effectivePerson.getName());
			if (ListTools.containsAny(ListTools.extractProperty(list, "name", String.class, true, true),
					queryView.getAvailableIdentityList())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryView.getAvailableDepartmentList())) {
			List<WrapDepartment> list = this.business().organization().department()
					.listWithPerson(effectivePerson.getName());
			if (ListTools.containsAny(ListTools.extractProperty(list, "name", String.class, true, true),
					queryView.getAvailableDepartmentList())) {
				return true;
			}
		}
		if (ListTools.isNotEmpty(queryView.getAvailableCompanyList())) {
			List<WrapCompany> list = this.business().organization().company().listWithPerson(effectivePerson.getName());
			if (ListTools.containsAny(ListTools.extractProperty(list, "name", String.class, true, true),
					queryView.getAvailableCompanyList())) {
				return true;
			}
		}
		return false;
	}

	public List<String> listWithAppId(String appId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( QueryView.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<QueryView> root = cq.from(QueryView.class);
		Predicate p = cb.equal(root.get( QueryView_.appId ), appId);
		cq.select(root.get( QueryView_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}