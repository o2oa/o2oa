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

import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutCompany;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute_;
import com.x.organization.core.entity.Company_;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Department_;

public class CompanyFactory extends AbstractFactory {

	public CompanyFactory(Business business) throws Exception {
		super(business);
	}

	/* 根据名称查找Company */
	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.name), name);
		cq.select(root.get(Company_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/* 根据Department查找Company */
	public String getWithDepartment(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Department> root = cq.from(Department.class);
		Predicate p = cb.equal(root.get(Department_.id), id);
		cq.select(root.get(Department_.company)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/* 计算公司直接下级公司数量. */
	public Long countSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.superior), id);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/* 列示顶层公司 */
	public List<String> listTop() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.level), 1);
		cq.select(root.get(Company_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 列示所有公司 */
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		cq.select(root.get(Company_.id));
		return em.createQuery(cq).getResultList();
	}

	//
	/* 查找公司的直接上级公司. */
	public String getSupDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.id), id);
		cq.select(root.get(Company_.superior)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/* 查找公司的嵌套上级公司. */
	public List<String> listSupNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<>();
		this.supNested(id, set);
		return set.asList();
	}

	/* 递归查找公司的嵌套上级公司. */
	private void supNested(String id, ListOrderedSet<String> set) throws Exception {
		String str = this.getSupDirect(id);
		if ((str != null) && (!set.contains(str))) {
			set.add(str);
			this.supNested(str, set);
		}
	}

	/* 获取指定公司的全部下属公司.包括嵌套的子公司. */
	public List<String> listSubNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<>();
		this.subNested(id, set);
		return set.asList();
	}

	/* 查找公司直接下级公司 */
	public List<String> listSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.superior), id);
		cq.select(root.get(Company_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 递归循环调用查找 */
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

	/* 查询属性值包含value的所有CompanyAttribute */
	public List<String> listWithCompanyAttribute(String name, String attribute) throws Exception {
		EntityManager em = this.entityManagerContainer().get(CompanyAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<CompanyAttribute> root = cq.from(CompanyAttribute.class);
		Predicate p = cb.equal(root.get(CompanyAttribute_.name), name);
		p = cb.and(p, cb.isMember(attribute, root.get(CompanyAttribute_.attributeList)));
		cq.select(root.get(CompanyAttribute_.company)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 列示所有首字母开始的公司. */
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.like(root.get(Company_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Company_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 进行模糊查询. */
	public List<String> listLike(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.like(root.get(Company_.name), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Company_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Company_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Company_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 根据拼音进行模糊查询. */
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.like(root.get(Company_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Company_.pinyinInitial), str + "%"));
		cq.orderBy(cb.asc(root.get(Company_.pinyinInitial)), cb.asc(root.get(Company_.pinyin)));
		cq.select(root.get(Company_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 转换id->name */
	public WrapOutCompany wrap(Company o) throws Exception {
		WrapOutCompany wrap = new WrapOutCompany();
		o.copyTo(wrap);
		if (StringUtils.isNotEmpty(wrap.getSuperior())) {
			Company superior = this.entityManagerContainer().fetchAttribute(wrap.getSuperior(), Company.class, "name");
			if (null != superior) {
				wrap.setSuperior(superior.getName());
			}
		}
		return wrap;
	}

	/* 对WrapOutCompany进行排序 */
	public void sort(List<WrapOutCompany> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutCompany>() {
			public int compare(WrapOutCompany o1, WrapOutCompany o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}
}