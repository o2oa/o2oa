package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.DocumentPictureInfo;
import com.x.cms.core.entity.DocumentPictureInfo_;

public class DocumentPictureInfoFactory extends AbstractFactory {

	public DocumentPictureInfoFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的DocumentPictureInfo信息对象")
	public DocumentPictureInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, DocumentPictureInfo.class );
	}
	
	@MethodDescribe("获取指定Id的DocumentPictureInfo信息对象")
	public List<DocumentPictureInfo> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentPictureInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentPictureInfo> cq = cb.createQuery( DocumentPictureInfo.class );
		Root<DocumentPictureInfo> root = cq.from( DocumentPictureInfo.class );
		Predicate p = root.get( DocumentPictureInfo_.documentId ).in( ids );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@MethodDescribe("获取指定Id的DocumentPictureInfo信息对象")
	public List<String> listByDocId( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		EntityManager em = this.entityManagerContainer().get( DocumentPictureInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentPictureInfo> root = cq.from( DocumentPictureInfo.class );
		Predicate p = cb.equal( root.get( DocumentPictureInfo_.documentId ), id );
		cq.select( root.get( DocumentPictureInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}	
}