package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSConfigSetting;
import com.x.bbs.entity.BBSConfigSetting_;


/**
 * 类   名：BBSConfigSettingFactory<br/>
 * 实体类：BBSConfigSetting<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class BBSConfigSettingFactory extends AbstractFactory {

	public BBSConfigSettingFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSConfigSetting实体信息对象" )
	public BBSConfigSetting get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, BBSConfigSetting.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "列示全部的BBSConfigSetting实体信息列表" )
	@SuppressWarnings("unused")
	public List<BBSConfigSetting> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSConfigSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSConfigSetting> cq = cb.createQuery(BBSConfigSetting.class);
		Root<BBSConfigSetting> root = cq.from( BBSConfigSetting.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "列示指定Id的BBSConfigSetting实体信息列表" )
	public List<BBSConfigSetting> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSConfigSetting>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSConfigSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSConfigSetting> cq = cb.createQuery(BBSConfigSetting.class);
		Root<BBSConfigSetting> root = cq.from(BBSConfigSetting.class);
		Predicate p = root.get(BBSConfigSetting_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "根据指定的配置编码获取配置的值" )
	public String getValueWithConfigCode(String configCode) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			throw new Exception( "config code is null, can not find any system setting!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSConfigSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSConfigSetting> root = cq.from(BBSConfigSetting.class);
		Predicate p = cb.equal( root.get( BBSConfigSetting_.configCode ), configCode );
		cq.select(root.get(BBSConfigSetting_.configValue));
		List<String> valueList = em.createQuery(cq.where(p)).getResultList();
		if( ListTools.isNotEmpty(valueList) ){
			return valueList.get(0);
		}
		return null;
	}

	public BBSConfigSetting getWithConfigCode(String configCode) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			throw new Exception( "config code is null, can not find any system setting!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSConfigSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSConfigSetting> cq = cb.createQuery(BBSConfigSetting.class);
		Root<BBSConfigSetting> root = cq.from(BBSConfigSetting.class);
		Predicate p = cb.equal( root.get( BBSConfigSetting_.configCode ), configCode );
		List<BBSConfigSetting> valueList = em.createQuery(cq.where(p)).getResultList();
		if( ListTools.isNotEmpty(valueList) ){
			return valueList.get(0);
		}
		return null;
	}
}
