package com.x.calendar.assemble.control.factory;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.AbstractFactory;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster_;
import com.x.calendar.core.tools.CriteriaBuilderTools;


/**
 * 日历任务记录信息表功能服务类
 * @author O2LEE
 */
public class Calendar_EventRepeatMasterFactory extends AbstractFactory {
	
	public Calendar_EventRepeatMasterFactory( Business business) throws Exception {
		super(business);
	}

	/**
	 * 获取指定Id的日历任务记录信息对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventRepeatMaster get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Calendar_EventRepeatMaster.class );
	}
	
	/**
	 * 列示指定Id的日历任务记录信息列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_EventRepeatMaster> list(List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_EventRepeatMaster.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Calendar_EventRepeatMaster> cq = cb.createQuery(Calendar_EventRepeatMaster.class);
		Root<Calendar_EventRepeatMaster> root = cq.from(Calendar_EventRepeatMaster.class);
		Predicate p = root.get( Calendar_EventRepeatMaster_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listWithCalendarId(String calendarId) throws Exception {
		if( StringUtils.isEmpty( calendarId ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_EventRepeatMaster.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_EventRepeatMaster> root = cq.from(Calendar_EventRepeatMaster.class);
		Predicate p = cb.equal( root.get( Calendar_EventRepeatMaster_.calendarId), calendarId);
		cq.select(root.get(Calendar_EventRepeatMaster_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 根据条件查询指定的日历信息ID列表
	 * @param title
	 * @param eventType
	 * @param source
	 * @param createPerson
	 * @param inFilterCalendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( String title, String eventType, String source, String createPerson, List<String> inFilterCalendarIds, 
			 String personName, List<String> unitNames, List<String> groupNames, Date startTime, Date endTime ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar_EventRepeatMaster.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_EventRepeatMaster> root = cq.from(Calendar_EventRepeatMaster.class);
		
		Predicate p = null;
		if( ListTools.isNotEmpty( inFilterCalendarIds )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, root.get(Calendar_EventRepeatMaster_.calendarId).in( inFilterCalendarIds ));
		}
		if( StringUtils.isNotEmpty( title )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_EventRepeatMaster_.title), title));
		}
		if( StringUtils.isNotEmpty( eventType )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_EventRepeatMaster_.eventType), eventType));
		}
		if( StringUtils.isNotEmpty( source )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_EventRepeatMaster_.source), source));
		}
		if( StringUtils.isNotEmpty( createPerson )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_EventRepeatMaster_.createPerson), createPerson));
		}
		if( startTime != null ) {
			if( endTime == null ) {
				throw new Exception("endTime is null!");
			}else {
				p = CriteriaBuilderTools.predicate_and( cb, p, cb.lessThanOrEqualTo( root.get(Calendar_EventRepeatMaster_.startTime), endTime ));
				p = CriteriaBuilderTools.predicate_and( cb, p, cb.greaterThanOrEqualTo( root.get(Calendar_EventRepeatMaster_.endTime), startTime ));
			}
		}
		Predicate permission = null;
		if( StringUtils.isNotEmpty( personName )) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_EventRepeatMaster_.manageablePersonList)));
			permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_EventRepeatMaster_.viewablePersonList)));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_EventRepeatMaster_.viewableUnitList).in( unitNames ));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_EventRepeatMaster_.viewableGroupList).in( groupNames ));		
		}		
		p = CriteriaBuilderTools.predicate_and( cb, p, permission );
		cq.select(root.get(Calendar_EventRepeatMaster_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 查询需要生成日历事件的重复主体信息ID列表
	 * @param calendarIds
	 * @param eventType
	 * @param createMonth
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedRepeatMaster(List<String> calendarIds, String eventType, String createMonth,
			String personName, List<String> unitNames, List<String> groupNames) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar_EventRepeatMaster.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_EventRepeatMaster> root = cq.from(Calendar_EventRepeatMaster.class);
		
		Predicate p = cb.equal( root.get(Calendar_EventRepeatMaster_.repeatStatus ), "等待生成" );
		
		if( ListTools.isNotEmpty( calendarIds )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, root.get(Calendar_EventRepeatMaster_.calendarId).in( calendarIds ));
		}
		if( StringUtils.isNotEmpty( eventType )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_EventRepeatMaster_.eventType), eventType));
		}
		if( StringUtils.isNotEmpty( createMonth )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, 
					cb.isNotMember( createMonth, root.get(Calendar_EventRepeatMaster_.createdMonthList) )
			);
		}
		
		Predicate permission = null;
		if( StringUtils.isNotEmpty( personName )) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_EventRepeatMaster_.manageablePersonList)));
			permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_EventRepeatMaster_.viewablePersonList)));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_EventRepeatMaster_.viewableUnitList).in( unitNames ));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_EventRepeatMaster_.viewableGroupList).in( groupNames ));		
		}
		
		p = CriteriaBuilderTools.predicate_and( cb, p, permission );
		cq.select(root.get(Calendar_EventRepeatMaster_.id));
		
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}
}