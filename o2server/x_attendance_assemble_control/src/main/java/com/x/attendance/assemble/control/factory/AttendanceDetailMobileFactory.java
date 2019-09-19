package com.x.attendance.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceDetailMobile_;
/**
 * 系统配置信息表基础功能服务类
 * @author liyi
 */
public class AttendanceDetailMobileFactory extends AbstractFactory {
	
	public AttendanceDetailMobileFactory( Business business ) throws Exception {
		super(business);
	}

	public List<String> listByEmployeeNameDateAndTime( String empName, String recordDateString, String signTime ) throws Exception {
		if( empName == null || empName.isEmpty() ){
			throw new Exception("empName is null!");
		}
		if( recordDateString == null || recordDateString.isEmpty() ){
			throw new Exception("recordDateString is null!");
		}
		if( signTime == null || signTime.isEmpty() ){
			throw new Exception("signTime is null!");
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceDetailMobile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetailMobile> root = cq.from( AttendanceDetailMobile.class);
		cq.select(root.get(AttendanceDetailMobile_.id));
		Predicate p = cb.equal( root.get(AttendanceDetailMobile_.empName),  empName );
		p = cb.and( p, cb.equal( root.get(AttendanceDetailMobile_.recordDateString ),  recordDateString ) );
		p = cb.and( p, cb.equal( root.get(AttendanceDetailMobile_.signTime ),  signTime ) );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	/**
	 * 列示指定人员指定日期的所有移动打卡信息
	 * @param empName
	 * @param recordDateString
	 * @return
	 * @throws Exception
	 */
	public List<AttendanceDetailMobile> listAttendanceDetailMobileWithEmployee( String empName, String recordDateString) throws Exception {
		if( empName == null || empName.isEmpty() ){
			throw new Exception("empName is null!");
		}
		if( recordDateString == null || recordDateString.isEmpty() ){
			throw new Exception("recordDateString is null!");
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceDetailMobile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetailMobile> cq = cb.createQuery(AttendanceDetailMobile.class);
		Root<AttendanceDetailMobile> root = cq.from( AttendanceDetailMobile.class);
		Predicate p = cb.equal( root.get(AttendanceDetailMobile_.empName),  empName );
		p = cb.and( p, cb.equal( root.get(AttendanceDetailMobile_.recordDateString ),  recordDateString ) );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的AttendanceDetailMobile信息列表")
	public List<AttendanceDetailMobile> list(List<String> ids) throws Exception {
		List<AttendanceDetailMobile> resultList = null;
		if( ids == null || ids.size() == 0 ){
			throw new Exception("ids is null!");
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceDetailMobile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetailMobile> cq = cb.createQuery(AttendanceDetailMobile.class);
		Root<AttendanceDetailMobile> root = cq.from(AttendanceDetailMobile.class);
		Predicate p = root.get( AttendanceDetailMobile_.id).in( ids );
		resultList = em.createQuery( cq.where(p) ).getResultList();
		return resultList;
	}

	public Long countAttendanceDetailMobileForPage( String empNo, String empName, String signDescription,
			String startDate, String endDate) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetailMobile.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetailMobile> root = cq.from(AttendanceDetailMobile.class);
		Predicate p = cb.isNotNull( root.get( AttendanceDetailMobile_.id ) );
		if( StringUtils.isNotEmpty( empNo ) ){
			p = cb.and( p, cb.equal( root.get( AttendanceDetailMobile_.empNo ), empNo ) );
		}
		if( StringUtils.isNotEmpty( empName ) ){
			p = cb.and( p, cb.equal( root.get( AttendanceDetailMobile_.empName ), empName ) );
		}
		if( StringUtils.isNotEmpty( signDescription ) ){
			p = cb.and( p, cb.equal( root.get( AttendanceDetailMobile_.signDescription ), signDescription ) );
		}
		if( StringUtils.isNotEmpty( startDate ) ){
			if( StringUtils.isNotEmpty( endDate ) && !endDate.equals( startDate ) ){//查询日期区间
				p = cb.between( root.get( AttendanceDetailMobile_.recordDateString ), startDate, endDate );
			}else{
				//查询startDate当天
				p = cb.and( p, cb.equal( root.get( AttendanceDetailMobile_.recordDateString ), startDate ) );
			}
		}
		cq.select( cb.count( root ) );		
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	public List<AttendanceDetailMobile> listAttendanceDetailMobileForPage( String empNo, String empName, String signDescription,
			String startDate, String endDate, Integer selectTotal ) throws Exception {
		if( selectTotal == null ){
			selectTotal = 100;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetailMobile.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetailMobile> cq = cb.createQuery(AttendanceDetailMobile.class);
		Root<AttendanceDetailMobile> root = cq.from(AttendanceDetailMobile.class);
		Predicate p = cb.isNotNull( root.get( AttendanceDetailMobile_.id ) );
		if( StringUtils.isNotEmpty( empNo ) ){
			p = cb.and( p, cb.equal( root.get( AttendanceDetailMobile_.empNo ), empNo ) );
		}
		if( StringUtils.isNotEmpty( empName ) ){
			p = cb.and( p, cb.equal( root.get( AttendanceDetailMobile_.empName ), empName ) );
		}
		if( StringUtils.isNotEmpty( signDescription ) ){
			p = cb.and( p, cb.equal( root.get( AttendanceDetailMobile_.signDescription ), signDescription ) );
		}
		if( StringUtils.isNotEmpty( startDate ) ){
			if( StringUtils.isNotEmpty( endDate ) && !endDate.equals( startDate ) ){//查询日期区间
				p = cb.between( root.get( AttendanceDetailMobile_.recordDateString ), startDate, endDate );
			}else{
				//查询startDate当天
				p = cb.and( p, cb.equal( root.get( AttendanceDetailMobile_.recordDateString ), startDate ) );
			}
		}
		return em.createQuery(cq.where(p)).setMaxResults( selectTotal ).getResultList();
	}

	public AttendanceDetailMobile get(String id) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceDetailMobile.class );
	}

	public List<String> listAllAnalyseWithStatus(int status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceDetailMobile.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetailMobile> root = cq.from( AttendanceDetailMobile.class);
		cq.select(root.get(AttendanceDetailMobile_.id));
		Predicate p = cb.equal( root.get(AttendanceDetailMobile_.recordStatus), status );
		return em.createQuery(cq.where( p )).getResultList();
	}

	

}