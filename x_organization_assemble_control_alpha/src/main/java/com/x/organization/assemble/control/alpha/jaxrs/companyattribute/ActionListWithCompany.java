package com.x.organization.assemble.control.alpha.jaxrs.companyattribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute_;

class ActionListWithCompany extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String companyFlag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Company company = business.company().pick(companyFlag);
			if (null == company) {
				throw new ExceptionCompanyNotExist(companyFlag);
			}
			List<CompanyAttribute> os = this.listWithCompany(business, company);
			List<Wo> wos = Wo.copier.copy(os);
			wos.stream().sorted(Comparator.comparing(Wo::getName));
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends CompanyAttribute {

		private static final long serialVersionUID = -127291000673692614L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static WrapCopier<CompanyAttribute, Wo> copier = WrapCopierFactory.wi(CompanyAttribute.class, Wo.class, null,
				Excludes);

	}

	private List<CompanyAttribute> listWithCompany(Business business, Company company) throws Exception {
		EntityManager em = business.entityManagerContainer().get(CompanyAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CompanyAttribute> cq = cb.createQuery(CompanyAttribute.class);
		Root<CompanyAttribute> root = cq.from(CompanyAttribute.class);
		Predicate p = cb.equal(root.get(CompanyAttribute_.company), company.getId());
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

}