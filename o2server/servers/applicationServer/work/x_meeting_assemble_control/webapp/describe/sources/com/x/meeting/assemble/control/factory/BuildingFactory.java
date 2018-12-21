package com.x.meeting.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.meeting.assemble.control.AbstractFactory;
import com.x.meeting.assemble.control.Business;
import com.x.meeting.core.entity.Building;
import com.x.meeting.core.entity.Building_;

public class BuildingFactory extends AbstractFactory {

	public BuildingFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> list() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Building.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Building> root = cq.from(Building.class);
		cq.select(root.get(Building_.id));
		return em.createQuery(cq).getResultList();
	}

	//@MethodDescribe("列示所有首字母开始的Building.")
	public List<String> listPinyinInitial(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Building.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Building> root = cq.from(Building.class);
		Predicate p = cb.like(root.get(Building_.pinyinInitial), str + "%", '\\');
		cq.select(root.get(Building_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	//@MethodDescribe("进行模糊查询.")
	public List<String> listLike(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Building.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Building> root = cq.from(Building.class);
		Predicate p = cb.like(root.get(Building_.name), "%" + str + "%", '\\');
		p = cb.or(p, cb.like(root.get(Building_.pinyin), str + "%", '\\'));
		p = cb.or(p, cb.like(root.get(Building_.pinyinInitial), str + "%", '\\'));
		cq.select(root.get(Building_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

	//@MethodDescribe("根据拼音进行模糊查询.")
	public List<String> listLikePinyin(String key) throws Exception {
		String str = key.replaceAll("_", "\\\\_");
		str = str.replaceAll("%", "\\\\%");
		str = str.toLowerCase();
		EntityManager em = this.entityManagerContainer().get(Building.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Building> root = cq.from(Building.class);
		Predicate p = cb.like(root.get(Building_.pinyin), str + "%");
		p = cb.or(p, cb.like(root.get(Building_.pinyinInitial), str + "%"));
		cq.select(root.get(Building_.id)).where(p);
		return em.createQuery(cq).setMaxResults(200).getResultList();
	}

}