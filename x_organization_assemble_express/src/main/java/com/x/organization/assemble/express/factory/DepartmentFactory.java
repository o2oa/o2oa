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

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutDepartment;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentAttribute;
import com.x.organization.core.entity.DepartmentAttribute_;
import com.x.organization.core.entity.Department_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;

public class DepartmentFactory extends AbstractFactory {

	public DepartmentFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获得所有部门")
	public List<String> list() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		cq.select(root.get(Department_.id));
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	/* 根据名称查找Department */
	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.name), name);
		cq.select(root.get(Department_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@MethodDescribe("根据Identity查找Department")
	public String getWithIdentity(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.id), id);
		cq.select(root.get(Identity_.department)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@MethodDescribe("查找部门的直接上级部门.")
	public String getSupDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.id), id);
		cq.select(root.get(Department_.superior)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	@MethodDescribe("查找部门的嵌套上级部门.")
	public List<String> listSupNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<String>();
		this.supNested(id, set);
		return set.asList();
	}

	@MethodDescribe("递归查找部门的嵌套上级部门.")
	private void supNested(String id, ListOrderedSet<String> set) throws Exception {
		String str = this.getSupDirect(id);
		if ((str != null) && (!set.contains(str))) {
			set.add(str);
			this.supNested(str, set);
		}
	}

	@MethodDescribe("获取指定部门的全部下属公司.包括嵌套的子部门.")
	public List<String> listSubNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<String>();
		this.subNested(id, set);
		return set.asList();
	}

	@MethodDescribe("查找部门直接下级部门.")
	public List<String> listSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.superior), id);
		cq.select(root.get(Department_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("递归循环调用查找。")
	private void subNested(String id, ListOrderedSet<String> set) throws Exception {
		List<String> list = new ArrayList<>();
		for (String str : this.listSubDirect(id)) {
			if (!set.contains(str)) {
				list.add(str);
			}
		}
		if (!list.isEmpty()) {
			set.addAll(list);
			for (String str : list) {
				this.subNested(str, set);
			}
		}
	}

	@MethodDescribe("按公司查找顶级部门.")
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

	@MethodDescribe("查找指定公司的所有下级部门。")
	public List<String> listWithCompanySubNested(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.company), id);
		cq.select(root.get(Department_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("列示所有首字母开始.")
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
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.like(root.get(Department_.name), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Department_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Department_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Department_.id)).where(p);
		return em.createQuery(cq).getResultList();
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

	@MethodDescribe("查询属性值包含value的所有DepartmentAttribute")
	public List<String> listWithDepartmentAttribute(String name, String attribute) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DepartmentAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<DepartmentAttribute> root = cq.from(DepartmentAttribute.class);
		Predicate p = cb.equal(root.get(DepartmentAttribute_.name), name);
		p = cb.and(p, cb.isMember(attribute, root.get(DepartmentAttribute_.attributeList)));
		cq.select(root.get(DepartmentAttribute_.department)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 内容转换 */
	public WrapOutDepartment wrap(Department o) throws Exception {
		WrapOutDepartment wrap = new WrapOutDepartment();
		o.copyTo(wrap);
		if (StringUtils.isNotEmpty(wrap.getSuperior())) {
			Department superior = this.entityManagerContainer().fetchAttribute(o.getSuperior(), Department.class,
					"name");
			if (null != superior) {
				wrap.setSuperior(superior.getName());
			}
		}
		if (StringUtils.isNotEmpty(wrap.getCompany())) {
			Company company = this.entityManagerContainer().fetchAttribute(o.getCompany(), Company.class, "name");
			if (null != company) {
				wrap.setCompany(company.getName());
			}
		}
		return wrap;
	}

	/* 内容转换 */
	public List<WrapOutDepartment> wrap(List<Department> list) throws Exception {
		List<WrapOutDepartment> wrapList = new ArrayList<>();
		for (Department o : ListTools.nullToEmpty(list)) {
			wrapList.add(this.wrap(o));
		}
		return wrapList;
	}

	/* 进行排序 */
	public void sort(List<WrapOutDepartment> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutDepartment>() {
			public int compare(WrapOutDepartment o1, WrapOutDepartment o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}
}