package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.x.attendance.assemble.control.jaxrs.attendanceappealinfo.WrapInFilterAppeal;
import com.x.attendance.entity.AttendanceAdmin;
import com.x.attendance.entity.AttendanceAdmin_;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceAppealInfo_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
/**
 * 系统配置信息表基础功能服务类

 */
public class AttendanceAppealInfoFactory extends AbstractFactory {
	
	public AttendanceAppealInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的AttendanceAppealInfo信息对象")
	public AttendanceAppealInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceAppealInfo.class, ExceptionWhen.none);
	}

	public List<AttendanceAppealInfo> listWithDetailId(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceAppealInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAppealInfo> cq = cb.createQuery(AttendanceAppealInfo.class);
		Root<AttendanceAppealInfo> root = cq.from( AttendanceAppealInfo.class);
		Predicate p = cb.equal( root.get(AttendanceAppealInfo_.detailId),  id );
		return em.createQuery(cq.where(p)).getResultList();
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
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAppealInfo> cq = cb.createQuery(AttendanceAppealInfo.class);
		Root<AttendanceAppealInfo> root = cq.from(AttendanceAppealInfo.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceAppealInfo_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceAppealInfo_.detailId));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(AttendanceAppealInfo_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(AttendanceAppealInfo_.sequence),sequence.toString()));
			}
		}
		if(StringUtils.isNotEmpty(wrapIn.getDetailId())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.detailId),wrapIn.getDetailId()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getEmpName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.empName),wrapIn.getEmpName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.unitName),wrapIn.getUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getTopUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.topUnitName),wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getYearString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.yearString),wrapIn.getYearString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getMonthString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.monthString),wrapIn.getMonthString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getAppealReason())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.appealReason),wrapIn.getAppealReason()));
		}
		if (wrapIn.getStatus()!=999) {
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.status),wrapIn.getStatus()));
		}
		//添加OR条件
		/*if (wrapIn.getOrAtrribute() != null && wrapIn.getOrAtrribute().size() > 0) {
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
		}*/
		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}

	/**
	 * 查询下一页的信息数据--只查询当前人有权限审批的
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AttendanceAppealInfo> listIdsNextWithFilterWithCurrentProcessor( String id, Integer count, Object sequence, WrapInFilterAppeal wrapIn ,Boolean isManager) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAppealInfo> cq = cb.createQuery(AttendanceAppealInfo.class);
		Root<AttendanceAppealInfo> root = cq.from(AttendanceAppealInfo.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceAppealInfo_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceAppealInfo_.detailId));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(AttendanceAppealInfo_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(AttendanceAppealInfo_.sequence),sequence.toString()));
			}
		}
		if(StringUtils.isNotEmpty(wrapIn.getDetailId())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.detailId),wrapIn.getDetailId()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getEmpName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.empName),wrapIn.getEmpName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.unitName),wrapIn.getUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getTopUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.topUnitName),wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getYearString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.yearString),wrapIn.getYearString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getMonthString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.monthString),wrapIn.getMonthString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getAppealReason())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.appealReason),wrapIn.getAppealReason()));
		}
		if (wrapIn.getStatus()!=999) {
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.status),wrapIn.getStatus()));
		}

		if(!isManager){
			if ((null != wrapIn.getProcessPerson1()) && (!wrapIn.getProcessPerson1().isEmpty())) {
				p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.currentProcessor),wrapIn.getProcessPerson1()));
			}
		}

		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}

	/**
	 * 查询下一页的信息数据--按管理员组织
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AttendanceAppealInfo> listIdsNextWithFilterWithManager( String id, Integer count, Object sequence, WrapInFilterAppeal wrapIn ,Map<String,List<String>> unitMap,Boolean isManager) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		Boolean unitFlag = false;
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAppealInfo> cq = cb.createQuery(AttendanceAppealInfo.class);
		Root<AttendanceAppealInfo> root = cq.from(AttendanceAppealInfo.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceAppealInfo_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceAppealInfo_.detailId));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(AttendanceAppealInfo_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(AttendanceAppealInfo_.sequence),sequence.toString()));
			}
		}
		if(StringUtils.isNotEmpty(wrapIn.getProcessPerson1())) {
			p = cb.and(p, cb.equal(root.get(AttendanceAppealInfo_.currentProcessor), wrapIn.getProcessPerson1()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getDetailId())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.detailId),wrapIn.getDetailId()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getEmpName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.empName),wrapIn.getEmpName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.unitName),wrapIn.getUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getTopUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.topUnitName),wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getYearString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.yearString),wrapIn.getYearString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getMonthString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.monthString),wrapIn.getMonthString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getAppealReason())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.appealReason),wrapIn.getAppealReason()));
		}
		if (wrapIn.getStatus()!=999) {
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.status),wrapIn.getStatus()));
		}
		// 这个管理员应该不需要 以前的错误逻辑 2022-1-11 @FancyLou
//		if(!isManager){
//			if(unitMap.isEmpty()){
//				p = cb.and(p,cb.isNull(root.get(AttendanceAppealInfo_.detailId)));
//			}else{
//				if(unitMap.containsKey("COMPANY")){
//					p = cb.and(p,root.get(AttendanceAppealInfo_.topUnitName).in(unitMap.get("COMPANY")));
//				}
//				if(unitMap.containsKey("DEPT")){
//					p = cb.and(p,root.get(AttendanceAppealInfo_.unitName).in(unitMap.get("DEPT")));
//				}
//			}
//		}
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
	public List<AttendanceAppealInfo> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterAppeal wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAppealInfo> cq = cb.createQuery(AttendanceAppealInfo.class);
		Root<AttendanceAppealInfo> root = cq.from(AttendanceAppealInfo.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceAppealInfo_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceAppealInfo_.detailId));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.greaterThan(root.get(AttendanceAppealInfo_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.lessThan(root.get(AttendanceAppealInfo_.sequence),sequence.toString()));
			}
		}
		if(StringUtils.isNotEmpty(wrapIn.getDetailId())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.detailId),wrapIn.getDetailId()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getEmpName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.empName),wrapIn.getEmpName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.unitName),wrapIn.getUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getTopUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.topUnitName),wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getYearString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.yearString),wrapIn.getYearString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getMonthString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.monthString),wrapIn.getMonthString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getAppealReason())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.appealReason),wrapIn.getAppealReason()));
		}
		if (wrapIn.getStatus()!=999) {
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.status),wrapIn.getStatus()));
		}
		//添加OR条件
		/*if (wrapIn.getOrAtrribute() != null && wrapIn.getOrAtrribute().size() > 0) {
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
		}*/
		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
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
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceAppealInfo> root = cq.from(AttendanceAppealInfo.class);
		Predicate p = cb.isNotNull(root.get(AttendanceAppealInfo_.detailId));

		if(StringUtils.isNotEmpty(wrapIn.getDetailId())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.detailId),wrapIn.getDetailId()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getEmpName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.empName),wrapIn.getEmpName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.unitName),wrapIn.getUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getTopUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.topUnitName),wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getYearString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.yearString),wrapIn.getYearString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getMonthString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.monthString),wrapIn.getMonthString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getAppealReason())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.appealReason),wrapIn.getAppealReason()));
		}
		if (wrapIn.getStatus()!=999) {
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.status),wrapIn.getStatus()));
		}

		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 查询符合的文档信息总数
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilterWithCurrentProcessor(WrapInFilterAppeal wrapIn , boolean isManager) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceAppealInfo> root = cq.from(AttendanceAppealInfo.class);
		Predicate p = cb.isNotNull(root.get(AttendanceAppealInfo_.detailId));

		if(StringUtils.isNotEmpty(wrapIn.getDetailId())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.detailId),wrapIn.getDetailId()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getEmpName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.empName),wrapIn.getEmpName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.unitName),wrapIn.getUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getTopUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.topUnitName),wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getYearString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.yearString),wrapIn.getYearString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getMonthString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.monthString),wrapIn.getMonthString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getAppealReason())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.appealReason),wrapIn.getAppealReason()));
		}
		if (wrapIn.getStatus()!=999) {
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.status),wrapIn.getStatus()));
		}
		if(!isManager){
			if ((null != wrapIn.getProcessPerson1()) && (!wrapIn.getProcessPerson1().isEmpty())) {
				p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.currentProcessor),wrapIn.getProcessPerson1()));
			}
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 查询符合的文档信息总数
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilterWithManager(WrapInFilterAppeal wrapIn , Map<String,List<String>> unitMap, boolean isManager) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceAppealInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceAppealInfo> root = cq.from(AttendanceAppealInfo.class);
		Predicate p = cb.isNotNull(root.get(AttendanceAppealInfo_.detailId));


		if(StringUtils.isNotEmpty(wrapIn.getProcessPerson1())) {
			p = cb.and(p, cb.equal(root.get(AttendanceAppealInfo_.currentProcessor), wrapIn.getProcessPerson1()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getDetailId())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.detailId),wrapIn.getDetailId()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getEmpName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.empName),wrapIn.getEmpName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.unitName),wrapIn.getUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getTopUnitName())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.topUnitName),wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getYearString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.yearString),wrapIn.getYearString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getMonthString())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.monthString),wrapIn.getMonthString()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getAppealReason())){
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.appealReason),wrapIn.getAppealReason()));
		}
		if (wrapIn.getStatus()!=999) {
			p = cb.and(p,cb.equal(root.get(AttendanceAppealInfo_.status),wrapIn.getStatus()));
		}
		// 这个管理员应该不需要 以前的错误逻辑 2022-1-11 @FancyLou
