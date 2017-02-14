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
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticCompanyForMonth;
import com.x.attendance.entity.StatisticCompanyForMonth;
import com.x.attendance.entity.StatisticCompanyForMonth_;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;

public class StatisticCompanyForMonthFactory extends AbstractFactory {

	private Logger logger = LoggerFactory.getLogger( StatisticCompanyForMonthFactory.class );
	
	public StatisticCompanyForMonthFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的StatisticCompanyForMonth信息对象")
	public StatisticCompanyForMonth get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticCompanyForMonth.class, ExceptionWhen.none);
	}
	
	@MethodDescribe("列示全部的StatisticCompanyForMonth信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticCompanyForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticCompanyForMonth> root = cq.from( StatisticCompanyForMonth.class);
		cq.select(root.get(StatisticCompanyForMonth_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的StatisticCompanyForMonth信息列表")
	public List<StatisticCompanyForMonth> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticCompanyForMonth>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticCompanyForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticCompanyForMonth> cq = cb.createQuery(StatisticCompanyForMonth.class);
		Root<StatisticCompanyForMonth> root = cq.from(StatisticCompanyForMonth.class);
		Predicate p = root.get(StatisticCompanyForMonth_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByCompanyYearAndMonth(String companyName, String sYear, String sMonth) throws Exception {
		if( companyName == null || companyName.isEmpty() ){
			logger.error("organizationName is null!");
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticCompanyForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticCompanyForMonth> root = cq.from( StatisticCompanyForMonth.class);
		Predicate p = cb.equal( root.get(StatisticCompanyForMonth_.companyName), companyName);
		if( sYear == null || sYear.isEmpty() ){
			logger.error("sYear is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticCompanyForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error("sMonth is null!");
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticCompanyForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticCompanyForMonth_.id));
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
	public List<StatisticCompanyForMonth> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticCompanyForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticCompanyForMonth.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+StatisticCompanyForMonth.class.getCanonicalName()+" o where 1=1" );

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
		
		//logger.debug("listIdsNextWithFilter:["+sql_stringBuffer.toString()+"]");
		//logger.debug( vs );
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticCompanyForMonth.class );
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
	public List<StatisticCompanyForMonth> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticCompanyForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticCompanyForMonth.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+StatisticCompanyForMonth.class.getCanonicalName()+" o where 1=1" );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticCompanyForMonth.class );
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
	public long getCountWithFilter( WrapInFilterStatisticCompanyForMonth wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticCompanyForMonth.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+StatisticCompanyForMonth.class.getCanonicalName()+" o where 1=1" );
		
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticCompanyForMonth.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}

}