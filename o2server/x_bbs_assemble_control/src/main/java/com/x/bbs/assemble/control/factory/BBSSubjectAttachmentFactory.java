package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectAttachment_;

/**
 * 类   名：BBSSubjectAttachmentFactory<br/>
 * 实体类：BBSSubjectAttachment<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-08-10 17:17:26
**/
public class BBSSubjectAttachmentFactory extends AbstractFactory {

	public BBSSubjectAttachmentFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSSubjectAttachment实体信息对象" )
	public BBSSubjectAttachment get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSSubjectAttachment.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "列示指定Id的BBSSubjectAttachment实体信息列表" )
	public List<BBSSubjectAttachment> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSSubjectAttachment>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectAttachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectAttachment> cq = cb.createQuery(BBSSubjectAttachment.class);
		Root<BBSSubjectAttachment> root = cq.from(BBSSubjectAttachment.class);
		Predicate p = root.get(BBSSubjectAttachment_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "根据主题ID列示BBSSubjectAttachment实体信息列表" )
	public List<BBSSubjectAttachment> listBySubjectId( String subjectId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception("subject id is null!");
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectAttachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectAttachment> cq = cb.createQuery(BBSSubjectAttachment.class);
		Root<BBSSubjectAttachment> root = cq.from(BBSSubjectAttachment.class);
		Predicate p = cb.equal( root.get(BBSSubjectAttachment_.subjectId ), subjectId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "列示全部的BBSSubjectAttachment实体信息列表" )
	public List<BBSSubjectAttachment> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSubjectAttachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectAttachment> cq = cb.createQuery(BBSSubjectAttachment.class);
		return em.createQuery( cq ).setMaxResults( 1000 ).getResultList();
	}

	/**
	 * 根据指定的版块ID查询所有的附件信息ID列表
	 * @param sectionId
	 * @param queryMainSectionId 是否查询主版块ID
	 * @return
	 * @throws Exception 
	 */
	public List<String> listBySectionId(String sectionId, Boolean queryMainSectionId ) throws Exception {
		if( StringUtils.isNotEmpty( sectionId )){
			throw new Exception("sectionId id is empty!");
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectAttachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSubjectAttachment> root = cq.from(BBSSubjectAttachment.class);
		Predicate p = cb.equal( root.get(BBSSubjectAttachment_.sectionId ), sectionId );
		if( queryMainSectionId ) {
			p = cb.or( p, cb.equal( root.get(BBSSubjectAttachment_.mainSectionId ), sectionId ) );
		}
		cq.select( root.get( BBSSubjectAttachment_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
