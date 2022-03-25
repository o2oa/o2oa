package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.CriteriaQueryTools;
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticUnitForMonth;
import com.x.attendance.entity.StatisticUnitForMonth;
import com.x.attendance.entity.StatisticUnitForMonth_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class StatisticUnitForMonthFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger( StatisticUnitForMonthFactory.class );
	
	public StatisticUnitForMonthFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的StatisticUnitForMonth信息对象")
	public StatisticUnitForMonth get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticUnitForMonth.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的StatisticUnitForMonth信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);
		cq.select(root.get(StatisticUnitForMonth_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的StatisticUnitForMonth信息列表")
	public List<StatisticUnitForMonth> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticUnitForMonth>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticUnitForMonth> cq = cb.createQuery(StatisticUnitForMonth.class);
		Root<StatisticUnitForMonth> root = cq.from(StatisticUnitForMonth.class);
		Predicate p = root.get(StatisticUnitForMonth_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	
	
	
	public List<String> listByUnitYearAndMonth( String unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.isEmpty() ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);
		Predicate p = cb.equal( root.get(StatisticUnitForMonth_.unitName), unitName);
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticUnitForMonth_.id));
		return em.createQuery(cq.where(p)).setMaxResults(60).getResultList();
	}
	
	public List<String> listByUnitYearAndMonth( List<String> unitNames, String sYear, String sMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0  ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in(unitNames);
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticUnitForMonth_.id));
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
	@SuppressWarnings("unchecked")
	public List<StatisticUnitForMonth> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticUnitForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticUnitForMonth> cq = cb.createQuery(StatisticUnitForMonth.class);
		Root<StatisticUnitForMonth> root = cq.from(StatisticUnitForMonth.class);

		String order = wrapIn.getOrder();//排序方式
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		String orderFieldName = "";
		if(StringUtils.isNotEmpty( wrapIn.getKey())){
			orderFieldName = wrapIn.getKey();
		}else{
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticUnitForMonth_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(StatisticUnitForMonth_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(StatisticUnitForMonth_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(StatisticUnitForMonth_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticMonth),wrapIn.getStatisticMonth()));
		}
		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
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
	public List<StatisticUnitForMonth> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticUnitForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticUnitForMonth> cq = cb.createQuery(StatisticUnitForMonth.class);
		Root<StatisticUnitForMonth> root = cq.from(StatisticUnitForMonth.class);

		String order = wrapIn.getOrder();//排序方式
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		String orderFieldName = "";
		if(StringUtils.isNotEmpty( wrapIn.getKey())){
			orderFieldName = wrapIn.getKey();
		}else{
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticUnitForMonth_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(StatisticUnitForMonth_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.greaterThan(root.get(StatisticUnitForMonth_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.lessThan(root.get(StatisticUnitForMonth_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticMonth),wrapIn.getStatisticMonth()));
		}
		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(20).getResultList();
	}
	
	/**
	 * 查询符合的文档信息总数
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilterStatisticUnitForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from(StatisticUnitForMonth.class);
		Predicate p = cb.isNotNull(root.get(StatisticUnitForMonth_.id));

		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticMonth),wrapIn.getStatisticMonth()));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员迟到次数总和
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumLateCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.lateCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员异常打卡次数总和
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumAbNormalDutyCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.abNormalDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员工时不足人次总和
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumLackOfTimeCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.lackOfTimeCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员早退人次总和
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumLeaveEarlyCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.leaveEarlyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员签退人次总和
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumOffDutyCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.offDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员签到人次总和
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumOnDutyCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员出勤人天总和
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Double sumAttendanceDayCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onDutyEmployeeCount ) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员请假人次总和
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onSelfHolidayCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据组织名称，统计年月，统计顶层组织所有人员缺勤人次总和
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDayCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.absenceDayCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );		
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据顶层组织名称，统计年月，统计顶层组织所有人员数量
	 * @return
	 * @throws Exception
	 */
	public Long sumLateCountByTopUnitNamesYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.lateCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据顶层组织名称，统计年月，统计顶层组织所有人员出勤人天数总和
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAttendanceDayCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onDutyEmployeeCount ) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 根据顶层组织名称，统计年月，统计顶层组织所有人员异常打卡次数总和
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumAbNormalDutyCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.abNormalDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据顶层组织名称，统计年月，统计顶层组织所有人员工时不足次数总和
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLackOfTimeCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.lackOfTimeCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据顶层组织名称，统计年月，统计顶层组织所有人员早退人次总和
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLeaveEarlyCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.leaveEarlyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据顶层组织名称，统计年月，统计顶层组织所有人员签退人次总和
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOffDutyCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.offDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据顶层组织名称，统计年月，统计顶层组织所有人员签到人次总和
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOnDutyCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据顶层组织名称，统计年月，统计顶层组织所有人员请假人次总和
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onSelfHolidayCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 根据顶层组织名称，统计年月，统计顶层组织所有人员缺勤人天总和
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDayCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//查询总数
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.absenceDayCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
}