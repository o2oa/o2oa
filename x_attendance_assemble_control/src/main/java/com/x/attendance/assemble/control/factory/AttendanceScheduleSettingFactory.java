package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.AttendanceScheduleSetting_;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.utils.annotation.MethodDescribe;
/**
 * 系统配置信息表基础功能服务类
 * @author liyi
 */
public class AttendanceScheduleSettingFactory extends AbstractFactory {
	
	public AttendanceScheduleSettingFactory(Business business) throws Exception {
		super(business);
	}

	@MethodDescribe("获取指定Id的AttendanceScheduleSetting应用信息对象")
	public AttendanceScheduleSetting get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceScheduleSetting.class, ExceptionWhen.none);
	}
	
	@MethodDescribe("列示全部的AttendanceScheduleSetting应用信息列表")
	public List<AttendanceScheduleSetting> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceScheduleSetting> cq = cb.createQuery(AttendanceScheduleSetting.class);
		Root<AttendanceScheduleSetting> root = cq.from( AttendanceScheduleSetting.class);
		return em.createQuery(cq).getResultList();
	}
	
	@MethodDescribe("列示指定Id的AttendanceScheduleSetting应用信息列表")
	public List<AttendanceScheduleSetting> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceScheduleSetting>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceScheduleSetting> cq = cb.createQuery(AttendanceScheduleSetting.class);
		Root<AttendanceScheduleSetting> root = cq.from(AttendanceScheduleSetting.class);
		Predicate p = root.get(AttendanceScheduleSetting_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@MethodDescribe("根据部门名称，查询部门排班信息对象")
	public List<String> listByDepartmentName( String departmentName ) throws Exception{		
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceScheduleSetting> root = cq.from(AttendanceScheduleSetting.class);
		Predicate p = cb.equal(root.get(AttendanceScheduleSetting_.organizationName), departmentName );
		cq.select(root.get(AttendanceScheduleSetting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@MethodDescribe("根据部门名称，查询部门排班信息对象")
	public List<String> listByDepartmentNames( List<String> departmentNameList ) throws Exception{		
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceScheduleSetting> root = cq.from(AttendanceScheduleSetting.class);
		Predicate p = root.get(AttendanceScheduleSetting_.organizationName).in(departmentNameList);
		cq.select(root.get(AttendanceScheduleSetting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	@MethodDescribe("根据公司名称，查询公司排班信息对象")
	public List<String> listByCompanyName( String companyName ) throws Exception{		
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceScheduleSetting> root = cq.from(AttendanceScheduleSetting.class);
		Predicate p = cb.equal(root.get(AttendanceScheduleSetting_.companyName), companyName );
		cq.select(root.get(AttendanceScheduleSetting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}