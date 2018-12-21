package com.x.okr.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.okr.assemble.control.AbstractFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo_;

/**
 * 类   名：OkrWorkReportDetailInfoFactory<br/>
 * 实体类：OkrWorkReportDetailInfo<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkReportDetailInfoFactory extends AbstractFactory {

	public OkrWorkReportDetailInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的OkrWorkReportDetailInfo实体信息对象" )
	public OkrWorkReportDetailInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, OkrWorkReportDetailInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的OkrWorkReportDetailInfo实体信息列表" )
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportDetailInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportDetailInfo> root = cq.from( OkrWorkReportDetailInfo.class);
		cq.select(root.get(OkrWorkReportDetailInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的OkrWorkReportDetailInfo实体信息列表" )
	public List<OkrWorkReportDetailInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<OkrWorkReportDetailInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportDetailInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OkrWorkReportDetailInfo> cq = cb.createQuery(OkrWorkReportDetailInfo.class);
		Root<OkrWorkReportDetailInfo> root = cq.from(OkrWorkReportDetailInfo.class);
		Predicate p = root.get(OkrWorkReportDetailInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据工作信息ID，获取汇报详细信息ID列表
	 * @param workId
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe( "根据工作信息ID，获取汇报详细信息ID列表" )
	public List<String> listByWorkId(String workId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportDetailInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportDetailInfo> root = cq.from( OkrWorkReportDetailInfo.class);
		Predicate p = cb.equal( root.get(OkrWorkReportDetailInfo_.workId), workId );
		cq.select(root.get( OkrWorkReportDetailInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据中心工作ID，列示所有的数据信息
	 * @param centerId 中心工作
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe( "根据中心工作ID，列示所有的信息" )
	public List<String> listByCenterWorkId(String centerId) throws Exception {
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( " centerId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(OkrWorkReportDetailInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<OkrWorkReportDetailInfo> root = cq.from(OkrWorkReportDetailInfo.class);
		Predicate p = cb.equal( root.get( OkrWorkReportDetailInfo_.centerId ), centerId );
		cq.select(root.get( OkrWorkReportDetailInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
