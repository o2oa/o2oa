package com.x.pan.assemble.control.factory;

import com.x.base.core.project.tools.StringTools;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Folder2_;
import com.x.pan.assemble.control.AbstractFactory;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileStatusEnum;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Folder2Factory extends AbstractFactory {

	public Folder2Factory(Business business) throws Exception {
		super(business);
	}

	public List<String> listTopWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.person), person);
		p = cb.and(p, cb.equal(root.get(Folder2_.status), FileStatus.VALID.getName()));
		p = cb.and(p, cb.equal(root.get(Folder2_.superior), Business.TOP_FOLD));
		cq.select(root.get(Folder2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWithSuperior(String person, String superior, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.person), person);
		p = cb.and(p, cb.equal(root.get(Folder2_.superior), superior));
		if (StringUtils.isNotEmpty(status)) {
			p = cb.and(p, cb.equal(root.get(Folder2_.status), status));
		}
		cq.select(root.get(Folder2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listSubNested(String id, String status) throws Exception {
		ListOrderedSet<String> list = new ListOrderedSet<>();
		List<String> subs = this.listSubDirect(id, status);
		for (String str : subs) {
			if (!list.contains(str)) {
				list.add(str);
				list.addAll(this.listSubNested(str, status));
			}
		}
		return list.asList();
	}

	public List<String> listSubDirect(String id, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.superior), id);
		if (StringUtils.isNotEmpty(status)) {
			p = cb.and(p, cb.equal(root.get(Folder2_.status), status));
		}
		cq.select(root.get(Folder2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Folder2> listSubNested1(String id, String status) throws Exception {
		List<Folder2> list = new ArrayList<>();
		List<Folder2> subs = this.listSubDirect1(id, status);
		for (Folder2 folder : subs) {
			list.add(folder);
			list.addAll(this.listSubNested1(folder.getId(), status));
		}
		return list;
	}

	public List<Folder2> listSubDirect1(String id, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Folder2> cq = cb.createQuery(Folder2.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.superior), id);
		if (StringUtils.isNotEmpty(status)) {
			p = cb.and(p, cb.equal(root.get(Folder2_.status), status));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Long countSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.superior), id);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public boolean exist(String person, String name, String superior, String excludeId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.person), person);
		p = cb.and(p, cb.equal(root.get(Folder2_.name), name));
		p = cb.and(p, cb.equal(root.get(Folder2_.superior), StringUtils.trimToEmpty(superior)));
		p = cb.and(p, cb.equal(root.get(Folder2_.status), FileStatus.VALID.getName()));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Folder2_.id), excludeId));
		}
		cq.select(cb.count(root)).where(p);
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	public void listSuPNested(String id, List<Folder2> list) throws Exception {
		if(!Business.TOP_FOLD.equals(id)) {
			Folder2 folder = this.entityManagerContainer().find(id, Folder2.class);
			if (folder != null) {
				list.add(folder);
				if (!Business.TOP_FOLD.equals(folder.getSuperior())) {
					listSuPNested(folder.getSuperior(), list);
				}
			}
		}
	}

	public String getSupPath(String id) throws Exception {
		String path = "";
		List<Folder2> list = new ArrayList<>();
		listSuPNested(id, list);
		Collections.reverse(list);
		if(!list.isEmpty()) {
			for (Folder2 folder : list){
				path = path + "/" + folder.getName();
			}
		}else {
			path = "/";
		}
		return path;
	}

	public String adjustFileName(String superior, String fileName) throws Exception {
		List<String> list = new ArrayList<>();
		list.add(fileName);
		for (int i = 1; i < 10; i++) {
			list.add(fileName + i);
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.superior), superior);
		p = cb.and(p, root.get(Folder2_.name).in(list));
		p = cb.and(p, cb.equal(root.get(Folder2_.status), FileStatusEnum.VALID.getName()));
		cq.select(root.get(Folder2_.name)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}
}
