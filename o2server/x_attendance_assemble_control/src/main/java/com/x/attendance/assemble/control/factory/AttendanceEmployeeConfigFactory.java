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
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceEmployeeConfig_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 员工考勤需求配置服务器
 */
public class AttendanceEmployeeConfigFactory extends AbstractFactory {
	
	public AttendanceEmployeeConfigFactory(Business business) throws Exception {
		super(business);
	}

//	@MethodDescribe("获取指定Id的AttendanceEmployeeConfig信息对象")
	public AttendanceEmployeeConfig get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceEmployeeConfig.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("列示全部的AttendanceEmployeeConfig信息列表")
	@SuppressWarnings("unused")
	public List<AttendanceEmployeeConfig> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceEmployeeConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceEmployeeConfig> cq = cb.createQuery(AttendanceEmployeeConfig.class);
		Root<AttendanceEmployeeConfig> root = cq.from( AttendanceEmployeeConfig.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的AttendanceEmployeeConfig信息列表")
	public List<AttendanceEmployeeConfig> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceEmployeeConfig>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceEmployeeConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceEmployeeConfig> cq = cb.createQuery(AttendanceEmployeeConfig.class);
		Root<AttendanceEmployeeConfig> root = cq.from(AttendanceEmployeeConfig.class);
		Predicate p = root.get(AttendanceEmployeeConfig_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("根据配置类别列示AttendanceEmployeeConfig信息列表")
	public List<String> listByConfigType( String configType ) throws Exception {
		if( configType == null || configType.isEmpty()){
			return new ArrayList<String>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceEmployeeConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceEmployeeConfig> root = cq.from(AttendanceEmployeeConfig.class);
		Predicate p = cb.equal(root.get(AttendanceEmployeeConfig_.configType), configType);	
		cq.select(root.get(AttendanceEmployeeConfig_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}	
}