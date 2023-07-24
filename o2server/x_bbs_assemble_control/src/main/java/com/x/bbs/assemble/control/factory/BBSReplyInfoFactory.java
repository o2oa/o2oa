package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.common.date.DateOperation;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSReplyInfo_;

/**
 * 类   名：BBSReplyInfoFactory<br/>
 * 实体类：BBSReplyInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class BBSReplyInfoFactory extends AbstractFactory {

	public BBSReplyInfoFactory( Business business ) throws Exception {
		super(business);
	}

	//@MethodDescribe( "获取指定Id的BBSReplyInfo实体信息对象" )
	public BBSReplyInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSReplyInfo.class, ExceptionWhen.none );
	}

	//@MethodDescribe( "列示指定Id的BBSReplyInfo实体信息列表" )
	public List<BBSReplyInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSReplyInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSReplyInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSReplyInfo> cq = cb.createQuery(BBSReplyInfo.class);
		Root<BBSReplyInfo> root = cq.from(BBSReplyInfo.class);
		Predicate p = root.get(BBSReplyInfo_.id).in(ids);
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 *
	 * @param subjectId
	 * @param showSubReply 是否平级显示所有的的回复, 如果为false则只显示第一层
	 * @return
	 * @throws Exception
	 */
	public Long countBySubjectId(String subjectId, Boolean showSubReply) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class);
		Predicate p = cb.equal( root.get( BBSReplyInfo_.subjectId ), subjectId );

		if( !showSubReply ){
			Predicate p_showSubReply = cb.isNull( root.get( BBSReplyInfo_.parentId ));
			p_showSubReply = cb.or( p_showSubReply, cb.equal( root.get( BBSReplyInfo_.parentId), ""));
			p = cb.and( p, p_showSubReply);
		}
		cq.select( cb.count( root ) );
