package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticRequireLog_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 员工考勤需求配置服务器
 * @author liyi
 */
public class AttendanceStatisticRequireLogFactory extends AbstractFactory {
	
	public AttendanceStatisticRequireLogFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的AttendanceStatisticRequireLog信息对象")
	public AttendanceStatisticRequireLog get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceStatisticRequireLog.class, ExceptionWhen.none);
	}
	
//	@MethodDescribe("列示全部的AttendanceStatisticRequireLog信息列表")
	@SuppressWarnings("unused")
	public List<AttendanceStatisticRequireLog> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceStatisticRequireLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceStatisticRequireLog> cq = cb.createQuery(AttendanceStatisticRequireLog.class);
		Root<AttendanceStatisticRequireLog> root = cq.from( AttendanceStatisticRequireLog.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的AttendanceStatisticRequireLog信息列表")
	public List<AttendanceStatisticRequireLog> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceStatisticRequireLog>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceStatisticRequireLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceStatisticRequireLog> cq = cb.createQuery(AttendanceStatisticRequireLog.class);
		Root<AttendanceStatisticRequireLog> root = cq.from(AttendanceStatisticRequireLog.class);
		Predicate p = root.get(AttendanceStatisticRequireLog_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("根据统计名称，统计Key，处理状态，列示AttendanceStatisticRequireLog信息列表")
	public List<AttendanceStatisticRequireLog> getByNameKeyAndStatus( String name, String key, String stauts ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceStatisticRequireLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceStatisticRequireLog> cq = cb.createQuery(AttendanceStatisticRequireLog.class);
		Root<AttendanceStatisticRequireLog> root = cq.from(AttendanceStatisticRequireLog.class);
		Predicate p = root.get(AttendanceStatisticRequireLog_.id).isNotNull();
		if( StringUtils.isNotEmpty( name ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.statisticName), name));
		}
		if( StringUtils.isNotEmpty( key ) ){
			p = cb.and(p,  cb.equal(root.get(AttendanceStatisticRequireLog_.statisticKey), key));
		}
		if( StringUtils.isNotEmpty( stauts ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.processStatus), stauts));
		}
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<AttendanceStatisticRequireLog> getByNameKeyAndStatus(String statisticType, String key, String statisticYear, String statisticMonth, String statisticDate, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceStatisticRequireLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceStatisticRequireLog> cq = cb.createQuery(AttendanceStatisticRequireLog.class);
		Root<AttendanceStatisticRequireLog> root = cq.from(AttendanceStatisticRequireLog.class);
		Predicate p = root.get(AttendanceStatisticRequireLog_.id).isNotNull();
		if( StringUtils.isNotEmpty( statisticType ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.statisticType), statisticType));
		}
		if( StringUtils.isNotEmpty( key ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.statisticKey), key));
		}
		if( StringUtils.isNotEmpty( statisticYear ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.statisticYear), statisticYear));
		}
		if( StringUtils.isNotEmpty( statisticMonth ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.statisticMonth), statisticMonth));
		}
		if( StringUtils.isNotEmpty( statisticDate ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.statisticDay), statisticDate));
		}
		if( StringUtils.isNotEmpty( status ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.processStatus), status));
		}
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 根据统计的类型以及处理状态获取所有符合条件的统计需求对象列表
	 * 
	 * @param statisticType：PERSON_PER_MONTH|UNIT_PER_MONTH|TOPUNIT_PER_MONTH|UNIT_PER_DAY|TOPUNIT_PER_DAY
	 * @param processStatus：WAITING|PROCESSING|COMPLETE|ERROR
	 * @return
	 * @throws Exception
	 */
	public List<AttendanceStatisticRequireLog> listByStatisticTypeAndStatus( String statisticType, String processStatus) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceStatisticRequireLog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceStatisticRequireLog> cq = cb.createQuery(AttendanceStatisticRequireLog.class);
		Root<AttendanceStatisticRequireLog> root = cq.from(AttendanceStatisticRequireLog.class);
		Predicate p = root.get(AttendanceStatisticRequireLog_.id).isNotNull();
		if( StringUtils.isNotEmpty( statisticType ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.statisticType), statisticType));
		}
		if( StringUtils.isNotEmpty( processStatus ) ){
			p = cb.and(p, cb.equal(root.get(AttendanceStatisticRequireLog_.processStatus), processStatus));
		}
		return em.createQuery( cq.where(p) ).getResultList();
	}
}