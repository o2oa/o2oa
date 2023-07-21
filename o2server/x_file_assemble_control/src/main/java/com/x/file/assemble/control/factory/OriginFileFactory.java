package com.x.file.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.file.assemble.control.AbstractFactory;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.open.OriginFile_;

public class OriginFileFactory extends AbstractFactory {

	public OriginFileFactory(Business business) throws Exception {
		super(business);
	}

	public OriginFile getByMd5(String fileMd5)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(OriginFile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OriginFile> cq = cb.createQuery(OriginFile.class);
		Root<OriginFile> root = cq.from(OriginFile.class);
		Predicate p = cb.equal(root.get(OriginFile_.fileMd5), fileMd5);
		List<OriginFile> originFileList = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if(originFileList!=null && !originFileList.isEmpty()){
			return originFileList.get(0);
		}
		return null;
	}


}