package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticDepartmentForMonth;
import com.x.attendance.entity.StatisticDepartmentForMonth;
import com.x.attendance.entity.StatisticDepartmentForMonth_;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;

public class StatisticDepartmentForMonthFactory extends AbstractFactory {

	private Logger logger = LoggerFactory.getLogger( StatisticDepartmentForMonthFactory.class );
	
	public StatisticDepartmentForMonthFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的StatisticDepartmentForMonth信息对象")
	public StatisticDepartmentForMonth get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticDepartmentForMonth.class, ExceptionWhen.none);
	}
	
	@MethodDescribe("列示全部的StatisticDepartmentForMonth信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);
		cq.select(root.get(StatisticDepartmentForMonth_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的StatisticDepartmentForMonth信息列表")
	public List<StatisticDepartmentForMonth> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticDepartmentForMonth>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticDepartmentForMonth> cq = cb.createQuery(StatisticDepartmentForMonth.class);
		Root<StatisticDepartmentForMonth> root = cq.from(StatisticDepartmentForMonth.class);
		Predicate p = root.get(StatisticDepartmentForMonth_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	
	
	
	public List<String> listByDepartmentYearAndMonth( String organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.isEmpty() ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);
		Predicate p = cb.equal( root.get(StatisticDepartmentForMonth_.organizationName), organizationName);
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticDepartmentForMonth_.id));
		return em.createQuery(cq.where(p)).setMaxResults(60).getResultList();
	}
	
	public List<String> listByDepartmentYearAndMonth( List<String> organizationNames, String sYear, String sMonth) throws Exception{
		if( organizationNames == null || organizationNames.size() == 0  ){
			logger.error("organizationNames is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in(organizationNames);
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticDepartmentForMonth_.id));
		return em.createQuery(cq.where(p)).setMaxResults(60).getResultList();
	}
	
	/**
	 * 查询下一页的信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<StatisticDepartmentForMonth> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticDepartmentForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+StatisticDepartmentForMonth.class.getCanonicalName()+" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append(" and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
				
		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ?" + (index));
			vs.add( wrapIn.getEmployeeName() );
			index++;
		}
		if ((null != wrapIn.getOrganizationName()) && wrapIn.getOrganizationName().size() > 0 ) {
			sql_stringBuffer.append(" and o.organizationName in ?" + (index));
			vs.add( wrapIn.getOrganizationName() );
			index++;
		}
		if ((null != wrapIn.getCompanyName()) && wrapIn.getCompanyName().size() > 0 ) {
			sql_stringBuffer.append(" and o.companyName in ?" + (index));
			vs.add( wrapIn.getCompanyName() );
			index++;
		}
		if ((null != wrapIn.getStatisticYear() ) && (!wrapIn.getStatisticYear().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticYear = ?" + (index));
			vs.add( wrapIn.getStatisticYear() );
			index++;
		}
		if ((null != wrapIn.getStatisticMonth()) && (!wrapIn.getStatisticMonth().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticMonth = ?" + (index));
			vs.add( wrapIn.getStatisticMonth() );
			index++;
		}
		sql_stringBuffer.append(" order by o.sequence " + order );		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticDepartmentForMonth.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		return query.setMaxResults(count).getResultList();
	}	
	
	/**
	 * 查询上一页的文档信息数据
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<StatisticDepartmentForMonth> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticDepartmentForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+StatisticDepartmentForMonth.class.getCanonicalName()+" o where 1=1" );
		if ((null != sequence) ) {
			sql_stringBuffer.append(" and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? ">" : "<") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ?" + (index));
			vs.add( wrapIn.getEmployeeName() );
			index++;
		}
		if ((null != wrapIn.getOrganizationName()) && wrapIn.getOrganizationName().size() > 0 ) {
			sql_stringBuffer.append(" and o.organizationName in ?" + (index));
			vs.add( wrapIn.getOrganizationName() );
			index++;
		}
		if ((null != wrapIn.getCompanyName()) && wrapIn.getCompanyName().size() > 0 ) {
			sql_stringBuffer.append(" and o.companyName in ?" + (index));
			vs.add( wrapIn.getCompanyName() );
			index++;
		}
		if ((null != wrapIn.getStatisticYear() ) && (!wrapIn.getStatisticYear().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticYear = ?" + (index));
			vs.add( wrapIn.getStatisticYear() );
			index++;
		}
		if ((null != wrapIn.getStatisticMonth()) && (!wrapIn.getStatisticMonth().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticMonth = ?" + (index));
			vs.add( wrapIn.getStatisticMonth() );
			index++;
		}
		sql_stringBuffer.append(" order by o.sequence " + order );
		
		//logger.debug("listIdsPrevWithFilter:["+sql_stringBuffer.toString()+"]");
		//logger.debug( vs );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticDepartmentForMonth.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		
		return query.setMaxResults(20).getResultList();
	}
	
	/**
	 * 查询符合的文档信息总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilterStatisticDepartmentForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+StatisticDepartmentForMonth.class.getCanonicalName()+" o where 1=1" );
		
		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ?" + (index));
			vs.add( wrapIn.getEmployeeName() );
			index++;
		}
		if ((null != wrapIn.getOrganizationName()) && wrapIn.getOrganizationName().size() > 0 ) {
			sql_stringBuffer.append(" and o.organizationName in ?" + (index));
			vs.add( wrapIn.getOrganizationName() );
			index++;
		}
		if ((null != wrapIn.getCompanyName()) && wrapIn.getCompanyName().size() > 0 ) {
			sql_stringBuffer.append(" and o.companyName in ?" + (index));
			vs.add( wrapIn.getCompanyName() );
			index++;
		}
		if ((null != wrapIn.getStatisticYear() ) && (!wrapIn.getStatisticYear().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticYear = ?" + (index));
			vs.add( wrapIn.getStatisticYear() );
			index++;
		}
		if ((null != wrapIn.getStatisticMonth()) && (!wrapIn.getStatisticMonth().isEmpty())) {
			sql_stringBuffer.append(" and o.statisticMonth = ?" + (index));
			vs.add( wrapIn.getStatisticMonth() );
			index++;
		}
		
		//logger.debug("listIdsPrevWithFilter:["+sql_stringBuffer.toString()+"]");
		//logger.debug( vs );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticDepartmentForMonth.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员迟到次数总和
	 * @param organizationName
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLateCountByDepartmentYearAndMonth(List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.lateCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员异常打卡次数总和
	 * @param organizationName
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumAbNormalDutyCountByDepartmentYearAndMonth(List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.abNormalDutyCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员工时不足人次总和
	 * @param organizationName
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLackOfTimeCountByDepartmentYearAndMonth(List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.lackOfTimeCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员早退人次总和
	 * @param organizationName
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLeaveEarlyCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.leaveEarlyCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员签退人次总和
	 * @param organizationName
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOffDutyCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.offDutyCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员签到人次总和
	 * @param organizationName
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOnDutyCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.onDutyCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据部门名称，统计年月，统计公司所有人员出勤人天总和
	 * @param organizationName
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAttendanceDayCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.onDutyEmployeeCount ) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门名称，统计年月，统计公司所有人员请假人次总和
	 * @param organizationName
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.onSelfHolidayCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据部门名称，统计年月，统计公司所有人员缺勤人次总和
	 * @param organizationName
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDayCountByDepartmentYearAndMonth( List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.absenceDayCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );		
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司名称，统计年月，统计公司所有人员数量
	 * @param companyNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLateCountByCompanyNamesYearAndMonth(List<String> organizationName, String sYear, String sMonth) throws Exception{
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.lateCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.organizationName).in( organizationName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司名称，统计年月，统计公司所有人员出勤人天数总和
	 * @param companyNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAttendanceDayCountByCompanyNamesYearAndMonth( List<String> companyNames, String cycleYear, String cycleMonth ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error("companyNames is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.onDutyEmployeeCount ) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.companyName).in( companyNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error("cycleYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error("cycleMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据公司名称，统计年月，统计公司所有人员异常打卡次数总和
	 * @param companyNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumAbNormalDutyCountByCompanyNamesYearAndMonth( List<String> companyNames, String cycleYear, String cycleMonth ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error("companyNames is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.abNormalDutyCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.companyName).in( companyNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error("cycleYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error("cycleMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据公司名称，统计年月，统计公司所有人员工时不足次数总和
	 * @param companyNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLackOfTimeCountByCompanyNamesYearAndMonth( List<String> companyNames, String cycleYear, String cycleMonth ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error("companyNames is null!");
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.lackOfTimeCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.companyName).in( companyNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error("cycleYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error("cycleMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据公司名称，统计年月，统计公司所有人员早退人次总和
	 * @param companyNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLeaveEarlyCountByCompanyNamesYearAndMonth( List<String> companyNames, String cycleYear, String cycleMonth ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error("companyNames is null!");
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.leaveEarlyCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.companyName).in( companyNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error("cycleYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error("cycleMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据公司名称，统计年月，统计公司所有人员签退人次总和
	 * @param companyNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOffDutyCountByCompanyNamesYearAndMonth( List<String> companyNames, String cycleYear, String cycleMonth ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error("companyNames is null!");
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.offDutyCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.companyName).in( companyNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error("cycleYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error("cycleMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据公司名称，统计年月，统计公司所有人员签到人次总和
	 * @param companyNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOnDutyCountByCompanyNamesYearAndMonth( List<String> companyNames, String cycleYear, String cycleMonth ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error("companyNames is null!");
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.onDutyCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.companyName).in( companyNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error("cycleYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error("cycleMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据公司名称，统计年月，统计公司所有人员请假人次总和
	 * @param companyNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayCountByCompanyNamesYearAndMonth( List<String> companyNames, String cycleYear, String cycleMonth ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error("companyNames is null!");
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.onSelfHolidayCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.companyName).in( companyNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error("cycleYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error("cycleMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据公司名称，统计年月，统计公司所有人员缺勤人天总和
	 * @param companyNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDayCountByCompanyNamesYearAndMonth( List<String> companyNames, String cycleYear, String cycleMonth ) throws Exception{
		if( companyNames == null || companyNames.size() == 0 ){
			logger.error("companyNames is null!");
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticDepartmentForMonth> root = cq.from( StatisticDepartmentForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticDepartmentForMonth_.absenceDayCount) ) );		
		Predicate p = root.get(StatisticDepartmentForMonth_.companyName).in( companyNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error("cycleYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error("cycleMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticDepartmentForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
}