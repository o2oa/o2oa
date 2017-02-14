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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartmentAttribute;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentAttribute;
import com.x.organization.core.entity.DepartmentAttribute_;

public class DepartmentAttributeFactory extends AbstractFactory {

	public DepartmentAttributeFactory(Business business) throws Exception {
		super(business);
	}

	/* 根据名称查找DepartmentAttribute */
	public String getWithName(String name, String departmentId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentAttribute> root = cq.from(DepartmentAttribute.class);
		Predicate p = cb.equal(root.get(DepartmentAttribute_.department), departmentId);
		p = cb.and(p, cb.equal(root.get(DepartmentAttribute_.name), name));
		cq.select(root.get(DepartmentAttribute_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/* 查找指定部门的属性 */
	public List<String> listWithDepartment(String departmentId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentAttribute> root = cq.from(DepartmentAttribute.class);
		Predicate p = cb.equal(root.get(DepartmentAttribute_.department), departmentId);
		cq.select(root.get(DepartmentAttribute_.id)).where(p);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/* 按名称查找 */
	public List<String> listWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentAttribute> root = cq.from(DepartmentAttribute.class);
		Predicate p = cb.equal(root.get(DepartmentAttribute_.name), name);
		cq.select(root.get(DepartmentAttribute_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 转换字段 */
	public WrapOutDepartmentAttribute wrap(DepartmentAttribute o) throws Exception {
		WrapOutDepartmentAttribute wrap = new WrapOutDepartmentAttribute();
		o.copyTo(wrap);
		if (StringUtils.isNotEmpty(wrap.getDepartment())) {
			Department department = this.entityManagerContainer().fetchAttribute(wrap.getDepartment(), Department.class, "name");
			if (null != department) {
				wrap.setDepartment(department.getName());
			}
		}
		return wrap;
	}

	/* 进行排序 */
	public void sort(List<WrapOutDepartmentAttribute> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutDepartmentAttribute>() {
			public int compare(WrapOutDepartmentAttribute o1, WrapOutDepartmentAttribute o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}

}