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
import com.x.attendance.entity.AttendanceWorkPlace;
import com.x.attendance.entity.AttendanceWorkPlace_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 工作场所信息服务类
 * @author liyi
 */
public class AttendanceWorkPlaceFactory extends AbstractFactory {
	
	public AttendanceWorkPlaceFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的AttendanceWorkPlace应用信息对象")
	public AttendanceWorkPlace get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceWorkPlace.class, ExceptionWhen.none);
	}
	
//	@MethodDescribe("列示全部的AttendanceWorkPlace应用信息列表")
	@SuppressWarnings("unused")
	public List<AttendanceWorkPlace> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkPlace.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceWorkPlace> cq = cb.createQuery(AttendanceWorkPlace.class);
		Root<AttendanceWorkPlace> root = cq.from( AttendanceWorkPlace.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的AttendanceWorkPlace应用信息列表")
	public List<AttendanceWorkPlace> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceWorkPlace>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkPlace.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceWorkPlace> cq = cb.createQuery(AttendanceWorkPlace.class);
		Root<AttendanceWorkPlace> root = cq.from(AttendanceWorkPlace.class);
		Predicate p = root.get(AttendanceWorkPlace_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}	
}