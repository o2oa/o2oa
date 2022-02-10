package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.common.date.DateOperation;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSSubjectContent;
import com.x.bbs.entity.BBSSubjectContent_;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSSubjectInfo_;

/**
 * 类   名：BBSSubjectInfoFactory<br/>
 * 实体类：BBSSubjectInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class BBSSubjectInfoFactory extends AbstractFactory {

	public BBSSubjectInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe( "获取指定Id的BBSSubjectInfo实体信息对象" )
	public BBSSubjectInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSSubjectInfo.class, ExceptionWhen.none );
	}

	//@MethodDescribe( "列示指定Id的BBSSubjectInfo实体信息列表" )
	public List<BBSSubjectInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSSubjectInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = root.get(BBSSubjectInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据版块信息查询所有需要展现的所有置顶主题列表" )
	public List<String> listAllTopSubject( String forumId, String mainSectionId, String sectionId, String creatorName, Date startTime , Date endTime  ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		cq.select(root.get( BBSSubjectInfo_.id));

		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );

		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p,  cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}

		if(startTime!= null) {
			   p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			   p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		Predicate top_or = null;
		Predicate top_toforum_or = null;
		Predicate top_tomainsection_or = null;
		Predicate top_tosection_or = null;
		if( StringUtils.isNotEmpty( forumId ) ){
			top_toforum_or = cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId );
			top_toforum_or = cb.and( top_toforum_or, cb.isTrue( root.get( BBSSubjectInfo_.topToForum )) );
			top_or = top_toforum_or;
		}

		if( StringUtils.isNotEmpty( mainSectionId ) ){//在指定的主版块中的所有置顶主题
			top_tomainsection_or = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId );
			top_tomainsection_or = cb.and( top_tomainsection_or, cb.isTrue( root.get( BBSSubjectInfo_.topToMainSection )) );
			if( top_or != null ){
				top_or = cb.or( top_or, top_tomainsection_or );
			}else{
				top_or = top_tomainsection_or;
			}
		}

		if( StringUtils.isNotEmpty( sectionId ) ){//在指定的版块中的所有置顶主题
			top_tosection_or = cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId );
			top_tosection_or = cb.and( top_tosection_or, cb.isTrue( root.get( BBSSubjectInfo_.topToSection )) );
			if( top_or != null ){
				top_or = cb.or( top_or, top_tosection_or );
			}else{
				top_or = top_tosection_or;
			}
		}



		if( top_or != null ){
			p = cb.and( p, top_or );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据版块信息查询所有需要展现的所有置顶主题列表" )
	public List<String> listTopSubjectByType( String forumId, String mainSectionId, String sectionId,String subjectType, String creatorName, Date startTime , Date endTime  ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		cq.select(root.get( BBSSubjectInfo_.id));

		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );

		if( StringUtils.isNotEmpty( subjectType ) ){
			p = cb.and( p,  cb.equal( root.get( BBSSubjectInfo_.type ), subjectType ) );
		}

		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p,  cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}

		if(startTime!= null) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		Predicate top_or = null;
		Predicate top_toforum_or = null;
		Predicate top_tomainsection_or = null;
		Predicate top_tosection_or = null;
		if( StringUtils.isNotEmpty( forumId ) ){
			top_toforum_or = cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId );
			top_toforum_or = cb.and( top_toforum_or, cb.isTrue( root.get( BBSSubjectInfo_.topToForum )) );
			top_or = top_toforum_or;
		}

		if( StringUtils.isNotEmpty( mainSectionId ) ){//在指定的主版块中的所有置顶主题
			top_tomainsection_or = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId );
			top_tomainsection_or = cb.and( top_tomainsection_or, cb.isTrue( root.get( BBSSubjectInfo_.topToMainSection )) );
			if( top_or != null ){
				top_or = cb.or( top_or, top_tomainsection_or );
			}else{
				top_or = top_tomainsection_or;
			}
		}

		if( StringUtils.isNotEmpty( sectionId ) ){//在指定的版块中的所有置顶主题
			top_tosection_or = cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId );
			top_tosection_or = cb.and( top_tosection_or, cb.isTrue( root.get( BBSSubjectInfo_.topToSection )) );
			if( top_or != null ){
				top_or = cb.or( top_or, top_tosection_or );
			}else{
				top_or = top_tosection_or;
			}
		}



		if( top_or != null ){
			p = cb.and( p, top_or );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据版块信息查询论坛内主题数量" )
	public Long countByForumId( String forumId, Boolean withTopSubject ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据版块信息查询论坛内主题列表" )
	public List<BBSSubjectInfo> listByForumId( String forumId, Boolean withTopSubject ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据版块信息查询版块内主题数量，包括子版块内的主题数量" )
	public Long countByMainAndSubSectionId( String sectionId, Boolean withTopSubject ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId );
		p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				Predicate p1 = cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}else{
				Predicate p1 = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据版块ID查询所有主题列表，包括子版块内的主题" )
	public List<BBSSubjectInfo> listByMainAndSubSectionId( String sectionId, Boolean withTopSubject ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId );
		p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				Predicate p1 = cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}else{
				Predicate p1 = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据版块ID查询所有主题ID列表，包括子版块内的主题
	 * @param sectionId
	 * @param withTopSubject 是否包括置顶主题
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdsByMainAndSubSectionId( String sectionId, Boolean withTopSubject ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId );
		p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				Predicate p1 = cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}else{
				Predicate p1 = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}
		}
		cq.select( root.get( BBSSubjectInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据版块ID查询所有主题数量，不包括子版块内的主题" )
	public Long countSubjectIdsBySection( String sectionId ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal(  root.get(BBSSubjectInfo_.sectionId), sectionId);
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据版块ID查询所有主题列表，不包括子版块内的主题" )
	public List<BBSSubjectInfo> listSubjectBySection( String sectionId ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal(  root.get(BBSSubjectInfo_.sectionId), sectionId);
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults(100).getResultList();
	}

	//@MethodDescribe( "根据版块ID查询所有主题列表，不包括子版块内的主题" )
	public List<String> listSubjectIdsBySection( String sectionId, Integer maxResults ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal(  root.get(BBSSubjectInfo_.sectionId), sectionId);
		cq.select( root.get(BBSSubjectInfo_.id) );
		if( maxResults == null || maxResults == 0 ){
			return em.createQuery(cq.where(p)).getResultList();
		}else{
			return em.createQuery(cq.where(p)).setMaxResults( maxResults ).getResultList();
		}
	}

	//@MethodDescribe( "根据版块ID, 主版块ID，版块ID，创建者姓名查询符合要求所有主题列表，不包括子版块内的主题数量" )
	public Long countSubjectInSectionForPage(String searchTitle, String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, List<String> viewSectionIds , Date startTime , Date endTime) throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isNotNull( root.get(BBSSubjectInfo_.id ) );

		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}

		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		if( ListTools.isNotEmpty( viewSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds ) );
		}
		if( isTopSubject != null ){
			if( isTopSubject ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}

		if(startTime!= null) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		cq.select( cb.count( root ) );
		//SELECT COUNT(b) FROM BBSSubjectInfo b WHERE ((b.id IS NOT NULL AND b.sectionId IN ('1c1d9dfc-0034-4d9a-adc7-bb4b3925bbd5')) AND b.title LIKE 'Count')
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据版块名称，标题类别, 主版块ID，版块ID，创建者姓名查询符合要求所有主题列表，不包括子版块内的主题数量" )
	public Long countSubjectWithSubjectTypeSectionForPage(String sectionName,String subjectType, String searchTitle, String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, List<String> viewSectionIds , Date startTime , Date endTime) throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isNotNull( root.get(BBSSubjectInfo_.id ) );

		if( StringUtils.isNotEmpty( sectionName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionName ), sectionName));
		}
		if( StringUtils.isNotEmpty( subjectType ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.type ), subjectType));
		}

		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}

		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		if( ListTools.isNotEmpty( viewSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds ) );
		}
		if( isTopSubject != null ){
			if( isTopSubject ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}

		if(startTime!= null) {
			   p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			   p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		cq.select( cb.count( root ) );
		//SELECT COUNT(b) FROM BBSSubjectInfo b WHERE ((b.id IS NOT NULL AND b.sectionId IN ('1c1d9dfc-0034-4d9a-adc7-bb4b3925bbd5')) AND b.title LIKE 'Count')
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据版块ID, 主版块ID，版块ID，创建者姓名查询符合要求所有主题列表，不包括子版块内的主题" )
	public List<BBSSubjectInfo> listSubjectInSectionForPage( String searchTitle, String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, Integer maxRecordCount, List<String> viewSectionIds, Date startTime , Date endTime) throws Exception {
		if( maxRecordCount == null ){
			throw new Exception( "maxRecordCount is null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isNotNull( root.get(BBSSubjectInfo_.id ) );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		if( ListTools.isNotEmpty( viewSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds ) );
		}
		if( isTopSubject != null ){
			if( isTopSubject ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}

		if(startTime!= null) {
			   p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			   p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( maxRecordCount ).getResultList();
	}

	//@MethodDescribe( "根据版块名称,主题类别,根据版块ID, 主版块ID，版块ID，创建者姓名查询符合要求所有主题列表，不包括子版块内的主题" )
	public List<BBSSubjectInfo> listSubjectWithSubjectTypeForPage(String sectionName,String subjectType, String searchTitle, String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, Integer maxRecordCount, List<String> viewSectionIds, Date startTime , Date endTime) throws Exception {
		if( maxRecordCount == null ){
			throw new Exception( "maxRecordCount is null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isNotNull( root.get(BBSSubjectInfo_.id ) );

		if( StringUtils.isNotEmpty( sectionName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionName ), sectionName));
		}
		if( StringUtils.isNotEmpty( subjectType ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.type ), subjectType));
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		if( ListTools.isNotEmpty( viewSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds ) );
		}
		if( isTopSubject != null ){
			if( isTopSubject ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}

		if(startTime!= null) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( maxRecordCount ).getResultList();
	}

	//@MethodDescribe( "根据指定的主题ID获取上一个主题的ID，根据最新回帖时间排序" )
	public List<BBSSubjectInfo> listLastSubject( Date latestReplyTime ) throws Exception {
		if( latestReplyTime == null ){
			throw new Exception( "latestReplyTime is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery( BBSSubjectInfo.class );
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.greaterThan( root.get( BBSSubjectInfo_.latestReplyTime ), latestReplyTime );
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
	}

	//@MethodDescribe( "根据最后回帖时间，查询是否还有更早的回帖，数量查询" )
	public Long countLastSubject( Date latestReplyTime ) throws Exception {
		if( latestReplyTime == null ){
			throw new Exception( "latestReplyTime is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< Long> cq = cb.createQuery(  Long.class );
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.greaterThan( root.get( BBSSubjectInfo_.latestReplyTime ), latestReplyTime );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据指定的主题ID获取下一个主题的ID，根据最新回帖时间排序" )
	public List<BBSSubjectInfo> listNextSubject( Date latestReplyTime ) throws Exception {
		if( latestReplyTime == null ){
			throw new Exception( "latestReplyTime is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery( BBSSubjectInfo.class );
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.lessThan( root.get( BBSSubjectInfo_.latestReplyTime ), latestReplyTime );
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
	}

	//@MethodDescribe( "根据最后回帖时间，查询是否还有更晚的回帖，数量查询" )
	public Long countNextSubject( Date latestReplyTime ) throws Exception {
		if( latestReplyTime == null ){
			throw new Exception( "latestReplyTime is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< Long> cq = cb.createQuery(  Long.class );
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.lessThan( root.get( BBSSubjectInfo_.latestReplyTime ), latestReplyTime );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据论坛ID，主版块ID，版块ID查询指定用户发表的主题数量" )
	public Long countUserSubjectForPage( String searchTitle, String forumId, String mainSectionId, String sectionId, Boolean needPicture, Boolean withTopSubject, String creatorName ) throws Exception {
		if( creatorName == null || creatorName.isEmpty() ){
			throw new Exception( "creatorName can not null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( needPicture != null ){
			if( needPicture ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据论坛ID，主版块ID，版块ID查询指定用户发表的主题列表， 分页" )
	public List<BBSSubjectInfo> listUserSubjectForPage( String searchTitle, String forumId, String mainSectionId, String sectionId, Boolean needPicture, Boolean withTopSubject, Integer maxRecordCount, String creatorName ) throws Exception {
		if( creatorName == null || creatorName.isEmpty() ){
			throw new Exception( "creatorName can not null." );
		}
		if( maxRecordCount == null ){
			maxRecordCount = 20;
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName );
		if( needPicture != null ){
			if( needPicture ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( forumId != null ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.createTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( maxRecordCount ).getResultList();
	}

	//@MethodDescribe( "查询指定用户今天发表的主题数量" )
	public Long countSubjectForTodayByUserName( String userName ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSSubjectInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( userName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), userName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "查询指定用户发表的精华主题数量" )
	public Long countCreamSubjectByUserName( String userName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isCreamSubject ) );
		if( StringUtils.isNotEmpty( userName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), userName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "查询指定用户发表的原创主题数量" )
	public Long countOriginalSubjectByUserName( String userName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isCreamSubject ) );
		if( StringUtils.isNotEmpty( userName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), userName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "查询指定版块中当天发表的主题数量" )
	public Long countSubjectForTodayBySectionId( String sectionId ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSSubjectInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( sectionId ) ){
			Predicate or = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId );
			or = cb.or( or, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
			p = cb.and( p, or );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "查询指定信封中当天发表的主题数量" )
	public Long countForTodayByForumId( String forumId ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSSubjectInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据指定的论坛ID列表，主版块ID列表，版块ID列表查询推荐到BBS首页的主题列表" )
	public List<BBSSubjectInfo> listRecommendedSubjectForBBSIndex( List<String> forumIds, List<String> mainSectionIds, List<String> sectionIds, Integer count ) throws Exception {
		if( count == null ){
			count = 10;
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.recommendToBBSIndex ) );
		if( ListTools.isNotEmpty( forumIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.forumId ).in( forumIds ) );
		}
		if( ListTools.isNotEmpty( mainSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.mainSectionId ).in( mainSectionIds ) );
		}
		if( ListTools.isNotEmpty( sectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( sectionIds ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	//@MethodDescribe( "根据指定的论坛ID列表，主版块ID列表，版块ID列表查询推荐到论坛首页的主题列表" )
	public List<BBSSubjectInfo> listRecommendedSubjectForForumIndex( List<String> forumIds, List<String> mainSectionIds, List<String> sectionIds, Integer count) throws Exception {
		if( count == null ){
			count = 10;
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.recommendToForumIndex ) );
		if( ListTools.isNotEmpty( forumIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.forumId ).in( forumIds ) );
		}
		if( ListTools.isNotEmpty( mainSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.mainSectionId ).in( mainSectionIds ) );
		}
		if( ListTools.isNotEmpty( sectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( sectionIds ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	//@MethodDescribe( "根据指定的版块ID列表搜索符合条件的主题数量" )
	public Long countSubjectSearchInSectionForPage( String searchContent, List<String> viewSectionIds ) throws Exception {
		if( searchContent == null || searchContent.isEmpty() ){
			throw new Exception( "searchContent can not null." );
		}
		if( viewSectionIds == null || viewSectionIds.isEmpty() ){
			throw new Exception( "viewSectionIds can not null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds );
		Predicate or = cb.like( root.get( BBSSubjectInfo_.title ), "%"+searchContent+"%" );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.sectionName ), "%"+searchContent+"%" ) );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.forumName ), "%"+searchContent+"%" ) );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.creatorName ), "%"+searchContent+"%" ) );
		p = cb.and( p, or );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据指定的版块ID列表搜索符合条件的主题列表，分页" )
	public List<BBSSubjectInfo> listSubjectSearchInSectionForPage( String searchContent, List<String> viewSectionIds, Integer count ) throws Exception {
		if( searchContent == null || searchContent.isEmpty() ){
			throw new Exception( "searchContent can not null." );
		}
		if( viewSectionIds == null || viewSectionIds.isEmpty() ){
			throw new Exception( "viewSectionIds can not null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds );
		Predicate or = cb.like( root.get( BBSSubjectInfo_.title ), "%"+searchContent+"%" );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.sectionName ), "%"+searchContent+"%" ) );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.forumName ), "%"+searchContent+"%" ) );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.creatorName ), "%"+searchContent+"%" ) );
		p = cb.and( p, or );
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	//@MethodDescribe( "根据指定的版块ID、主版块ID、版块ID和指定的人员查询精华主题数量" )
	public Long countCreamedSubjectInSectionForPage( String forumId, String mainSectionId, String sectionId, String creatorName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isCreamSubject ) );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId ) );
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId ) );
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据指定的版块ID、主版块ID、版块ID和指定的人员查询精华主题列表，分页" )
	public List<BBSSubjectInfo> listCreamedSubjectInSectionForPage( String forumId, String mainSectionId, String sectionId, String creatorName, Integer count ) throws Exception {
		if( count == null ){
			count = 10;
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isCreamSubject ) );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId ) );
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId ) );
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	//@MethodDescribe( "根据指定的版块ID、主版块ID、版块ID和指定的人员查询推荐到BBS首页的主题数量" )
	public Long countRecommendedSubjectInSectionForPage( String searchForumId, String searchMainSectionId, String searchSectionId, String creatorName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.recommendToBBSIndex ) );
		if( StringUtils.isNotEmpty( searchForumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), searchForumId ) );
		}
		if( StringUtils.isNotEmpty( searchMainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), searchMainSectionId ) );
		}
		if( StringUtils.isNotEmpty( searchSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), searchSectionId ) );
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据指定的版块ID、主版块ID、版块ID和指定的人员查询推荐到BBS首页的主题列表" )
	public List<BBSSubjectInfo> listRecommendedSubjectInSectionForPage( String searchForumId, String searchMainSectionId,
			String searchSectionId, String creatorName, Integer count) throws Exception {
		if( count == null ){
			count = 10;
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.recommendToBBSIndex ) );
		if( StringUtils.isNotEmpty( searchForumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), searchForumId ) );
		}
		if( StringUtils.isNotEmpty( searchMainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), searchMainSectionId ) );
		}
		if( StringUtils.isNotEmpty( searchSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), searchSectionId ) );
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	public List<BBSSubjectContent> getSubjectContent( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null");
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectContent> cq = cb.createQuery(BBSSubjectContent.class);
		Root<BBSSubjectContent> root = cq.from( BBSSubjectContent.class );
		Predicate p = cb.equal( root.get( BBSSubjectContent_.id ), id );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 查询指定的版块内时间范围内发表的主题数量
	 * @param sectionId
	 * @param todayStartTime
	 * @param todayEndTime
	 * @param queryMainSection  是否也查询主版块是sectionId的数据
	 * @return
	 * @throws Exception
	 */
	public Long countSubjectByDate(String sectionId, Date todayStartTime, Date todayEndTime, boolean queryMainSection) throws Exception {
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception("sectionId is null");
		}
		if( todayStartTime == null ) {
			todayStartTime = new DateOperation().getTodayStartTime();
		}
		if( todayEndTime == null ) {
			todayEndTime = new DateOperation().getTodayEndTime();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId );
		if( queryMainSection ) {
			p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId ));
		}
		Predicate p_time = cb.between( root.get( BBSSubjectInfo_.createTime ), todayStartTime, todayEndTime ) ;
		p = cb.and( p, p_time );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<String> listSubjectIdsByCreator(String creatorName) throws Exception {
		if( StringUtils.isBlank(creatorName) ){
			return new ArrayList<>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal(  root.get(BBSSubjectInfo_.creatorName), creatorName);
		cq.select( root.get(BBSSubjectInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

}
