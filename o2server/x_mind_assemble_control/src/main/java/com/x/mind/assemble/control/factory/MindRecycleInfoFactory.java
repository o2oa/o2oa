package com.x.mind.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.AbstractFactory;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindRecycleInfo;
import com.x.mind.entity.MindRecycleInfo_;


/**
 * 类   名：MindRecycleInfoFactory<br/>
 * 实体类：MindRecycleInfo<br/>
 * 作   者：O2LEE<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-11-15 17:17:26 
**/
public class MindRecycleInfoFactory extends AbstractFactory {

	public MindRecycleInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的回收站脑图实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindRecycleInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindRecycleInfo.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的回收站脑图实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<MindRecycleInfo> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<MindRecycleInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(MindRecycleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindRecycleInfo> cq = cb.createQuery(MindRecycleInfo.class);
		Root<MindRecycleInfo> root = cq.from(MindRecycleInfo.class);
		Predicate p = root.get(MindRecycleInfo_.id).in(ids);
		cq.orderBy( cb.desc( root.get( MindRecycleInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示全部的回收站脑图实体信息列表
	 * @return
	 * @throws Exception
	 */
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindRecycleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindRecycleInfo> root = cq.from(MindRecycleInfo.class);
		cq.orderBy( cb.desc( root.get( MindRecycleInfo_.updateTime ) ) );
		cq.select( root.get(MindRecycleInfo_.id ) );
		return em.createQuery( cq ).setMaxResults( 10000 ).getResultList();
	}

	/**
	 * 根据条件搜索符合条件的回收站脑图信息条目数量
	 * @param folderId  所属目录ID
	 * @param name	  脑图的名称（模糊搜索）
	 * @param person  脑图的创建者
	 * @param unit       脑图创建的组织
	 * @param hasShared  是否已经分享过了
	 * @return
	 * @throws Exception
	 */
	public Long count( String folderId, String name, String person, String unit, Boolean hasShared ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindRecycleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MindRecycleInfo> root = cq.from(MindRecycleInfo.class);
		Predicate p = cb.isNotNull( root.get(MindRecycleInfo_.id ) );
		if( folderId != null && !folderId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.id), folderId));
		}
		if( person != null && !person.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creator), person));
		}
		if( unit != null && !unit.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creatorUnit), unit));
		}
		if( hasShared != null ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.shared), hasShared));
		}
		if( name != null && !name.isEmpty() ){
			p = cb.and( p, cb.like( root.get( MindRecycleInfo_.name ), "%" + name + "%" ));
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据条件搜索符合条件的回收站脑图信息ID列表
	 * @param folderId  所属目录ID
	 * @param name	  脑图的名称（模糊搜索）
	 * @param person  脑图的创建者
	 * @param unit       脑图创建的组织
	 * @param hasShared  是否已经分享过了
	 * @return
	 * @throws Exception
	 */
	public List<String> list( String folderId, String name, String person, String unit, Boolean hasShared ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindRecycleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindRecycleInfo> root = cq.from(MindRecycleInfo.class);
		Predicate p = cb.isNotNull( root.get(MindRecycleInfo_.id ) );
		if( folderId != null && !folderId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.id), folderId));
		}
		if( person != null && !person.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creator), person));
		}
		if( unit != null && !unit.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creatorUnit), unit));
		}
		if( hasShared != null ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.shared), hasShared));
		}
		if( name != null && !name.isEmpty() ){
			p = cb.and( p, cb.like( root.get( MindRecycleInfo_.name ), "%" + name + "%" ));
		}
		cq.orderBy( cb.asc( root.get( MindRecycleInfo_.updateTime ) ) );
		cq.select( root.get(MindRecycleInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据条件搜索符合条件的脑图回收站信息ID列表(分页)
	 * @param count
	 * @param name
	 * @param folderId
	 * @param creator
	 * @param creatorUnit
	 * @param sequenceFieldValue
	 * @param orderField
	 * @param orderType
	 * @param inMindIds 
	 * @return
	 * @throws Exception 
	 */
	public List<MindRecycleInfo> listNextPageWithFilter(Integer count, String key, String folderId, Boolean shared, String creator, 
			String creatorUnit, Object sequenceFieldValue, String orderField, String orderType, List<String> inMindIds) throws Exception {
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		
		EntityManager em = this.entityManagerContainer().get( MindRecycleInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindRecycleInfo> cq = cb.createQuery( MindRecycleInfo.class );
		Root<MindRecycleInfo> root = cq.from( MindRecycleInfo.class );
		
		Predicate p = root.get( MindRecycleInfo_.id ).isNotNull();
		if( ListTools.isNotEmpty( inMindIds )) {
			p = root.get( MindRecycleInfo_.id ).in( inMindIds );
		}

		if( sequenceFieldValue != null ){
			if( "folderId".equals( orderField  )){//文件夹
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.folder_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.folder_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.folder_sequence ), sequenceFieldValue.toString() ));
				}
			}else if( "shared".equals( orderField  )){//是否已分享
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.shared_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.shared_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.shared_sequence ), sequenceFieldValue.toString() ));
				}
			}else if( "createTime".equals( orderField  )){//创建时间
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.createTime ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.createTime ), (Date)sequenceFieldValue ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.createTime ), (Date)sequenceFieldValue ));
				}
			}else if( "creatorUnit".equals( orderField  )){//创建者所属组织
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.creatorUnit_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.creatorUnit_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.creatorUnit_sequence ), sequenceFieldValue.toString() ));
				}
			}else if( "creator".equals( orderField  )){//创建者
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.creator_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.creator_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.creator_sequence ), sequenceFieldValue.toString() ));
				}
			}else if(  JpaObject.sequence_FIELDNAME.equals( orderField  )){//sequence
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.sequence ), sequenceFieldValue.toString() ));
				}
			}
		}
		
		if(  StringUtils.isNotEmpty(folderId) ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.folderId), folderId));
		}
		if(  shared != null ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.shared), shared));
		}
		if(  StringUtils.isNotEmpty(creatorUnit) ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creatorUnit), creatorUnit));
		}
		if(  StringUtils.isNotEmpty(creator) ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creator), creator));
		}
		if(  StringUtils.isNotEmpty(key) ){
			p = cb.and( p, cb.like( root.get( MindRecycleInfo_.name), "%"+key+"%"));
		}
		
		if( "folderId".equals( orderField  )){//文件夹
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.folder_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.folder_sequence ) ) );
			}
		}else if( "shared".equals( orderField  )){//是否已经分享
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.shared_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.shared_sequence ) ));
			}
		}else if( "createTime".equals( orderField  )){//创建时间
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.createTime ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.createTime ) ));
			}
		}else if( "creatorUnit".equals( orderField  )){//创建者所属组织
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.creatorUnit_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.creatorUnit_sequence ) ));
			}
		}else if( "creator".equals( orderField  )){//创建者
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.creator_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.creator_sequence ) ));
			}
		}else if(  JpaObject.sequence_FIELDNAME.equals( orderField  )){//sequence
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.sequence ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.sequence ) ) );
			}
		}

		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}
}
