package com.x.mind.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.mind.assemble.control.AbstractFactory;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindFolderInfo;
import com.x.mind.entity.MindFolderInfo_;


/**
 * 类   名：MindFolderInfoFactory<br/>
 * 实体类：MindFolderInfo<br/>
 * 作   者：O2LEE<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-11-15 17:17:26 
**/
public class MindFolderInfoFactory extends AbstractFactory {

	public MindFolderInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的脑图文件夹实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindFolderInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindFolderInfo.class, ExceptionWhen.none );
	}
	
	/**
	 * 列示指定Id的脑图文件夹实体信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<MindFolderInfo> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<MindFolderInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindFolderInfo> cq = cb.createQuery(MindFolderInfo.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		Predicate p = root.get(MindFolderInfo_.id).in(ids);
		cq.orderBy( cb.desc( root.get( MindFolderInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 列示全部的脑图文件夹实体信息列表
	 * @return
	 * @throws Exception
	 */
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		cq.select( root.get(MindFolderInfo_.id ) );
		return em.createQuery( cq ).setMaxResults( 10000 ).getResultList();
	}

	/**
	 * 根据条件查询符合条件的目录信息条目数量
	 * @param parentId
	 * @param folderId
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public Long count( String parentId, String folderId, String name, String creator, String creatorUnit ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		Predicate p = cb.isNotNull( root.get(MindFolderInfo_.id ) );
		if( folderId != null && !folderId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.id), folderId));
		}
		if( creator != null && !creator.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.creator), creator));
		}
		if( creatorUnit != null && !creatorUnit.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.creatorUnit), creatorUnit));
		}
		if( parentId != null && !parentId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.parentId ), parentId));
		}
		if( name != null && !name.isEmpty() ){
			p = cb.and( p, cb.like( root.get( MindFolderInfo_.name ), "%" + name + "%" ));
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据条件查询符合条件的目录信息
	 * @param parentId
	 * @param folderId
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> list( String parentId, String folderId, String name, String creator, String creatorUnit ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		Predicate p = cb.isNotNull( root.get(MindFolderInfo_.id ) );
		if( folderId != null && !folderId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.id), folderId));
		}
		if( creator != null && !creator.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.creator), creator));
		}
		if( creatorUnit != null && !creatorUnit.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.creatorUnit), creatorUnit));
		}
		if( parentId != null && !parentId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.parentId ), parentId));
		}
		if( name != null && !name.isEmpty() ){
			p = cb.and( p, cb.like( root.get( MindFolderInfo_.name ), "%" + name + "%" ));
		}
		cq.select( root.get(MindFolderInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据文件夹ID，获取所有下级文件夹个数
	 * @param folderId
	 * @return
	 * @throws Exception
	 */
	public Long countChildWithFolder(String folderId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		Predicate p = cb.equal(root.get( MindFolderInfo_.parentId ), folderId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
}
