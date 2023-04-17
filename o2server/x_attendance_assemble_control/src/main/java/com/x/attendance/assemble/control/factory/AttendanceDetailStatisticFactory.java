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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 打卡信息统计服务类，以打卡信息表为基础进行统计
 */
public class AttendanceDetailStatisticFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailStatisticFactory.class );
	
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
	 * 根据员工，年月，统计次数 就是出勤天数
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countDutyDaysByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception {
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.equal( root.get( AttendanceDetail_.recordStatus ), 1);
		p = cb.and( p, root.get( AttendanceDetail_.empName).in( employeeNames ));
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
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbsent) ));
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
	 * 根据组织，年月，统计异常打卡次数
	 * @param unitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByUnitCycleYearAndMonth(List<String> unitNames, String cycleYear, String cycleMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * 根据组织，年月，统计工时不足次数
	 * @param unitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByUnitCycleYearAndMonth( List<String> unitNames, String cycleYear, String cycleMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * 根据组织，年月，统计早退次数
	 * @param unitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByUnitCycleYearAndMonth( List<String> unitNames, String cycleYear, String cycleMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * 根据组织，年月，统计迟到次数
	 * @param unitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLateByUnitCycleYearAndMonth( List<String> unitNames, String cycleYear, String cycleMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * 根据组织，打卡日期，统计异常打卡次数
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByUnitAndDate(List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * 根据组织，打卡日期，统计工时不足次数
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * 根据组织，打卡日期，统计早退次数
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * 根据组织，打卡日期，统计迟到次数
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLateByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDaysByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);	
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayDaysByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);			
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
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
	 * 根据组织，打卡日期，统计签退人数
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOffDutyByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.offDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.offDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.unitName).in( unitNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.unitName), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据组织，打卡日期，统计签到人数
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOnDutyByUnitAndDate( List<String> unitNames, String recordDate) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.onDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.onDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.unitName ).in( unitNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.unitName), recordDate));
		}
		//查询总数
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据顶层组织，打卡日期，统计异常打卡次数
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByTopUnitAndDate(List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
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
	 * 根据顶层组织，打卡日期，统计工时不足次数
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
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
	 * 根据顶层组织，打卡日期，统计早退次数
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
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
	 * 根据顶层组织，打卡日期，统计迟到次数
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLateByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
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
	 * 根据顶层组织，打卡日期，统计缺勤天数
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDaysByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);	
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
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
	 * 根据顶层组织，打卡日期月，统计请假天数
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayDaysByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);			
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
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
	 * 根据顶层组织，打卡日期，统计签到人数
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOffDutyByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.offDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.offDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.topUnitName ).in( topUnitNames ));
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
	 * 根据顶层组织，打卡日期，统计签到人数
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOnDutyByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.onDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.onDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.topUnitName).in( topUnitNames ));
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