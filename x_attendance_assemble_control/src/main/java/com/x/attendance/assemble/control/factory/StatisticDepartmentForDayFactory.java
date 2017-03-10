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
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticDepartmentForDay;
import com.x.attendance.entity.StatisticDepartmentForDay;
import com.x.attendance.entity.StatisticDepartmentForDay_;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.annotation.MethodDescribe;

public class StatisticDepartmentForDayFactory extends AbstractFactory {

	private Logger logger = LoggerFactory.getLogger( StatisticDepartmentForDayFactory.class );
	
	public StatisticDepartmentForDayFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的StatisticDepartmentForDay信息对象")
	public StatisticDepartmentForDay get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticDepartmentForDay.class, ExceptionWhen.none);
	}
	
	@MethodDescribe("列示全部的StatisticDepartmentForDay信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticDepartmentForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticDepartmentForDay> root = cq.from( StatisticDepartmentForDay.class);
		cq.select(root.get(StatisticDepartmentForDay_.id));
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的StatisticDepartmentForDay信息列表")
	public List<StatisticDepartmentForDay> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticDepartmentForDay>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticDepartmentForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticDepartmentForDay> cq = cb.createQuery(StatisticDepartmentForDay.class);
		Root<StatisticDepartmentForDay> root = cq.from(StatisticDepartmentForDay.class);
		Predicate p = root.get(StatisticDepartmentForDay_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByDepartmentRecordDateString( List<String> organizationName, String sDate) throws Exception {
		if( organizationName == null || organizationName.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root< StatisticDepartmentForDay> root = cq.from( StatisticDepartmentForDay.class);
		Predicate p = root.get( StatisticDepartmentForDay_.organizationName).in(organizationName);
		if( sDate == null || sDate.isEmpty() ){
			logger.error( new StatisticDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticDepartmentForDay_.statisticDate), sDate));
		}
		cq.select(root.get( StatisticDepartmentForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}
	
	public List<String> listByDepartmentRecordDateString( String organizationName, String sDate) throws Exception {
		if( organizationName == null || organizationName.isEmpty() ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}
		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root< StatisticDepartmentForDay> root = cq.from( StatisticDepartmentForDay.class);
		Predicate p = cb.equal( root.get( StatisticDepartmentForDay_.organizationName), organizationName);
		if( sDate == null || sDate.isEmpty() ){
			logger.error( new StatisticDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticDepartmentForDay_.statisticDate), sDate));
		}
		cq.select(root.get( StatisticDepartmentForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}

	public List<String> listByDepartmentDayYearAndMonth(List<String> name, String year, String month) throws Exception {
		if( name == null || name.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}
		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root< StatisticDepartmentForDay> root = cq.from( StatisticDepartmentForDay.class);
		Predicate p = root.get( StatisticDepartmentForDay_.organizationName).in(name);
		if( year == null || year.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticDepartmentForDay_.statisticYear), year));
		}
		if( month == null || month.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticDepartmentForDay_.statisticMonth), month));
		}
		cq.select(root.get( StatisticDepartmentForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}

	public List<String> listByDepartmentDayDate(List<String> name, String date) throws Exception{
		if( name == null || name.size() == 0 ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}
		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root< StatisticDepartmentForDay> root = cq.from( StatisticDepartmentForDay.class);
		Predicate p = root.get( StatisticDepartmentForDay_.organizationName).in( name );
		if( date == null || date.isEmpty() ){
			logger.error( new StatisticDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticDepartmentForDay_.statisticDate), date));
		}
		cq.select(root.get( StatisticDepartmentForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}
	
	public List<String> listByDepartmentDayDate( String name, String date) throws Exception{
		if( name == null || name.isEmpty() ){
			logger.error( new OganizationNamesEmptyException() );
			return null;
		}
		
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root< StatisticDepartmentForDay> root = cq.from( StatisticDepartmentForDay.class);
		Predicate p = cb.equal(root.get( StatisticDepartmentForDay_.organizationName), name);
		if( date == null || date.isEmpty() ){
			logger.error( new StatisticDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( StatisticDepartmentForDay_.statisticDate), date));
		}
		cq.select(root.get( StatisticDepartmentForDay_.id));
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
	public List<StatisticDepartmentForDay> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticDepartmentForDay wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForDay.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+StatisticDepartmentForDay.class.getCanonicalName()+" o where 1=1" );

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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticDepartmentForDay.class );
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
	public List<StatisticDepartmentForDay> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticDepartmentForDay wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForDay.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+StatisticDepartmentForDay.class.getCanonicalName()+" o where 1=1" );
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticDepartmentForDay.class );
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
	public long getCountWithFilter( WrapInFilterStatisticDepartmentForDay wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( StatisticDepartmentForDay.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+StatisticDepartmentForDay.class.getCanonicalName()+" o where 1=1" );
		
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
		
		Query query = em.createQuery( sql_stringBuffer.toString(), StatisticDepartmentForDay.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}
}