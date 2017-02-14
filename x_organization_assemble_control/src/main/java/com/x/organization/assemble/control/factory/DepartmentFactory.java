package com.x.organization.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Department_;

public class DepartmentFactory extends AbstractFactory {

	public DepartmentFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定部门的全部下属部门.包括嵌套的部门,仅返回Id.")
	public List<String> listSubNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<>();
		this.subNested(id, set);
		return set.asList();
	}

	@MethodDescribe("递归循环调用查找,仅返回Id.")
	private void subNested(String id, ListOrderedSet<String> set) throws Exception {
		List<String> list = new ArrayList<>();
		for (String o : this.listSubDirect(id)) {
			if (!set.contains(o)) {
				list.add(o);
			}
		}
		if (!list.isEmpty()) {
			set.addAll(list);
			for (String o : list) {
				this.subNested(o, set);
			}
		}
	}

	@MethodDescribe("查找指定公司的所有下级部门。")
	public List<String> listSubNestedWithCompany(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.company), id);
		cq.select(root.get(Department_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("获取指定公司的直接下属部门(Level=1).")
	public List<String> listTopWithCompany(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.company), id);
		p = cb.and(p, cb.equal(root.get(Department_.level), 1));
		cq.select(root.get(Department_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("计算部门直接下级部门数量.")
	public Long countSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.superior), id);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	@MethodDescribe("计算公司顶层部门数量.")
	public Long countTopWithCompany(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.superior), "");
		p = cb.and(p, cb.equal(root.get(Department_.company), id));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	@MethodDescribe("获取指定部门的直接下级部门,仅返回Id.")
	public List<String> listSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.superior), id);
		cq.select(root.get(Department_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("调整部门部门层级以及下属部门层级,以及所有下属部门所属公司")
	public void adjust(Department department) throws Exception {
		int level = 1;
		List<String> prevents = new ArrayList<String>();
		prevents.add(department.getId());
		if (StringUtils.isNotEmpty(department.getSuperior())) {
			level = this.entityManagerContainer()
					.find(department.getSuperior(), Department.class, ExceptionWhen.not_found).getLevel() + 1;
			prevents.add(department.getSuperior());
		}
		department.setLevel(level);
		List<Department> loop = new ArrayList<Department>();
		loop.add(department);
		while (!loop.isEmpty()) {
			List<Department> list = new ArrayList<Department>();
			for (Department o : loop) {
				for (String id : this.listSubDirect(o.getId())) {
					if (prevents.contains(id)) {
						throw new Exception("superior of department looped{id:" + id + "}.");
					}
					prevents.add(id);
					Department d = this.entityManagerContainer().find(id, Department.class);
					if (null != d) {
						list.add(d);
					}
				}
			}
			level = level + 1;
			for (Department o : list) {
				o.setLevel(level);
				o.setCompany(department.getCompany());
			}
			loop = list;
		}
	}

	@MethodDescribe("列示所有首字母开始的公司.")
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.like(root.get(Department_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Department_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("进行模糊查询.")
	public List<String> listLike(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		//str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		String q = "%" + str + "%";
		Predicate p = cb.like(root.get(Department_.name), q, '\\');
		p = cb.or(p, cb.like(root.get(Department_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Department_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Department_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	@MethodDescribe("根据拼音进行模糊查询.")
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.like(root.get(Department_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Department_.pinyinInitial), str + "%"));
		cq.select(root.get(Department_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

}