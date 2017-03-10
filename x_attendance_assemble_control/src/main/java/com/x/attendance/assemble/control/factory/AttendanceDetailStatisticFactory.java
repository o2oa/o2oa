package com.x.attendance.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetail_;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

/**
 * 打卡信息统计服务类，以打卡信息表为基础进行统计
 * @author liyi
 */
public class AttendanceDetailStatisticFactory extends AbstractFactory {

	private Logger logger = LoggerFactory.getLogger( AttendanceDetailStatisticFactory.class );
	
	public AttendanceDetailStatisticFactory(Business business) throws Exception {
		super(business);
	}

	/**
	 * 根据员工，年月，统计异常打卡次数
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByEmployeeCycleYearAndMonth(List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbnormalDuty) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据员工，年月，统计工时不足次数
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLackOfTime) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据员工，年月，统计早退次数
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLeaveEarlier ) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据员工，年月，统计迟到次数
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLateByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLate ) ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据员工，年月，统计签退次数
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countOffDutyByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.offDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.offDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.empName).in( employeeNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据员工，年月，统计签退次数
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countOnDutyByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.onDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.onDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.empName).in( employeeNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据员工，年月，统计请假天数
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayDaysByEmployeeYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		// 不是周末，或者是周末但是调工作日了
		Predicate p1 = cb.isFalse(root.get( AttendanceDetail_.isWeekend ));
		p1 = cb.or( p1, cb.and(cb.isTrue(root.get( AttendanceDetail_.isWeekend )), cb.isTrue(root.get( AttendanceDetail_.isWorkday )) ));
								
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isFalse(root.get( AttendanceDetail_.isHoliday ))); //不是节假日
		p = cb.and( p, p1 ); //不是周末并且未调休工作晶
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear ));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		}
		
		//查询总数
		cq.select( cb.sum( root.get( AttendanceDetail_.getSelfHolidayDays ) ) );	
				
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	
	
	/**
	 * 根据员工，年月，统计缺勤天数
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDaysByEmployeeYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);	
		Predicate p = root.get(AttendanceDetail_.empName).in( employeeNames );		
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear));
		}
		
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.sum( root.get(AttendanceDetail_.absence ) ) );	
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，年月，统计异常打卡次数
	 * @param departmentNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByDempartmentCycleYearAndMonth(List<String> departmentNames, String cycleYear, String cycleMonth) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbnormalDuty) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，年月，统计工时不足次数
	 * @param departmentNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByDempartmentCycleYearAndMonth( List<String> departmentNames, String cycleYear, String cycleMonth) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLackOfTime) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，年月，统计早退次数
	 * @param departmentNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByDempartmentCycleYearAndMonth( List<String> departmentNames, String cycleYear, String cycleMonth) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLeaveEarlier ) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，年月，统计迟到次数
	 * @param departmentNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLateByDempartmentCycleYearAndMonth( List<String> departmentNames, String cycleYear, String cycleMonth) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLate ) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，打卡日期，统计异常打卡次数
	 * @param departmentNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByDempartmentAndDate(List<String> departmentNames, String recordDate ) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbnormalDuty) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，打卡日期，统计工时不足次数
	 * @param departmentNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByDempartmentAndDate( List<String> departmentNames, String recordDate ) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLackOfTime) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，打卡日期，统计早退次数
	 * @param departmentNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByDempartmentAndDate( List<String> departmentNames, String recordDate ) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLeaveEarlier ) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，打卡日期，统计迟到次数
	 * @param departmentNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLateByDempartmentAndDate( List<String> departmentNames, String recordDate ) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLate ) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据员工，打卡日期，统计缺勤天数
	 * @param departmentNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDaysByDepartmentAndDate( List<String> departmentNames, String recordDate ) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);	
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.sum( root.get(AttendanceDetail_.absence ) ) );	
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据员工，打卡日期月，统计请假天数
	 * @param departmentNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayDaysByDepartmentAndDate( List<String> departmentNames, String recordDate ) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);			
		Predicate p = root.get( AttendanceDetail_.departmentName ).in( departmentNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}		
		//查询总数
		cq.select( cb.sum( root.get( AttendanceDetail_.getSelfHolidayDays ) ) );	
				
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，打卡日期，统计签退人数
	 * @param departmentNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOffDutyByDepartmentAndDate( List<String> departmentNames, String recordDate ) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.offDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.offDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.departmentName).in( departmentNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.departmentName), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门，打卡日期，统计签到人数
	 * @param departmentNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOnDutyByDepartmentAndDate( List<String> departmentNames, String recordDate) throws Exception{
		if( departmentNames == null || departmentNames.size() == 0 ){
			logger.error( new DepartmentNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.onDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.onDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.departmentName ).in( departmentNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.departmentName), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司，打卡日期，统计异常打卡次数
	 * @param companyNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByCompanyAndDate(List<String> companyNames, String recordDate ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.companyName ).in( companyNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbnormalDuty) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司，打卡日期，统计工时不足次数
	 * @param companyNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByCompanyAndDate( List<String> companyNames, String recordDate ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.companyName ).in( companyNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLackOfTime) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司，打卡日期，统计早退次数
	 * @param companyNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByCompanyAndDate( List<String> companyNames, String recordDate ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.companyName ).in( companyNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLeaveEarlier ) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司，打卡日期，统计迟到次数
	 * @param companyNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLateByCompanyAndDate( List<String> companyNames, String recordDate ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.companyName ).in( companyNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLate ) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司，打卡日期，统计缺勤天数
	 * @param companyNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDaysByCompanyAndDate( List<String> companyNames, String recordDate ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);	
		Predicate p = root.get( AttendanceDetail_.companyName ).in( companyNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//查询总数
		cq.select( cb.sum( root.get(AttendanceDetail_.absence ) ) );	
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司，打卡日期月，统计请假天数
	 * @param companyNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayDaysByCompanyAndDate( List<String> companyNames, String recordDate ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);			
		Predicate p = root.get( AttendanceDetail_.companyName ).in( companyNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}		
		//查询总数
		cq.select( cb.sum( root.get( AttendanceDetail_.getSelfHolidayDays ) ) );	
				
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司，打卡日期，统计签到人数
	 * @param companyNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOffDutyByCompanyAndDate( List<String> companyNames, String recordDate ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.offDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.offDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.companyName ).in( companyNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司，打卡日期，统计签到人数
	 * @param companyNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOnDutyByCompanyAndDate( List<String> companyNames, String recordDate ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.onDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.onDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.companyName).in( companyNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
}