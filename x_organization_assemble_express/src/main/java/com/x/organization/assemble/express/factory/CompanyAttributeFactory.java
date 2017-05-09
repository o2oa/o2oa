package com.x.organization.assemble.express.factory;

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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompanyAttribute;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute_;

public class CompanyAttributeFactory extends AbstractFactory {

	public CompanyAttributeFactory(Business business) throws Exception {
		super(business);
	}

	/* 根据CompanyAttribute和Company Name查找CompanyAttribute */
	public String getWithName(String name, String companyId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyAttribute> root = cq.from(CompanyAttribute.class);
		Predicate p = cb.equal(root.get(CompanyAttribute_.company), companyId);
		p = cb.and(p, cb.equal(root.get(CompanyAttribute_.name), name));
		cq.select(root.get(CompanyAttribute_.id)).where(p).distinct(true);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	/* 查找公司所有的CompanyAttribute */
	public List<String> listWithCompany(String companyId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyAttribute> root = cq.from(CompanyAttribute.class);
		Predicate p = cb.equal(root.get(CompanyAttribute_.company), companyId);
		cq.select(root.get(CompanyAttribute_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	/* 按名称查找 */
	public List<String> listWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyAttribute> root = cq.from(CompanyAttribute.class);
		Predicate p = cb.equal(root.get(CompanyAttribute_.name), name);
		cq.select(root.get(CompanyAttribute_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	/* 转换内容 */
	public WrapOutCompanyAttribute wrap(CompanyAttribute o) throws Exception {
		WrapOutCompanyAttribute wrap = new WrapOutCompanyAttribute();
		o.copyTo(wrap);
		if (StringUtils.isNotEmpty(wrap.getCompany())) {
			Company company = this.entityManagerContainer().fetchAttribute(o.getCompany(), Company.class, "name");
			if (null != company) {
				wrap.setCompany(company.getName());
			}
		}
		return wrap;
	}

	/* 进行排序 */
	public void sort(List<WrapOutCompanyAttribute> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutCompanyAttribute>() {
			public int compare(WrapOutCompanyAttribute o1, WrapOutCompanyAttribute o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}

}