//		System.out.println( ">>>>>SQL:" + em.createQuery(cq.where(p)).toString() );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据版块信息查询版块内主题数量，包括子版块内的主题数量" )
	public Long countBySectionId( String sectionId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class);
		Predicate p = cb.equal( root.get( BBSReplyInfo_.mainSectionId ), sectionId );
		p = cb.or( p, cb.equal( root.get( BBSReplyInfo_.sectionId ), sectionId ) );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据版块信息查询论坛内主题数量" )
	public Long countByForumId(String forumId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class);
		Predicate p = cb.equal( root.get( BBSReplyInfo_.forumId ), forumId );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 *根据主题ID获取该主题所有的回复信息对象列表
	 * @param subjectId
	 * @param showSubReply 是否平级显示所有的的回复, 如果为false则只显示第一层
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<BBSReplyInfo> listWithSubjectForPage(String subjectId, Boolean showSubReply, Integer maxCount, String orderType ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId can not null." );
		}
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSReplyInfo> cq = cb.createQuery( BBSReplyInfo.class );
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class );
		Predicate p = cb.equal( root.get( BBSReplyInfo_.subjectId ), subjectId );
		if( !showSubReply ){
			Predicate p_showSubReply = cb.isNull( root.get( BBSReplyInfo_.parentId ));
			p_showSubReply = cb.or( p_showSubReply, cb.equal( root.get( BBSReplyInfo_.parentId), ""));
			p = cb.and( p, p_showSubReply);
		}
		if( StringUtils.equalsIgnoreCase(orderType, "DESC")){
			cq.orderBy( cb.desc( root.get( BBSReplyInfo_.createTime ) ) );
		}else{
			cq.orderBy( cb.asc( root.get( BBSReplyInfo_.createTime ) ) );
		}
		if( maxCount == null ){
			return em.createQuery(cq.where(p)).setMaxResults( 2000 ).getResultList();
		}else{
			return em.createQuery(cq.where(p)).setMaxResults( maxCount ).getResultList();
		}
	}

	//@MethodDescribe( "根据主题ID获取该主题最大的回复编号（楼层）" )
	public Integer getMaxOrderNumber( String subjectId ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId can not null." );
		}
		List<BBSReplyInfo> replyInfoList = null;
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSReplyInfo> cq = cb.createQuery( BBSReplyInfo.class );
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class );
		Predicate p = cb.equal( root.get( BBSReplyInfo_.subjectId ), subjectId );
		cq.orderBy( cb.desc( root.get( BBSReplyInfo_.orderNumber ) ) );
		replyInfoList = em.createQuery(cq.where(p)).setMaxResults( 1 ).getResultList();
		if( ListTools.isNotEmpty( replyInfoList ) ){
			return replyInfoList.get(0).getOrderNumber();
		}else{
			return 0;
		}
	}

	//@MethodDescribe( "根据指定用户姓名、论坛ID，主版块ID， 版块ID查询符合条件的所有回复的数量" )
	public Long countReplyForPage( String creatorName, String forumId, String mainSectionId, String sectionId, String subjectId ) throws Exception {
		Boolean allFilterNull = true;
		if( StringUtils.isNotEmpty( creatorName ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( subjectId ) ){
			allFilterNull = false;
		}
		if( allFilterNull ){
			throw new Exception( "count filter can not all null." );
		}
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class);
		Predicate p = cb.isNotNull( root.get( BBSReplyInfo_.id ) );
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.creatorName ), creatorName ) );
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.forumId ), forumId ) );
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.mainSectionId ), mainSectionId ) );
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.sectionId ), sectionId ) );
		}
		if( StringUtils.isNotEmpty( subjectId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.subjectId ), subjectId ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "根据指定用户姓名、论坛ID，主版块ID， 版块ID查询符合条件的所有回复对象列表" )
	public List<BBSReplyInfo> listReplyForPage( String creatorName, String forumId, String mainSectionId, String sectionId, String subjectId, String orderType, Integer maxCount ) throws Exception {
		Boolean allFilterNull = true;
		if( StringUtils.isNotEmpty( creatorName ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( subjectId ) ){
			allFilterNull = false;
		}
		if( allFilterNull ){
			throw new Exception( "list filter can not all null." );
		}
		if( maxCount == null ){
			maxCount = 20;
		}
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSReplyInfo> cq = cb.createQuery( BBSReplyInfo.class );
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class );
		Predicate p = cb.isNotNull( root.get( BBSReplyInfo_.id ) );
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.creatorName ), creatorName ) );
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.forumId ), forumId ) );
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.mainSectionId ), mainSectionId ) );
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.sectionId ), sectionId ) );
		}
		if( StringUtils.isNotEmpty( subjectId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.subjectId ), subjectId ) );
		}
		if( StringUtils.equalsIgnoreCase(orderType, "DESC")){
			cq.orderBy( cb.desc( root.get( BBSReplyInfo_.createTime ) ) );
		}else{
			cq.orderBy( cb.asc( root.get( BBSReplyInfo_.createTime ) ) );
		}
		return em.createQuery(cq.where(p)).setMaxResults( maxCount ).getResultList();
	}

	//@MethodDescribe( "（今日）根据指定用户姓名、论坛ID，主版块ID， 版块ID查询符合条件的所有回复的数量" )
	public Long countReplyForTodayByUserName( String creatorName, String forumId, String mainSectionId, String sectionId, String subjectId ) throws Exception {
		Boolean allFilterNull = true;
		if( StringUtils.isNotEmpty( creatorName ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( subjectId ) ){
			allFilterNull = false;
		}
		if( allFilterNull ){
			throw new Exception( "list filter can not all null." );
		}
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class);
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSReplyInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.creatorName ), creatorName ) );
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.forumId ), forumId ) );
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.mainSectionId ), mainSectionId ) );
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.sectionId ), sectionId ) );
		}
		if( StringUtils.isNotEmpty( subjectId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.subjectId ), subjectId ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "（今日）根据指定用户姓名、论坛ID，主版块ID， 版块ID查询符合条件的所有回复对象列表" )
	public List<BBSReplyInfo> listReplyForTodayByUserName( String creatorName, String forumId, String mainSectionId, String sectionId, String subjectId, Integer maxCount ) throws Exception {
		Boolean allFilterNull = true;
		if( StringUtils.isNotEmpty( creatorName ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			allFilterNull = false;
		}
		if( StringUtils.isNotEmpty( subjectId ) ){
			allFilterNull = false;
		}
		if( allFilterNull ){
			throw new Exception( "list filter can not all null." );
		}
		if( maxCount == null ){
			maxCount = 20;
		}
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSReplyInfo> cq = cb.createQuery( BBSReplyInfo.class );
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class );
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSReplyInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.creatorName ), creatorName ) );
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.forumId ), forumId ) );
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.mainSectionId ), mainSectionId ) );
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.sectionId ), sectionId ) );
		}
		if( StringUtils.isNotEmpty( subjectId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.subjectId ), subjectId ) );
		}
		cq.orderBy( cb.asc( root.get( BBSReplyInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( maxCount ).getResultList();
	}

	public Long countReplyForTodayBySectionId( String sectionId ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSReplyInfo> root = cq.from(BBSReplyInfo.class);
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSReplyInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( sectionId ) ){
			Predicate or = cb.equal( root.get( BBSReplyInfo_.mainSectionId ), sectionId );
			or = cb.or( or, cb.equal( root.get( BBSReplyInfo_.sectionId ), sectionId ) );
			p = cb.and( p, or );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long countForTodayByForumId( String forumId ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSReplyInfo> root = cq.from(BBSReplyInfo.class);
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSReplyInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSReplyInfo_.forumId ), forumId ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 查询指定的版块内时间范围内发表的回帖数量
	 * @param sectionId
	 * @param todayStartTime
	 * @param todayEndTime
	 * @param queryMainSection  是否也查询主版块是sectionId的数据
	 * @return
	 * @throws Exception
	 */
	public Long countReplyByDate(String sectionId, Date todayStartTime, Date todayEndTime, boolean queryMainSection ) throws Exception {
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception("sectionId is null");
		}
		if( todayStartTime == null ) {
			todayStartTime = new DateOperation().getTodayStartTime();
		}
		if( todayEndTime == null ) {
			todayEndTime = new DateOperation().getTodayEndTime();
		}
		EntityManager em = this.entityManagerContainer().get(BBSReplyInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class );
		Predicate p = cb.equal( root.get( BBSReplyInfo_.sectionId ), sectionId );
		if( queryMainSection ) {
			p = cb.or( p, cb.equal( root.get( BBSReplyInfo_.mainSectionId ), sectionId ));
		}
		Predicate p_time = cb.between( root.get( BBSReplyInfo_.createTime ), todayStartTime, todayEndTime ) ;
		p = cb.and( p, p_time );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<String> listIdsByMainAndSubSectionId( String sectionId ) throws Exception {
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception( "sectionId is empty!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class );
		Predicate p = cb.equal( root.get( BBSReplyInfo_.mainSectionId ), sectionId );
		p = cb.or( p, cb.equal( root.get( BBSReplyInfo_.sectionId ), sectionId ) );
		cq.select( root.get( BBSReplyInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据回复ID，查询二级回复列表，状态：无审核|审核通过
	 *
	 * @param replyId
	 * @return
	 */
    public List<BBSReplyInfo> listReplyWithReplyId(String replyId, String orderType ) throws Exception {
		if( StringUtils.isEmpty( replyId ) ){
			throw new Exception( "replyId is empty!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSReplyInfo> cq = cb.createQuery( BBSReplyInfo.class );
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class );
		Predicate p = cb.equal( root.get( BBSReplyInfo_.parentId ), replyId );
		p = cb.and( p, cb.or(
				cb.equal( root.get( BBSReplyInfo_.replyAuditStatus ), "无审核" ),
				cb.equal( root.get( BBSReplyInfo_.replyAuditStatus ), "审核通过" )
		));
		if( StringUtils.equalsIgnoreCase( orderType, "DESC")){
			cq.orderBy( cb.desc( root.get( BBSReplyInfo_.createTime ) ) );
		}else{
			cq.orderBy( cb.asc( root.get( BBSReplyInfo_.createTime ) ) );
		}
		return em.createQuery(cq.where(p)).getResultList();
    }

	/**
	 * 根据回复ID，查询二级回复列表，状态：无审核|审核通过
	 *
	 * @param replyId
	 * @return
	 */
	public List<String> listSubReplyIdsWithReplyId(String replyId) throws Exception {
		if( StringUtils.isEmpty( replyId ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( BBSReplyInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSReplyInfo> root = cq.from( BBSReplyInfo.class );
		Predicate p = cb.equal( root.get( BBSReplyInfo_.parentId ), replyId );
		cq.select( root.get( BBSReplyInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listReplyIdsByCreator(String creatorName) throws Exception {
		if( StringUtils.isBlank(creatorName) ){
			return new ArrayList<>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSReplyInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSReplyInfo> root = cq.from(BBSReplyInfo.class);
		Predicate p = cb.equal(  root.get(BBSReplyInfo_.creatorName), creatorName);
		cq.select( root.get(BBSReplyInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

}
