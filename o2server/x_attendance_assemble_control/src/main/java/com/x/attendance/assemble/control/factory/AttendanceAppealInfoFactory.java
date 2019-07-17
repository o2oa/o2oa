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
import com.x.attendance.assemble.control.jaxrs.attendanceappealinfo.WrapInFilterAppeal;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceAppealInfo_;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 系统配置信息表基础功能服务类
 * @author liyi
 */
public class AttendanceAppealInfoFactory extends AbstractFactory {
	
	public AttendanceAppealInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的AttendanceAppealInfo信息对象")
	public AttendanceAppealInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceAppealInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的AttendanceAppealInfo信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceAppealInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		cq.select(root.get(AttendanceAppealInfo_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示全部的AttendanceAppealInfo信息列表")
	public String getMaxRecordDate() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceAppealInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAppealInfo> cq = cb.createQuery(AttendanceAppealInfo.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		cq.orderBy( cb.desc( root.get( AttendanceAppealInfo_.recordDateString) ) );	
		List<AttendanceAppealInfo> resultList = em.createQuery(cq).setMaxResults(1).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return null;
		}else{
			return resultList.get(0).getRecordDateString();
		}
	}
	
	//@MethodDescribe("根据员工姓名和打卡日期列示AttendanceAppealInfo信息列表")
	public List<String> listByEmployeeNameAndAppealDate( String employeeName, String appealDateString ) throws Exception {
		
		if( employeeName == null || appealDateString == null ){
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get(AttendanceAppealInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		cq.select(root.get(AttendanceAppealInfo_.id));
		Predicate p = cb.equal( root.get(AttendanceAppealInfo_.empName),  employeeName );
		p = cb.and( p, cb.equal( root.get(AttendanceAppealInfo_.appealDateString ),  appealDateString ) );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	//@MethodDescribe("根据组织和打卡日期列示AttendanceAppealInfo信息列表")
	public List<String> listByUnitNameAndAppealDate( String unitName, String appealDateString ) throws Exception {
		
		if( unitName == null || appealDateString == null ){
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get(AttendanceAppealInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		cq.select(root.get(AttendanceAppealInfo_.id));
		Predicate p = cb.equal( root.get(AttendanceAppealInfo_.unitName),  unitName );
		p = cb.and( p, cb.equal( root.get(AttendanceAppealInfo_.appealDateString ),  appealDateString ) );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	//@MethodDescribe("根据顶层组织和打卡日期列示AttendanceAppealInfo信息列表")
	public List<String> listByTopUnitNameAndAppealDate( String topUnitName, String appealDateString ) throws Exception {
		
		if( topUnitName == null || appealDateString == null ){
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get(AttendanceAppealInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		cq.select(root.get(AttendanceAppealInfo_.id));
		Predicate p = cb.equal( root.get(AttendanceAppealInfo_.topUnitName),  topUnitName );
		p = cb.and( p, cb.equal( root.get(AttendanceAppealInfo_.appealDateString ),  appealDateString ) );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的AttendanceAppealInfo信息列表")
	public List<AttendanceAppealInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceAppealInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceAppealInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAppealInfo> cq = cb.createQuery(AttendanceAppealInfo.class);
		Root<AttendanceAppealInfo> root = cq.from(AttendanceAppealInfo.class);
		Predicate p = root.get(AttendanceAppealInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("按年份月份查询某用户的申诉记录列表")
	public List<String> listUserAttendanceAppealInfoByYearAndMonth(String user, String year, String month)  throws Exception {
		if( user == null || user.isEmpty() ||year == null || month == null || year.isEmpty() || month.isEmpty()  ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		cq.select( root.get(AttendanceAppealInfo_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceAppealInfo_.empName), user );
		if( StringUtils.isNotEmpty( year  ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceAppealInfo_.yearString), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceAppealInfo_.monthString), month ));
		}
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("按年份月份查询某组织的申诉记录列表")
	public List<String> listUnitAttendanceAppealInfoByYearAndMonth(String unitName, String year, String month)  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		cq.select( root.get(AttendanceAppealInfo_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceAppealInfo_.unitName), unitName );
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceAppealInfo_.yearString), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceAppealInfo_.monthString), month ));
		}
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
//	@MethodDescribe("按年份月份查询某顶层组织的申诉记录列表")
	public List<String> listTopUnitAttendanceAppealInfoByYearAndMonth(String topUnitName, String year, String month)  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		cq.select( root.get(AttendanceAppealInfo_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.equal( root.get(AttendanceAppealInfo_.topUnitName), topUnitName );
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceAppealInfo_.yearString), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceAppealInfo_.monthString), month ));
		}
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("查询未归档的申诉记录列表，最大2000条")
	public List<String> listNonArchiveAppealInfoIds()  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		cq.select( root.get(AttendanceAppealInfo_.id ));
		//一般始终为true, id is not null
		Predicate p = cb.isNotNull( root.get(AttendanceAppealInfo_.archiveTime) );
		return em.createQuery(cq.where(p)).setMaxResults(2000).getResultList();
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
	public List<AttendanceAppealInfo> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterAppeal wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		Integer index = 1;
		sql_stringBuffer.append( "SELECT o FROM "+AttendanceAppealInfo.class.getCanonicalName()+" o where 1=1" );

		if ((null != sequence) ) {
			sql_stringBuffer.append(" and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? "<" : ">") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getDetailId()) && (!wrapIn.getDetailId().isEmpty())) {
			sql_stringBuffer.append(" and o.detailId = ?" + (index));
			vs.add( wrapIn.getDetailId() );
			index++;
		}
		if ((null != wrapIn.getEmpName()) && (!wrapIn.getEmpName().isEmpty())) {
			sql_stringBuffer.append(" and o.empName = ?" + (index));
			vs.add( wrapIn.getEmpName() );
			index++;
		}
		if ((null != wrapIn.getUnitName()) && (!wrapIn.getUnitName().isEmpty())) {
			sql_stringBuffer.append(" and o.unitName = ?" + (index));
			vs.add( wrapIn.getUnitName() );
			index++;
		}
		if ((null != wrapIn.getTopUnitName()) && (!wrapIn.getTopUnitName().isEmpty())) {
			sql_stringBuffer.append(" and o.topUnitName = ?" + (index));
			vs.add( wrapIn.getTopUnitName() );
			index++;
		}
		if ((null != wrapIn.getYearString() ) && (!wrapIn.getYearString().isEmpty())) {
			sql_stringBuffer.append(" and o.yearString = ?" + (index));
			vs.add( wrapIn.getYearString() );
			index++;
		}
		if ((null != wrapIn.getMonthString()) && (!wrapIn.getMonthString().isEmpty())) {
			sql_stringBuffer.append(" and o.monthString = ?" + (index));
			vs.add( wrapIn.getMonthString() );
			index++;
		}
		if (wrapIn.getStatus()!=999) {
			sql_stringBuffer.append(" and o.status = ?" + (index));
			vs.add( wrapIn.getStatus() );
			index++;
		}
		if ((null != wrapIn.getAppealReason()) && (!wrapIn.getAppealReason().isEmpty())) {
			sql_stringBuffer.append(" and o.appealReason = ?" + (index));
			vs.add( wrapIn.getAppealReason() );
			index++;
		}
		if ((null != wrapIn.getProcessPerson1()) && (!wrapIn.getProcessPerson1().isEmpty())) {
			sql_stringBuffer.append(" and o.processPerson1 = ?" + (index));
			vs.add( wrapIn.getProcessPerson1() );
			index++;
		}
		if ((null != wrapIn.getProcessPerson2()) && (!wrapIn.getProcessPerson2().isEmpty())) {
			sql_stringBuffer.append(" and o.processPerson2 = ?" + (index));
			vs.add( wrapIn.getProcessPerson2() );
			index++;
		}
		
		//添加OR条件
		if (wrapIn.getOrAtrribute() != null && wrapIn.getOrAtrribute().size() > 0) {
			sql_stringBuffer.append(" and (");
			NameValueCountPair nameValueCountPair = null;
			for (int p = 0; p < wrapIn.getOrAtrribute().size(); p++) {
				nameValueCountPair = wrapIn.getOrAtrribute().get(p);
				if (p == 0) {
					sql_stringBuffer.append(" o." + nameValueCountPair.getName() + " = ?" + (index));

				} else {
					sql_stringBuffer.append(" or o." + nameValueCountPair.getName() + " = ?" + (index));
				}
				vs.add(nameValueCountPair.getValue());
				index++;
			}
			sql_stringBuffer.append(" )");
		}
		
		if( StringUtils.isNotEmpty( wrapIn.getKey() )){
			sql_stringBuffer.append(" order by o."+wrapIn.getKey()+" " + order );
		}else{
			sql_stringBuffer.append(" order by o.sequence " + order );
		}
	
		Query query = em.createQuery( sql_stringBuffer.toString(), AttendanceAppealInfo.class );
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
	public List<AttendanceAppealInfo> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterAppeal wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		String order = wrapIn.getOrder();//排序方式
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		
		sql_stringBuffer.append( "SELECT o FROM "+AttendanceAppealInfo.class.getCanonicalName()+" o where 1=1" );
		if ((null != sequence) ) {
			sql_stringBuffer.append(" and o.sequence " + (StringUtils.equalsIgnoreCase(order, "DESC") ? ">" : "<") + (" ?" + (index)));
			vs.add(sequence);
			index++;
		}
		if ((null != wrapIn.getDetailId()) && (!wrapIn.getDetailId().isEmpty())) {
			sql_stringBuffer.append(" and o.detailId = ?" + (index));
			vs.add( wrapIn.getDetailId() );
			index++;
		}
		if ((null != wrapIn.getEmpName()) && (!wrapIn.getEmpName().isEmpty())) {
			sql_stringBuffer.append(" and o.empName = ?" + (index));
			vs.add( wrapIn.getEmpName() );
			index++;
		}
		if ((null != wrapIn.getUnitName()) && (!wrapIn.getUnitName().isEmpty())) {
			sql_stringBuffer.append(" and o.unitName = ?" + (index));
			vs.add( wrapIn.getUnitName() );
			index++;
		}
		if ((null != wrapIn.getTopUnitName()) && (!wrapIn.getTopUnitName().isEmpty())) {
			sql_stringBuffer.append(" and o.topUnitName = ?" + (index));
			vs.add( wrapIn.getTopUnitName() );
			index++;
		}
		if ((null != wrapIn.getYearString() ) && (!wrapIn.getYearString().isEmpty())) {
			sql_stringBuffer.append(" and o.yearString = ?" + (index));
			vs.add( wrapIn.getYearString() );
			index++;
		}
		if ((null != wrapIn.getMonthString()) && (!wrapIn.getMonthString().isEmpty())) {
			sql_stringBuffer.append(" and o.monthString = ?" + (index));
			vs.add( wrapIn.getMonthString() );
			index++;
		}
		if (wrapIn.getStatus()!=999) {
			sql_stringBuffer.append(" and o.status = ?" + (index));
			vs.add( wrapIn.getStatus() );
			index++;
		}
		if ((null != wrapIn.getAppealReason()) && (!wrapIn.getAppealReason().isEmpty())) {
			sql_stringBuffer.append(" and o.appealReason = ?" + (index));
			vs.add( wrapIn.getAppealReason() );
			index++;
		}
		if ((null != wrapIn.getProcessPerson1()) && (!wrapIn.getProcessPerson1().isEmpty())) {
			sql_stringBuffer.append(" and o.processPerson1 = ?" + (index));
			vs.add( wrapIn.getProcessPerson1() );
			index++;
		}
		if ((null != wrapIn.getProcessPerson2()) && (!wrapIn.getProcessPerson2().isEmpty())) {
			sql_stringBuffer.append(" and o.processPerson2 = ?" + (index));
			vs.add( wrapIn.getProcessPerson2() );
			index++;
		}
		//添加OR
		if( wrapIn.getOrAtrribute() != null && wrapIn.getOrAtrribute().size() > 0){
			sql_stringBuffer.append(" and (" );
			NameValueCountPair nameValueCountPair = null;
			for( int p = 0 ; p< wrapIn.getOrAtrribute().size(); p++ ){
				nameValueCountPair = wrapIn.getOrAtrribute().get(p);
				if( p == 0 ){
					sql_stringBuffer.append(" o."+nameValueCountPair.getName()+" = ?" + (index));
					
				}else{
					sql_stringBuffer.append(" or o."+nameValueCountPair.getName()+" = ?" + (index));					
				}
				vs.add( nameValueCountPair.getValue() );
				index++;
			}
			sql_stringBuffer.append(" )" );
		}
		
		if( StringUtils.isNotEmpty( wrapIn.getKey() )){
			sql_stringBuffer.append(" order by o."+wrapIn.getKey()+" " + order );
		}else{
			sql_stringBuffer.append(" order by o.sequence " + order );
		}
		
		Query query = em.createQuery( sql_stringBuffer.toString(), AttendanceAppealInfo.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}
		
		return query.setMaxResults(20).getResultList();
	}

	/**
	 * 查询符合的文档信息总数
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilterAppeal wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		List<Object> vs = new ArrayList<>();
		StringBuffer sql_stringBuffer = new StringBuffer();
		Integer index = 1;
		
		sql_stringBuffer.append( "SELECT count(o.id) FROM "+AttendanceAppealInfo.class.getCanonicalName()+" o where 1=1" );
		
		if ((null != wrapIn.getDetailId()) && (!wrapIn.getDetailId().isEmpty())) {
			sql_stringBuffer.append(" and o.detailId = ?" + (index));
			vs.add( wrapIn.getDetailId() );
			index++;
		}
		if ((null != wrapIn.getEmpName()) && (!wrapIn.getEmpName().isEmpty())) {
			sql_stringBuffer.append(" and o.empName = ?" + (index));
			vs.add( wrapIn.getEmpName() );
			index++;
		}
		if ((null != wrapIn.getUnitName()) && (!wrapIn.getUnitName().isEmpty())) {
			sql_stringBuffer.append(" and o.unitName = ?" + (index));
			vs.add( wrapIn.getUnitName() );
			index++;
		}
		if ((null != wrapIn.getTopUnitName()) && (!wrapIn.getTopUnitName().isEmpty())) {
			sql_stringBuffer.append(" and o.topUnitName = ?" + (index));
			vs.add( wrapIn.getTopUnitName() );
			index++;
		}
		if ((null != wrapIn.getYearString() ) && (!wrapIn.getYearString().isEmpty())) {
			sql_stringBuffer.append(" and o.yearString = ?" + (index));
			vs.add( wrapIn.getYearString() );
			index++;
		}
		if ((null != wrapIn.getMonthString()) && (!wrapIn.getMonthString().isEmpty())) {
			sql_stringBuffer.append(" and o.monthString = ?" + (index));
			vs.add( wrapIn.getMonthString() );
			index++;
		}
		if (wrapIn.getStatus()!=999) {
			sql_stringBuffer.append(" and o.status = ?" + (index));
			vs.add( wrapIn.getStatus() );
			index++;
		}
		if ((null != wrapIn.getAppealReason()) && (!wrapIn.getAppealReason().isEmpty())) {
			sql_stringBuffer.append(" and o.appealReason = ?" + (index));
			vs.add( wrapIn.getAppealReason() );
			index++;
		}
		if ((null != wrapIn.getProcessPerson1()) && (!wrapIn.getProcessPerson1().isEmpty())) {
			sql_stringBuffer.append(" and o.processPerson1 = ?" + (index));
			vs.add( wrapIn.getProcessPerson1() );
			index++;
		}
		if ((null != wrapIn.getProcessPerson2()) && (!wrapIn.getProcessPerson2().isEmpty())) {
			sql_stringBuffer.append(" and o.processPerson2 = ?" + (index));
			vs.add( wrapIn.getProcessPerson2() );
			index++;
		}
		//添加OR
		if (wrapIn.getOrAtrribute() != null && wrapIn.getOrAtrribute().size() > 0) {
			sql_stringBuffer.append(" and (");
			NameValueCountPair nameValueCountPair = null;
			for (int p = 0; p < wrapIn.getOrAtrribute().size(); p++) {
				nameValueCountPair = wrapIn.getOrAtrribute().get(p);
				if (p == 0) {
					sql_stringBuffer.append(" o." + nameValueCountPair.getName() + " = ?" + (index));

				} else {
					sql_stringBuffer.append(" or o." + nameValueCountPair.getName() + " = ?" + (index));
				}
				vs.add(nameValueCountPair.getValue());
				index++;
			}
			sql_stringBuffer.append(" )");
		}

		Query query = em.createQuery( sql_stringBuffer.toString(), AttendanceAppealInfo.class );
		//为查询设置所有的参数值
		for (int i = 0; i < vs.size(); i++) {
			query.setParameter(i + 1, vs.get(i));
		}		
		return (Long) query.getSingleResult();
	}
}