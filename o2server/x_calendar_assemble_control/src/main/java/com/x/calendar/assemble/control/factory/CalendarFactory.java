package com.x.calendar.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.AbstractFactory;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_;
import com.x.calendar.core.tools.CriteriaBuilderTools;


/**
 * 日历账户信息表功能服务类
 * @author O2LEE
 */
public class CalendarFactory extends AbstractFactory {
	
	public CalendarFactory( Business business) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的日历账户配置信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Calendar.class );
	}
	
	/**
	 * 列示全部的日历账户配置信息列表
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public List<Calendar> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Calendar> cq = cb.createQuery(Calendar.class);
		Root<Calendar> root = cq.from( Calendar.class);
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 列示指定Id的日历账户配置信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Calendar> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<Calendar>();
		}
		EntityManager em = this.entityManagerContainer().get(Calendar.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Calendar> cq = cb.createQuery(Calendar.class);
		Root<Calendar> root = cq.from(Calendar.class);
		Predicate p = root.get( Calendar_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据条件查询指定的日历信息ID列表
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param name
	 * @param type
	 * @param source
	 * @param createor
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( String name, String type, String source, String createor, List<String> inFilterCalendarIds,
			 String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar> root = cq.from(Calendar.class);

		Predicate p = null;
		if( ListTools.isNotEmpty( inFilterCalendarIds )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, root.get(Calendar_.id).in( inFilterCalendarIds ));
		}
		if( StringUtils.isNotEmpty( name )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_.name), name));
		}
		if( StringUtils.isNotEmpty( type )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_.type), type));
		}
		if( StringUtils.isNotEmpty( source )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_.source), source));
		}
		if( StringUtils.isNotEmpty( createor )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.or(
					cb.equal(root.get(Calendar_.createor), createor),
					cb.equal(root.get(Calendar_.target), createor)
			));
		}		
		
		Predicate permission = null;
		if( StringUtils.isNotEmpty( personName )) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_.manageablePersonList)));
			permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_.publishablePersonList)));
			permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_.viewablePersonList)));
			permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_.followers)));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_.publishableUnitList).in( unitNames ));
			permission = CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_.viewableUnitList).in( unitNames ));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_.publishableGroupList).in( groupNames ));
			permission = CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_.viewableGroupList).in( groupNames ));		
		}
		//permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isTrue( root.get(Calendar_.isPublic) ));
		p = CriteriaBuilderTools.predicate_and( cb, p, permission );
		cq.select(root.get(Calendar_.id));
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 查询我自己（可管理）的日历
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listMyCalender( String personName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar> root = cq.from(Calendar.class);
		Predicate permission = cb.or(
				cb.equal(root.get(Calendar_.createor), personName),
				cb.equal(root.get(Calendar_.target), personName)
		);
		cq.select(root.get(Calendar_.id));
		return em.createQuery(cq.where(permission)).getResultList();
	}

	public List<String> listPublicCalendar() throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar> root = cq.from(Calendar.class);
		Predicate permission = cb.isTrue( root.get(Calendar_.isPublic) );
		cq.select(root.get(Calendar_.id));
		return em.createQuery(cq.where(permission)).getResultList();
	}	
}