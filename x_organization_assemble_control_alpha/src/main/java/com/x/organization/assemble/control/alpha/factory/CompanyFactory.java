package com.x.organization.assemble.control.alpha.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.organization.assemble.control.alpha.AbstractFactory;
import com.x.organization.assemble.control.alpha.Business;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.Company_;

public class CompanyFactory extends AbstractFactory {

	public CompanyFactory(Business business) throws Exception {
		super(business);
	}

	public Company pick(String flag) throws Exception {
		return this.pick(flag, Company.class, Company.FLAGS);
	}

	public List<Company> batchPick(String... flags) throws Exception {
		List<Company> list = new ArrayList<>();
		Arrays.asList(flags).stream().forEach(s -> {
			try {
				Company o = this.pick(s);
				if (null != o) {
					list.add(o);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return list;
	}

	@MethodDescribe("获取所有顶层公司.")
	public List<String> listTop() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.level), 1);
		cq.select(root.get(Company_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	@MethodDescribe("计算公司直接下级公司数量.")
	public Long countSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.superior), id);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	@MethodDescribe("查找公司直接下级公司,仅返回ID.")
	public List<String> listSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.superior), id);
		cq.select(root.get(Company_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Company> listSubDirectObject(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Company> cq = cb.createQuery(Company.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.equal(root.get(Company_.superior), id);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Long countSubNested(String id) throws Exception {
		return this.listSubNested(id).stream().count();
	}

	@MethodDescribe("获取指定公司的全部下属公司.包括嵌套的子公司.仅返回ID")
	public List<String> listSubNested(String id) throws Exception {
		ListOrderedSet<String> set = new ListOrderedSet<>();
		this.subNested(id, set);
		return set.asList();
	}

	@MethodDescribe("递归循环调用查找,仅返回ID.")
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

	public List<Company> listSubNestedObject(String id) throws Exception {
		List<String> ids = this.listSubNested(id);
		return this.entityManagerContainer().list(Company.class, ids);
	}

	@MethodDescribe("查找公司的直接上级公司.")
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

	@MethodDescribe("查找公司的嵌套上级公司.")
	public List<String> listSupNested(String id) throws Exception {
		List<String> list = SetUniqueList.setUniqueList(new ArrayList<String>());
		this.supNested(id, list);
		return list;
	}

	@MethodDescribe("递归查找公司的嵌套上级公司.")
	private void supNested(String id, List<String> list) throws Exception {
		String superior = this.getSupDirect(id);
		if ((StringUtils.isNotEmpty(superior)) && (!list.contains(superior))) {
			list.add(superior);
			this.supNested(superior, list);
		}
	}

	@MethodDescribe("调整公司的层级,同时调整其下属公司的层级.")
	public void adjustLevel(Company company) throws Exception {
		int level = 1;
		List<String> prevents = new ArrayList<String>();
		prevents.add(company.getId());
		if (StringUtils.isNotEmpty(company.getSuperior())) {
			level = this.entityManagerContainer().find(company.getSuperior(), Company.class, ExceptionWhen.not_found)
					.getLevel() + 1;
			prevents.add(company.getSuperior());
		}
		company.setLevel(level);
		List<Company> loop = new ArrayList<Company>();
		loop.add(company);
		while (!loop.isEmpty()) {
			List<Company> list = new ArrayList<Company>();
			for (Company o : loop) {
				for (String id : this.listSubDirect(o.getId())) {
					if (prevents.contains(id)) {
						throw new Exception("superior of company looped{id:" + id + "}.");
					}
					prevents.add(id);
					list.add(this.entityManagerContainer().find(id, Company.class, ExceptionWhen.none));
				}
			}
			level = level + 1;
			for (Company o : list) {
				o.setLevel(level);
			}
			loop = list;
		}
	}

	public List<String> listWithControl(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		Predicate p = cb.isMember(id, root.get(Company_.controllerList));
		cq.select(root.get(Company_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Company> root = cq.from(Company.class);
		cq.select(root.get(Company_.id));
		return em.createQuery(cq).getResultList();
	}
}