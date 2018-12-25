package com.x.report.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.report.assemble.control.AbstractFactory;
import com.x.report.assemble.control.Business;
import com.x.report.core.entity.Report_S_Setting;
import com.x.report.core.entity.Report_S_Setting_;

/**
 * 系统配置信息表基础功能服务类
 * @author liyi
 */
public class Report_S_SettingFactory extends AbstractFactory {
	
	public Report_S_SettingFactory( Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("获取指定Id的Report_S_Setting应用信息对象")
	public Report_S_Setting get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Report_S_Setting.class, ExceptionWhen.none);
	}
	
	@SuppressWarnings("unused")
	//@MethodDescribe("列示全部的Report_S_Setting应用信息列表")
	public List<Report_S_Setting> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Report_S_Setting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_S_Setting> cq = cb.createQuery(Report_S_Setting.class);
		Root<Report_S_Setting> root = cq.from( Report_S_Setting.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("列示指定Id的Report_S_Setting应用信息列表")
	public List<Report_S_Setting> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<Report_S_Setting>();
		}
		EntityManager em = this.entityManagerContainer().get(Report_S_Setting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_S_Setting> cq = cb.createQuery(Report_S_Setting.class);
		Root<Report_S_Setting> root = cq.from(Report_S_Setting.class);
		Predicate p = root.get( Report_S_Setting_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("根据CODE列示指定Id的Report_S_Setting应用信息列表")
	public List<String> listIdsByCode(String code) throws Exception {
		if( code == null || code.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Report_S_Setting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Report_S_Setting> root = cq.from(Report_S_Setting.class);
		Predicate p = cb.equal(root.get(Report_S_Setting_.configCode),  code);
		cq.select(root.get(Report_S_Setting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Report_S_Setting getWithConfigCode( String configCode ) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			return null;
		}
		Report_S_Setting attendanceSetting = null;
		List<Report_S_Setting> settingList = null;
		EntityManager em = this.entityManagerContainer().get(Report_S_Setting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Report_S_Setting> cq = cb.createQuery(Report_S_Setting.class);
		Root<Report_S_Setting> root = cq.from(Report_S_Setting.class);
		Predicate p = cb.equal(root.get( Report_S_Setting_.configCode ),  configCode );
		settingList = em.createQuery(cq.where(p)).getResultList();
		if( settingList != null && settingList.size() > 0 ){
			attendanceSetting = settingList.get(0);
		}
		return attendanceSetting;
	}
	
}