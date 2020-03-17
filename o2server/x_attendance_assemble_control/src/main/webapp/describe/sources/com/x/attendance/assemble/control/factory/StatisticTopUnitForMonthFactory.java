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
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticTopUnitForMonth;
import com.x.attendance.entity.StatisticTopUnitForMonth;
import com.x.attendance.entity.StatisticTopUnitForMonth_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class StatisticTopUnitForMonthFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger( StatisticTopUnitForMonthFactory.class );
	
	public StatisticTopUnitForMonthFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的StatisticTopUnitForMonth信息对象")
	public StatisticTopUnitForMonth get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticTopUnitForMonth.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的StatisticTopUnitForMonth信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticTopUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticTopUnitForMonth> root = cq.from( StatisticTopUnitForMonth.class);
		cq.select(root.get(StatisticTopUnitForMonth_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的StatisticTopUnitForMonth信息列表")
	public List<StatisticTopUnitForMonth> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticTopUnitForMonth>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticTopUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticTopUnitForMonth> cq = cb.createQuery(StatisticTopUnitForMonth.class);
		Root<StatisticTopUnitForMonth> root = cq.from(StatisticTopUnitForMonth.class);
		Predicate p = root.get(StatisticTopUnitForMonth_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByTopUnitYearAndMonth(String topUnitName, String sYear, String sMonth) throws Exception {
		if( topUnitName == null || topUnitName.isEmpty() ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticTopUnitForMonth> root = cq.from( StatisticTopUnitForMonth.class);
		Predicate p = cb.equal( root.get(StatisticTopUnitForMonth_.topUnitName), topUnitName);
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticTopUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticTopUnitForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticTopUnitForMonth_.id));
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
	public List<StatisticTopUnitForMonth> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticTopUnitForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForMonth.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+StatisticTopUnitForMonth.class.getCanonicalName()+" o where 1=1" );

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
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticTopUnitForMonth.class );
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
	public List<StatisticTopUnitForMonth> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticTopUnitForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForMonth.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+StatisticTopUnitForMonth.class.getCanonicalName()+" o where 1=1" );
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
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticTopUnitForMonth.class );
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
	public long getCountWithFilter( WrapInFilterStatisticTopUnitForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForMonth.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+StatisticTopUnitForMonth.class.getCanonicalName()+" o where 1=1" );
		
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
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticTopUnitForMonth.class );
		
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}

}