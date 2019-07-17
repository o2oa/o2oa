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

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticTopUnitForDay;
import com.x.attendance.entity.StatisticTopUnitForDay;
import com.x.attendance.entity.StatisticTopUnitForDay_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class StatisticTopUnitForDayFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger( StatisticTopUnitForDayFactory.class );
	
	public StatisticTopUnitForDayFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的StatisticTopUnitForDay应用信息对象")
	public StatisticTopUnitForDay get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticTopUnitForDay.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的StatisticTopUnitForDay应用信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticTopUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticTopUnitForDay> root = cq.from( StatisticTopUnitForDay.class);
		cq.select(root.get(StatisticTopUnitForDay_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的StatisticTopUnitForDay应用信息列表")
	public List<StatisticTopUnitForDay> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticTopUnitForDay>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticTopUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticTopUnitForDay> cq = cb.createQuery(StatisticTopUnitForDay.class);
		Root<StatisticTopUnitForDay> root = cq.from(StatisticTopUnitForDay.class);
		Predicate p = root.get(StatisticTopUnitForDay_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByTopUnitRecordDateString(String topUnitName, String sDate) throws Exception{
		if( topUnitName == null || topUnitName.isEmpty() ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}
		
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root< StatisticTopUnitForDay> root = cq.from( StatisticTopUnitForDay.class);
		Predicate p = cb.equal( root.get( StatisticTopUnitForDay_.topUnitName), topUnitName);
		if( sDate == null || sDate.isEmpty() ){
			logger.error( new StatisticDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticTopUnitForDay_.statisticDate), sDate));
		}
		cq.select(root.get( StatisticTopUnitForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}

	public List<String> listByNameYearAndMonth(String topUnitName, String year, String month) throws Exception {
		if( topUnitName == null || topUnitName.isEmpty() ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root< StatisticTopUnitForDay> root = cq.from( StatisticTopUnitForDay.class);
		Predicate p = cb.equal( root.get( StatisticTopUnitForDay_.topUnitName), topUnitName);
		if( year == null || year.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticTopUnitForDay_.statisticYear), year));
		}
		if( month == null || month.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticTopUnitForDay_.statisticMonth), month));
		}
		cq.select(root.get( StatisticTopUnitForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
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
	public List<StatisticTopUnitForDay> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticTopUnitForDay wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForDay.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+StatisticTopUnitForDay.class.getCanonicalName()+" o where 1=1" );

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
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0 ) {
			sql_stringBuffer.append(" and o.unitName in ?" + (index));
			vs.add( wrapIn.getUnitName() );
			index++;
		}
		if ((null != wrapIn.getTopUnitName()) && wrapIn.getTopUnitName().size() > 0 ) {
			sql_stringBuffer.append(" and o.topUnitName in ?" + (index));
			vs.add( wrapIn.getTopUnitName() );
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
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticTopUnitForDay.class );
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
	public List<StatisticTopUnitForDay> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticTopUnitForDay wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForDay.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+StatisticTopUnitForDay.class.getCanonicalName()+" o where 1=1" );
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
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0 ) {
			sql_stringBuffer.append(" and o.unitName in ?" + (index));
			vs.add( wrapIn.getUnitName() );
			index++;
		}
		if ((null != wrapIn.getTopUnitName()) && wrapIn.getTopUnitName().size() > 0 ) {
			sql_stringBuffer.append(" and o.topUnitName in ?" + (index));
			vs.add( wrapIn.getTopUnitName() );
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
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticTopUnitForDay.class );
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
	public long getCountWithFilter( WrapInFilterStatisticTopUnitForDay wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForDay.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+StatisticTopUnitForDay.class.getCanonicalName()+" o where 1=1" );
		
		if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			sql_stringBuffer.append(" and o.employeeName in ?" + (index));
			vs.add( wrapIn.getEmployeeName() );
			index++;
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0 ) {
			sql_stringBuffer.append(" and o.unitName in ?" + (index));
			vs.add( wrapIn.getUnitName() );
			index++;
		}
		if ((null != wrapIn.getTopUnitName()) && wrapIn.getTopUnitName().size() > 0 ) {
			sql_stringBuffer.append(" and o.topUnitName in ?" + (index));
			vs.add( wrapIn.getTopUnitName() );
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
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticTopUnitForDay.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}
}