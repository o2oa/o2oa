package com.x.file.assemble.control.factory;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.file.assemble.control.AbstractFactory;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.File_;
import com.x.file.core.entity.open.ReferenceType;

public class FileFactory extends AbstractFactory {

	public FileFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithReferenceTypeWithReference(ReferenceType referenceType, String reference)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.reference), reference);
		p = cb.and(p, cb.equal(root.get(File_.referenceType), referenceType));
		cq.select(root.get(File_.id)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}
}