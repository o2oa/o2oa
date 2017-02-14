package com.x.organization.assemble.express.factory;

import java.util.Collection;
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
import com.x.organization.assemble.express.jaxrs.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;

public class IdentityFactory extends AbstractFactory {

	public IdentityFactory(Business business) throws Exception {
		super(business);
	}

	/* 根据名称查找Identity */
	public String getWithName(String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.name), name);
		cq.select(root.get(Identity_.id)).where(p);
		List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (!list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/* 多值根据名称查找Identity */
	public List<String> listWithName(List<String> names) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = root.get(Identity_.name).in(names);
		cq.select(root.get(Identity_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

	/* 根据给定的Person Id 获取其所有的 Identity */
	public List<String> listWithPerson(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), id);
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 根据给定的Department Id 获取其所有的 Identity */
	public List<String> listWithDepartment(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.department), id);
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 根据给定的Department Id 获取其所有的 Identity */
	public List<String> listWithDepartment(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = root.get(Identity_.department).in(ids);
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 列示所有首字母开始的公司. */
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.like(root.get(Identity_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 进行模糊查询. */
	public List<String> listLike(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.like(root.get(Identity_.name), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Identity_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Identity_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 根据拼音进行模糊查询. */
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.like(root.get(Identity_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Identity_.pinyinInitial), str + "%"));
		cq.select(root.get(Identity_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	/* 计算部门直接成员Identity数量. */
	public Long countSubDirectWithDepartment(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.department), id);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/* 在指定的部门范围内查找人员. */
	public List<String> listLikeWithDepartment(Collection<String> departments, String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate pa = cb.conjunction();
		for (String o : departments) {
			pa = cb.or(pa, cb.equal(root.get(Identity_.department), o));
		}
		Predicate pb = cb.like(root.get(Identity_.name), "%" + str + "%", '\\');
		pb = cb.or(pb, cb.like(root.get(Identity_.pinyin), str + "%", '\\'));
		pb = cb.or(pb, cb.like(root.get(Identity_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Identity_.id)).where(cb.and(pa, pb));
		return em.createQuery(cq).getResultList();
	}

	public WrapOutIdentity wrap(Identity o) throws Exception {
		WrapOutIdentity wrap = new WrapOutIdentity();
		o.copyTo(wrap);
		if (StringUtils.isNotEmpty(wrap.getDepartment())) {
			Department department = this.entityManagerContainer().fetchAttribute(o.getDepartment(), Department.class,
					"name");
			if (null != department) {
				wrap.setDepartment(department.getName());
			}
		}
		if (StringUtils.isNotEmpty(wrap.getPerson())) {
			Person person = this.entityManagerContainer().fetchAttribute(o.getDepartment(), Person.class, "name");
			if (null != person) {
				wrap.setPerson(person.getName());
			}
		}
		return wrap;
	}

	public void sort(List<WrapOutIdentity> wraps) throws Exception {
		Collections.sort(wraps, new Comparator<WrapOutIdentity>() {
			public int compare(WrapOutIdentity o1, WrapOutIdentity o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}
}