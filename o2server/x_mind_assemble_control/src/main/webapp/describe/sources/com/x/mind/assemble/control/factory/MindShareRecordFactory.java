package com.x.mind.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.AbstractFactory;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindShareRecord;
import com.x.mind.entity.MindShareRecord_;


/**
 * 类   名：MindShareRecordFactory<br/>
 * 实体类：MindShareRecord<br/>
 * 作   者：O2LEE<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-11-15 17:17:26 
**/
public class MindShareRecordFactory extends AbstractFactory {

	public MindShareRecordFactory( Business business ) throws Exception {
		super(business);
	}

	public List<String> listIdsWithMindId(String mindId) throws Exception {
		if( StringUtils.isEmpty(mindId) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(MindShareRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindShareRecord> root = cq.from(MindShareRecord.class);
		Predicate p = cb.equal(  root.get(MindShareRecord_.fileId), mindId);
		p = cb.and( p,  cb.equal(  root.get(MindShareRecord_.fileType), "MIND"));
		cq.select( root.get(MindShareRecord_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<MindShareRecord> list(List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(MindShareRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindShareRecord> cq = cb.createQuery(MindShareRecord.class);
		Root<MindShareRecord> root = cq.from(MindShareRecord.class);
		Predicate p =  root.get(MindShareRecord_.id).in( ids );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Boolean exists(MindShareRecord ｍindShareRecord) throws Exception {
		if( ｍindShareRecord == null  ){
			throw new Exception( "ｍindShareRecord is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(MindShareRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindShareRecord> root = cq.from(MindShareRecord.class);
		Predicate p =  cb.equal( root.get(MindShareRecord_.fileId), ｍindShareRecord.getFileId() );
		p = cb.and( p , cb.equal( root.get(MindShareRecord_.target), ｍindShareRecord.getTarget()));
		p = cb.and( p , cb.equal( root.get(MindShareRecord_.targetType), ｍindShareRecord.getTargetType()));
		cq.select( root.get(MindShareRecord_.id) );
		List<String> ids = em.createQuery(cq.where(p)).getResultList();		
		return ListTools.isNotEmpty(ids);
	}

	public List<String> listSharedRecordIds(String source, List<String> targetList, List<String> inMindIds ) throws Exception {
		if( StringUtils.isEmpty(source)  ){
			throw new Exception( "source is empty!" );
		}
		EntityManager em = this.entityManagerContainer().get(MindShareRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindShareRecord> root = cq.from(MindShareRecord.class);
		Predicate p =  root.get(MindShareRecord_.id).isNotNull();
		if(StringUtils.isNotEmpty(source)) {
			p = cb.and( p , cb.equal( root.get(MindShareRecord_.source), source));
		}
		if( ListTools.isNotEmpty( targetList )) {
			p = cb.and( p , root.get(MindShareRecord_.target).in( targetList ));
		}
		if(ListTools.isNotEmpty( inMindIds )) {
			p = cb.and( p , root.get(MindShareRecord_.fileId).in( inMindIds ));
		}
		cq.select( root.get(MindShareRecord_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listSharedMindIdsFromRecord(String source, List<String> targetList, List<String> inMindIds) throws Exception {
		if( StringUtils.isEmpty(source)  ){
			throw new Exception( "source is empty!" );
		}
		EntityManager em = this.entityManagerContainer().get(MindShareRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindShareRecord> root = cq.from(MindShareRecord.class);
		Predicate p =  root.get(MindShareRecord_.id).isNotNull();
		if(StringUtils.isNotEmpty(source)) {
			p = cb.and( p , cb.equal( root.get(MindShareRecord_.source), source));
		}
		if( ListTools.isNotEmpty( targetList )) {
			p = cb.and( p , root.get(MindShareRecord_.target).in( targetList ));
		}
		if(ListTools.isNotEmpty( inMindIds )) {
			p = cb.and( p , root.get(MindShareRecord_.fileId).in( inMindIds ));
		}
		cq.select( root.get(MindShareRecord_.fileId) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	
}
