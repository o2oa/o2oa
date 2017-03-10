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
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticCompanyForDay;
import com.x.attendance.entity.StatisticCompanyForDay;
import com.x.attendance.entity.StatisticCompanyForDay_;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.annotation.MethodDescribe;

public class StatisticCompanyForDayFactory extends AbstractFactory {

	private Logger logger = LoggerFactory.getLogger( StatisticCompanyForDayFactory.class );
	
	public StatisticCompanyForDayFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的StatisticCompanyForDay应用信息对象")
	public StatisticCompanyForDay get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticCompanyForDay.class, ExceptionWhen.none);
	}
	
	@MethodDescribe("列示全部的StatisticCompanyForDay应用信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticCompanyForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticCompanyForDay> root = cq.from( StatisticCompanyForDay.class);
		cq.select(root.get(StatisticCompanyForDay_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的StatisticCompanyForDay应用信息列表")
	public List<StatisticCompanyForDay> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticCompanyForDay>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticCompanyForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticCompanyForDay> cq = cb.createQuery(StatisticCompanyForDay.class);
		Root<StatisticCompanyForDay> root = cq.from(StatisticCompanyForDay.class);
		Predicate p = root.get(StatisticCompanyForDay_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByCompanyRecordDateString(String companyName, String sDate) throws Exception{
		if( companyName == null || companyName.isEmpty() ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}
		
		EntityManager em = this.entityManagerContainer().get( StatisticCompanyForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root< StatisticCompanyForDay> root = cq.from( StatisticCompanyForDay.class);
		Predicate p = cb.equal( root.get( StatisticCompanyForDay_.companyName), companyName);
		if( sDate == null || sDate.isEmpty() ){
			logger.error( new StatisticDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticCompanyForDay_.statisticDate), sDate));
		}
		cq.select(root.get( StatisticCompanyForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}

	public List<String> listByNameYearAndMonth(String companyName, String year, String month) throws Exception {
		if( companyName == null || companyName.isEmpty() ){
			logger.error( new CompanyNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticCompanyForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root< StatisticCompanyForDay> root = cq.from( StatisticCompanyForDay.class);
		Predicate p = cb.equal( root.get( StatisticCompanyForDay_.companyName), companyName);
		if( year == null || year.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticCompanyForDay_.statisticYear), year));
		}
		if( month == null || month.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticCompanyForDay_.statisticMonth), month));
		}
		cq.select(root.get( StatisticCompanyForDay_.id));
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
	public List<StatisticCompanyForDay> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticCompanyForDay wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticCompanyForDay.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+StatisticCompanyForDay.class.getCanonicalName()+" o where 1=1" );

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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticCompanyForDay.class );
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
	public List<StatisticCompanyForDay> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticCompanyForDay wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticCompanyForDay.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+StatisticCompanyForDay.class.getCanonicalName()+" o where 1=1" );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticCompanyForDay.class );
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
	public long getCountWithFilter( WrapInFilterStatisticCompanyForDay wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticCompanyForDay.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+StatisticCompanyForDay.class.getCanonicalName()+" o where 1=1" );
		
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticCompanyForDay.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}
}