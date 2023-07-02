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
import com.x.attendance.entity.AttendanceAdmin;
import com.x.attendance.entity.AttendanceAdmin_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 系统配置信息表基础功能服务类
 */
public class AttendanceAdminFactory extends AbstractFactory {
	
	public AttendanceAdminFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的AttendanceAdmin应用信息对象")
	public AttendanceAdmin get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceAdmin.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的AttendanceAdmin应用信息列表")
	@SuppressWarnings("unused")
	public List<AttendanceAdmin> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceAdmin.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAdmin> cq = cb.createQuery(AttendanceAdmin.class);
		Root<AttendanceAdmin> root = cq.from( AttendanceAdmin.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的AttendanceAdmin应用信息列表")
	public List<AttendanceAdmin> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceAdmin>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceAdmin.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceAdmin> cq = cb.createQuery(AttendanceAdmin.class);
		Root<AttendanceAdmin> root = cq.from(AttendanceAdmin.class);
		Predicate p = root.get(AttendanceAdmin_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}	
}