//		if(!isManager){
//			if(unitMap.isEmpty()){
//				p = cb.and(p,cb.isNull(root.get(AttendanceAppealInfo_.detailId)));
//			}else{
//				if(unitMap.containsKey("COMPANY")){
//					p = cb.and(p,root.get(AttendanceAppealInfo_.topUnitName).in(unitMap.get("COMPANY")));
//				}
//				if(unitMap.containsKey("DEPT")){
//					p = cb.and(p,root.get(AttendanceAppealInfo_.unitName).in(unitMap.get("DEPT")));
//				}
//			}
//		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Map<String,List<String>> listUnits(EffectivePerson effectivePerson)  throws Exception {
		Map<String,List<String>> unitMap=new HashMap<>();
		List<AttendanceAdmin> unitlist = new ArrayList<>();
		List<String> companyList = new ArrayList<String>();
		List<String> deptList = new ArrayList<String>();
		EntityManager em = this.entityManagerContainer().get( AttendanceAdmin.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAdmin> cq = cb.createQuery(AttendanceAdmin.class);
		Root<AttendanceAdmin> root = cq.from( AttendanceAdmin.class);
		Predicate p = cb.equal(root.get(AttendanceAdmin_.admin),effectivePerson.getDistinguishedName());
		unitlist =  em.createQuery(cq.where(p)).getResultList();
		if(ListTools.isNotEmpty(unitlist)){
			for(AttendanceAdmin attendanceAdmin :unitlist){
				if(StringUtils.equals(attendanceAdmin.getAdminLevel(),"DEPT")){
					deptList.add(attendanceAdmin.getUnitName());
				}
				if(StringUtils.equals(attendanceAdmin.getAdminLevel(),"COMPANY")){
					companyList.add(attendanceAdmin.getUnitName());
				}
			}
		}
		if(ListTools.isNotEmpty(deptList)){
			unitMap.put("DEPT",deptList);
		}
		if(ListTools.isNotEmpty(companyList)){
			unitMap.put("COMPANY",companyList);
		}
		return  unitMap;
	}
}