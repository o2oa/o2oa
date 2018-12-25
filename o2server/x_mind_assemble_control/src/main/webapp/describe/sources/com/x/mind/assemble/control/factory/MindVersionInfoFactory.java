package com.x.mind.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.AbstractFactory;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindVersionContent;
import com.x.mind.entity.MindVersionInfo;
import com.x.mind.entity.MindVersionInfo_;


/**
 * 类   名：MindVersionInfoFactory<br/>
 * 实体类：MindVersionInfo<br/>
 * 作   者：O2LEE<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-11-15 17:17:26 
**/
public class MindVersionInfoFactory extends AbstractFactory {

	public MindVersionInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的脑图版本实体信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindVersionInfo getVersionInfoWithId( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindVersionInfo.class, ExceptionWhen.none );
	}
	
	/**
	 * 根据ID列表获取历史版本信息列表
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public List<MindVersionInfo> listVersionsWithIds(List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindVersionInfo> cq = cb.createQuery(MindVersionInfo.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = root.get(MindVersionInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 获取指定Id的脑图版本内容信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindVersionContent getVersionContentWithId( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindVersionContent.class, ExceptionWhen.none );
	}

	/**
	 * 根据脑图ID获取该脑图所有的版本信息
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<String> listVersionsWithMindId(String mindId) throws Exception {
		if( StringUtils.isEmpty( mindId ) ){
			new Exception("脑图ID为空，无法查询版本信息列表！");
		}
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = cb.equal( root.get(MindVersionInfo_.mindId) , mindId);
		cq.select( root.get(MindVersionInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据ID查询某个脑图的历史版本数量
	 * @param mindId
	 * @return
	 * @throws Exception
	 */
	public Long countMindVersionWithMindId(String mindId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = cb.equal( root.get( MindVersionInfo_.mindId), mindId);
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 根据脑图ID获取一个最早的版本信息ID
	 * @param mindId
	 * @return
	 * @throws Exception 
	 */
	public MindVersionInfo getEarliestVersionInfoId(String mindId) throws Exception {
		if( StringUtils.isEmpty( mindId ) ){
			new Exception("脑图ID为空，无法查询版本信息！");
		}
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindVersionInfo> cq = cb.createQuery(MindVersionInfo.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = cb.equal( root.get(MindVersionInfo_.mindId) , mindId);
		cq.orderBy( cb.asc( root.get( MindVersionInfo_.updateTime ) ) );
		List<MindVersionInfo> versions = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if(ListTools.isNotEmpty( versions )) {
			 return versions.get(0);
		}
		return null;
	}

	public MindVersionInfo getLatestVersionWithMind(String mindId) throws Exception {
		if( StringUtils.isEmpty( mindId ) ){
			new Exception("脑图ID为空，无法查询版本信息！");
		}
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindVersionInfo> cq = cb.createQuery(MindVersionInfo.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = cb.equal( root.get(MindVersionInfo_.mindId) , mindId);
		cq.orderBy( cb.desc( root.get( MindVersionInfo_.updateTime ) ) );
		List<MindVersionInfo> versions = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if(ListTools.isNotEmpty( versions )) {
			 return versions.get(0);
		 }
		return null;
	}

	
}
