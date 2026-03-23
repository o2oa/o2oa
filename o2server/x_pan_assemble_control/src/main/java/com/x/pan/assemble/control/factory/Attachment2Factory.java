package com.x.pan.assemble.control.factory;

import com.x.base.core.project.tools.StringTools;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Attachment2_;
import com.x.pan.assemble.control.AbstractFactory;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Attachment3_;
import com.x.pan.core.entity.FileStatusEnum;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class Attachment2Factory extends AbstractFactory {

	public Attachment2Factory(Business business) throws Exception {
		super(business);
	}

	public List<String> listTopWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.person), person);
		p = cb.and(p, cb.equal(root.get(Attachment2_.status), FileStatus.VALID.getName()));
		p = cb.and(p, cb.equal(root.get(Attachment2_.folder), Business.TOP_FOLD));
		cq.select(root.get(Attachment2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithFolder(String folder, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.folder), folder);
		if (StringUtils.isNotEmpty(status)) {
			p = cb.and(p, cb.equal(root.get(Attachment2_.status), status));
		}
		cq.select(root.get(Attachment2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithName(String person, String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.person), person);
		p = cb.and(p, cb.equal(root.get(Attachment2_.status), FileStatus.VALID.getName()));
		p = cb.and(p, cb.like(root.get(Attachment2_.name), "%" + name + "%"));
		cq.select(root.get(Attachment2_.id)).where(p);
		return em.createQuery(cq).setMaxResults(100).getResultList();
	}

	public long getUseCapacity(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.person), person);
		p = cb.and(p, cb.equal(root.get(Attachment2_.status), FileStatus.VALID.getName()));
		cq.select(cb.sum(root.get(Attachment2_.length))).where(p);
		Long sum = em.createQuery(cq).getSingleResult();
		return sum == null ? 0 : sum;
	}

	public List<Attachment2> listWithFolder2(String folder, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Attachment2> cq = cb.createQuery(Attachment2.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.folder), folder);
		if (StringUtils.isNotEmpty(status)) {
			p = cb.and(p, cb.equal(root.get(Attachment2_.status), status));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public String adjustFileName(String folderId, String fileName) throws Exception {
		List<String> list = new ArrayList<>();
		list.add(fileName);
		String base = FilenameUtils.getBaseName(fileName);
		String extension = FilenameUtils.getExtension(fileName);
		for (int i = 1; i < 10; i++) {
			list.add(base + i + (StringUtils.isEmpty(extension) ? "" : "." + extension));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = root.get(Attachment2_.name).in(list);
		p = cb.and(p, cb.equal(root.get(Attachment2_.folder), folderId));
		p = cb.and(p, cb.equal(root.get(Attachment2_.status), FileStatusEnum.VALID.getName()));
		cq.select(root.get(Attachment2_.name)).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

}
