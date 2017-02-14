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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentDuty;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty_;
import com.x.organization.core.entity.Identity;

public class DepartmentDutyFactory extends AbstractFactory {

	public DepartmentDutyFactory(Business business) throws Exception {
		super(business);
	}

	/* 根据名称查找DepartmentDuty */
	public String getWithName(String name, String departmentId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
		Predicate p = cb.equal(root.get(DepartmentDuty_.department), departmentId);
		p = cb.and(p, cb.equal(root.get(DepartmentDuty_.name), name));
		cq.select(root.get(DepartmentDuty_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/* 查找指定部门的所有Duty */
	public List<String> listWithDepartment(String departmentId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
		Predicate p = cb.equal(root.get(DepartmentDuty_.department), departmentId);
		cq.select(root.get(DepartmentDuty_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 根据属性名查找所有部门职务 */
	public List<String> listWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
		Predicate p = cb.equal(root.get(DepartmentDuty_.name), name);
		cq.select(root.get(DepartmentDuty_.id)).where(p);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/* 查找Identity所有的部门职务 */
	public List<String> listWithIdentity(String identityId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
		Predicate p = cb.isMember(identityId, root.get(DepartmentDuty_.identityList));
		cq.select(root.get(DepartmentDuty_.id)).where(p);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/* 转换字段 */
	public WrapOutDepartmentDuty wrap(DepartmentDuty o) throws Exception {
		WrapOutDepartmentDuty wrap = new WrapOutDepartmentDuty();
		o.copyTo(wrap);
		if (StringUtils.isNotEmpty(o.getDepartment())) {
			Department department = this.entityManagerContainer().fetchAttribute(o.getDepartment(), Department.class,
					"name");
			if (null != department) {
				wrap.setDepartment(department.getName());
			}
		}
		List<String> list = new ArrayList<>();
		for (Identity identity : this.entityManagerContainer().fetchAttribute(o.getIdentityList(), Identity.class,
				"name")) {
			list.add(identity.getName());
		}
		Collections.sort(list, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return ObjectUtils.compare(o1, o2, true);
			}
		});
		wrap.setIdentityList(list);
		return wrap;
	}

	/* 进行排序 */
	public void sort(List<WrapOutDepartmentDuty> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutDepartmentDuty>() {
			public int compare(WrapOutDepartmentDuty o1, WrapOutDepartmentDuty o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}

}