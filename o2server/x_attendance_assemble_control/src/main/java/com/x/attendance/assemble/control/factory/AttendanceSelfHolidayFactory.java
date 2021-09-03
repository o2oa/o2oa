package com.x.attendance.assemble.control.factory;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.CriteriaQueryTools;
import com.x.attendance.assemble.control.jaxrs.selfholiday.ActionListNextWithFilter;
import com.x.attendance.assemble.control.jaxrs.selfholiday.WrapInFilter;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceSelfHoliday_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 系统配置信息表基础功能服务类
 */
public class AttendanceSelfHolidayFactory extends AbstractFactory {
	
	public AttendanceSelfHolidayFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的AttendanceSelfHoliday信息对象")
	public AttendanceSelfHoliday get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceSelfHoliday.class, ExceptionWhen.none);
	}
	
//@MethodDescribe("列示全部的AttendanceSelfHoliday信息列表")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceSelfHoliday> root = cq.from( AttendanceSelfHoliday.class);
		cq.select(root.get(AttendanceSelfHoliday_.id));
		return em.createQuery(cq).getResultList();
	}
	
//	@MethodDescribe("列示指定Id的AttendanceSelfHoliday信息列表")
	public List<AttendanceSelfHoliday> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceSelfHoliday>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceSelfHoliday> cq = cb.createQuery(AttendanceSelfHoliday.class);
		Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
		Predicate p = root.get(AttendanceSelfHoliday_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<AttendanceSelfHoliday> listWithBatchFlag( String batchFlag ) throws Exception {
		if( StringUtils.isEmpty( batchFlag )){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceSelfHoliday> cq = cb.createQuery(AttendanceSelfHoliday.class);
		Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
		Predicate p = cb.equal( root.get(AttendanceSelfHoliday_.batchFlag), batchFlag );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listIdsWithBatchFlag( String batchFlag ) throws Exception {
		if( StringUtils.isEmpty( batchFlag )){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
		Predicate p = cb.equal( root.get(AttendanceSelfHoliday_.batchFlag), batchFlag );
		cq.select(root.get(AttendanceSelfHoliday_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("列示单个员工的AttendanceSelfHoliday信息列表")
	public List<String> getByEmployeeName(String empName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceSelfHoliday> root = cq.from( AttendanceSelfHoliday.class);
		Predicate p = root.get(AttendanceSelfHoliday_.employeeName).in( empName );
		cq.select(root.get(AttendanceSelfHoliday_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByStartDateAndEndDate(Date startDate, Date endDate) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceSelfHoliday> root = cq.from( AttendanceSelfHoliday.class);
		Predicate p = cb.between(root.get(AttendanceSelfHoliday_.startTime), startDate, endDate);
		p = cb.and( p, cb.between(root.get(AttendanceSelfHoliday_.endTime), startDate, endDate));
		cq.select(root.get(AttendanceSelfHoliday_.id));
		return em.createQuery(cq.where(p)).getResultList();
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
	public List<AttendanceSelfHoliday> listIdsNextWithFilter( String id, Integer count, Object sequence, ActionListNextWithFilter.WrapIn wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceSelfHoliday.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceSelfHoliday> cq = cb.createQuery(AttendanceSelfHoliday.class);
		Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceSelfHoliday_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceSelfHoliday_.employeeName));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(AttendanceSelfHoliday_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(AttendanceSelfHoliday_.sequence),sequence.toString()));
			}
		}
		if(StringUtils.isNotEmpty(wrapIn.getQ_empName())){
			p = cb.and(p,cb.equal(root.get(AttendanceSelfHoliday_.employeeName),wrapIn.getQ_empName()));
		}
		if(ListTools.isNotEmpty(wrapIn.getUnitNames())){
			p = cb.and(p,cb.equal(root.get(AttendanceSelfHoliday_.unitOu),wrapIn.getUnitNames().get(0)));
		}
		if(ListTools.isNotEmpty(wrapIn.getTopUnitNames())){
			p = cb.and(p,cb.equal(root.get(AttendanceSelfHoliday_.topUnitOu),wrapIn.getTopUnitNames().get(0)));
		}
		if (null != wrapIn.getStartdate() && null != wrapIn.getEnddate()) {
			p = cb.and(p,cb.greaterThanOrEqualTo(root.get(AttendanceSelfHoliday_.startTime),wrapIn.getStartdate()));
			p = cb.and(p,cb.lessThanOrEqualTo(root.get(AttendanceSelfHoliday_.endTime),wrapIn.getEnddate()));
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
	public List<AttendanceSelfHoliday> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceSelfHoliday.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceSelfHoliday> cq = cb.createQuery(AttendanceSelfHoliday.class);
		Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceSelfHoliday_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceSelfHoliday_.employeeName));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.greaterThan(root.get(AttendanceSelfHoliday_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.lessThan(root.get(AttendanceSelfHoliday_.sequence),sequence.toString()));
			}
		}
		if(StringUtils.isNotEmpty(wrapIn.getQ_empName())){
			p = cb.and(p,cb.equal(root.get(AttendanceSelfHoliday_.employeeName),wrapIn.getQ_empName()));
		}
		if(ListTools.isNotEmpty(wrapIn.getUnitNames())){
			p = cb.and(p,cb.equal(root.get(AttendanceSelfHoliday_.unitOu),wrapIn.getUnitNames().get(0)));
		}
		if(ListTools.isNotEmpty(wrapIn.getTopUnitNames())){
			p = cb.and(p,cb.equal(root.get(AttendanceSelfHoliday_.topUnitOu),wrapIn.getTopUnitNames().get(0)));
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
	public long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		//先获取上一页最后一条的sequence值，如果有值的话，以此sequence值作为依据取后续的count条数据
		EntityManager em = this.entityManagerContainer().get( AttendanceSelfHoliday.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceSelfHoliday> root = cq.from(AttendanceSelfHoliday.class);
		Predicate p = cb.isNotNull(root.get(AttendanceSelfHoliday_.employeeName));
		if(StringUtils.isNotEmpty(wrapIn.getQ_empName())){
			p = cb.and(p,cb.equal(root.get(AttendanceSelfHoliday_.employeeName),wrapIn.getQ_empName()));
		}
		if(ListTools.isNotEmpty(wrapIn.getUnitNames())){
			p = cb.and(p,cb.equal(root.get(AttendanceSelfHoliday_.unitOu),wrapIn.getUnitNames().get(0)));
		}
		if(ListTools.isNotEmpty(wrapIn.getTopUnitNames())){
			p = cb.and(p,cb.equal(root.get(AttendanceSelfHoliday_.topUnitOu),wrapIn.getTopUnitNames().get(0)));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> getByPersonName(String personName) throws Exception {
		if( personName == null || personName.isEmpty() ) {
			throw new Exception("personName is null.");
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceSelfHoliday.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceSelfHoliday> root = cq.from( AttendanceSelfHoliday.class);
		Predicate p = cb.equal(root.get(AttendanceSelfHoliday_.employeeName ), personName );
		cq.select(root.get(AttendanceSelfHoliday_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}