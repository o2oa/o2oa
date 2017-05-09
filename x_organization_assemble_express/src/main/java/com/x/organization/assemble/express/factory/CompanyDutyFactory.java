package com.x.organization.assemble.express.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyDuty;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.CompanyDuty_;
import com.x.organization.core.entity.Identity;

public class CompanyDutyFactory extends AbstractFactory {

	public CompanyDutyFactory(Business business) throws Exception {
		super(business);
	}

	/* 根据名称查找CompanyDuty */
	public String getWithName(String name, String companyId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyDuty> root = cq.from(CompanyDuty.class);
		Predicate p = cb.equal(root.get(CompanyDuty_.company), companyId);
		p = cb.and(p, cb.equal(root.get(CompanyDuty_.name), name));
		cq.select(root.get(CompanyDuty_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/* 根据名称查找CompanyDuty */
	public List<String> listWithCompany(String companyId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyDuty> root = cq.from(CompanyDuty.class);
		Predicate p = cb.equal(root.get(CompanyDuty_.company), companyId);
		cq.select(root.get(CompanyDuty_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	/* 根据属性名查找所有公司职务 */
	public List<String> listWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyDuty> root = cq.from(CompanyDuty.class);
		Predicate p = cb.equal(root.get(CompanyDuty_.name), name);
		cq.select(root.get(CompanyDuty_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	/* 根据属性名查找所有公司职务 */
	public List<String> listWithIdentity(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyDuty> root = cq.from(CompanyDuty.class);
		Predicate p = cb.isMember(name, root.get(CompanyDuty_.identityList));
		cq.select(root.get(CompanyDuty_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	/* 转换内容 */
	public WrapOutCompanyDuty wrap(CompanyDuty o) throws Exception {
		WrapOutCompanyDuty wrap = new WrapOutCompanyDuty();
		o.copyTo(wrap);
		if (StringUtils.isNotEmpty(o.getCompany())) {
			Company company = this.entityManagerContainer().fetchAttribute(o.getCompany(), Company.class, "name");
			if (null != company) {
				wrap.setCompany(company.getName());
			}
		}
		List<String> list = new ArrayList<>();
		if (null != o.getIdentityList() && (!o.getIdentityList().isEmpty())) {
			for (Identity identity : this.entityManagerContainer().fetchAttribute(o.getIdentityList(), Identity.class,
					"name")) {
				list.add(identity.getName());
			}
		}
		Collections.sort(list, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return ObjectUtils.compare(o1, o2, true);
			}
		});
		wrap.setIdentityList(list);
		return wrap;
	}

}