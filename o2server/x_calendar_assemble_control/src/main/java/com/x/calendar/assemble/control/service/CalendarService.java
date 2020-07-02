package com.x.calendar.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster;


/**
 * 日历账户信息服务类
 * @author O2LEE
 *
 */
public class CalendarService {

	/**
	 * 获取所有的日历账户信息
	 * @return
	 * @throws Exception
	 */
	public List<Calendar> listAll( EntityManagerContainer emc ) throws Exception {
		Business business =  new Business( emc );
		return business.calendarFactory().listAll();
	}

	/**
	 * 根据ID删除指定的日历账户信息
	 * @param id
	 * @throws Exception
	 */
	public void destory( EntityManagerContainer emc, String id ) throws Exception {
		Calendar calendar = null;
		if( StringUtils.isEmpty( id ) ){
			throw new Exception( "id is empty, system can not delete any object." );
		}
		calendar = emc.find( id, Calendar.class );
		if ( null == calendar ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			List<String> ids = null;
			Business business = new Business(emc);
			emc.beginTransaction( Calendar.class );
			emc.beginTransaction( Calendar_Event.class );
			emc.beginTransaction( Calendar_EventRepeatMaster.class );
			
			//删除所有的日历记录信息Calendar_Event
			List<Calendar_Event> eventList = null;
			ids = business.calendar_EventFactory().listWithCalendarId(id);
			if( ListTools.isNotEmpty( ids )) {
				eventList = business.calendar_EventFactory().list(ids);
				if( ListTools.isNotEmpty( eventList )) {
					for( Calendar_Event event : eventList ) {
						emc.remove( event, CheckRemoveType.all );
					}
				}
			}
			
			//删除所有的重复主要依据信息Calendar_EventRepeatMaster
			List<Calendar_EventRepeatMaster> repeatedMasterList = null;
			ids = business.calendar_EventRepeatMasterFactory().listWithCalendarId(id);
			if( ListTools.isNotEmpty( ids )) {
				repeatedMasterList = business.calendar_EventRepeatMasterFactory().list(ids);
				if( ListTools.isNotEmpty( repeatedMasterList )) {
					for( Calendar_EventRepeatMaster repeatedMaster : repeatedMasterList ) {
						emc.remove( repeatedMaster, CheckRemoveType.all );
					}
				}
			}
			
			//删除日历信息
			emc.remove( calendar, CheckRemoveType.all );
			emc.commit();
		}
	}

	/**
	 * 创建日历账户信息
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public Calendar create( EntityManagerContainer emc, Calendar calendar ) throws Exception {
		Calendar calendar_old = null;
		calendar_old = emc.find( calendar.getId(), Calendar.class );
		if( calendar_old != null ){
			throw new Exception("calendar{'id':' "+ calendar.getId() +" '} exists, can not create new object");
		}else{
			emc.beginTransaction( Calendar.class );
			emc.persist( calendar, CheckPersistType.all);
			emc.commit();
		}
		return calendar;
	}
	
	/**
	 * 更新日历账户信息
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public Calendar update( EntityManagerContainer emc, Calendar calendar ) throws Exception {
		if( calendar == null ){
			throw new Exception("calendar is null, can not update object!");
		}
		Calendar calendar_old = null;
		Business business = new Business(emc);
		calendar_old = business.calendarFactory().get( calendar.getId() );
		if( calendar_old != null ){
			emc.beginTransaction( Calendar.class );
			calendar_old.setName( calendar.getName() );
			calendar_old.setColor( calendar.getColor() );
			calendar_old.setCreateor( calendar.getCreateor() );
			calendar_old.setManageablePersonList( calendar.getManageablePersonList() );
			calendar_old.setPublishableGroupList( calendar.getPublishableGroupList() );
			calendar_old.setPublishablePersonList( calendar.getPublishablePersonList() );
			calendar_old.setPublishableUnitList( calendar.getPublishableUnitList() );
			calendar_old.setViewableGroupList( calendar.getViewableGroupList() );
			calendar_old.setViewablePersonList( calendar.getViewablePersonList() );
			calendar_old.setViewableUnitList( calendar.getViewableUnitList() );
			calendar_old.setTarget( calendar.getTarget() );
			calendar_old.setDescription( calendar.getDescription() );
			calendar_old.setIsPublic( calendar.getIsPublic() );
			calendar_old.setStatus( calendar.getStatus() );
			emc.check( calendar_old, CheckPersistType.all);
			emc.commit();
		}else{
			throw new Exception("old object calendar{'id':' "+ calendar.getId() +" '} is not exists. ");
		}
		return calendar_old;
	}

	/**
	 * 查询用户创建的日历
	 * @param emc
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listMyCalenders( EntityManagerContainer emc, String personName ) throws Exception {
		Business business =  new Business( emc );
		return business.calendarFactory().listMyCalender( personName );
	}
	
	/**
	 * 根据条件查询指定的日历信息ID列表
	 * @param emc
	 * @param name
	 * @param type
	 * @param source
	 * @param createor
	 * @param inFilterCalendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( EntityManagerContainer emc,  String name, String type, String source, String createor,
			 List<String> inFilterCalendarIds, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		Business business =  new Business( emc );

		//同步，避免前端的连续调用导致多条个人日历
		List<String> ids = business.calendarFactory().listMyCalender( personName );
		if( ListTools.isEmpty( ids ) ) {
			//创建一个自己的默认日历
			Calendar calendar = new Calendar();
			calendar.setSource( "PERSON" );
			calendar.setCreateor( "SYSTEM" );
			calendar.setColor( "#1462be" );
			calendar.setDescription( "我的默认日历" );
			calendar.setId( Calendar.createId() );
			calendar.setIsPublic( false );
			calendar.setName( "我的日历" );
			calendar.setSource("PERSON");
			calendar.setType("PERSON");
			calendar.setTarget( personName );
			calendar.setManageablePersonList( new ArrayList<>() );
			calendar.setPublishablePersonList( new ArrayList<>() );
			calendar.setViewablePersonList( new ArrayList<>() );

			addStringToList( personName, calendar.getManageablePersonList() );
			addStringToList( personName, calendar.getPublishablePersonList() );
			addStringToList( personName, calendar.getViewablePersonList() );

			queryForAddNewPersonalDefaultCalendar(emc, personName,calendar );

		}
		return business.calendarFactory().listWithCondition( name, type, source, createor, inFilterCalendarIds, personName, unitNames, groupNames );
	}

	/**
	 * 同步方法，避免前端的连续调用导致多条个人日历信息
	 * @param emc
	 * @param personName
	 * @param calendar
	 * @throws Exception
	 */
	private synchronized void queryForAddNewPersonalDefaultCalendar(EntityManagerContainer emc, String personName, Calendar calendar) throws Exception {
		Business business =  new Business( emc );
		List<String> ids = business.calendarFactory().listMyCalender( personName );
		if( ListTools.isEmpty( ids )){
			emc.beginTransaction( Calendar.class );
			emc.persist( calendar, CheckPersistType.all );
			emc.commit();
		}
	}

	/**
	 * 将一个字符串添加到一个List里
	 * @param distinguishedName
	 * @param manageablePersonList
	 */
	private void addStringToList(String distinguishedName, List<String> manageablePersonList) {
		if( manageablePersonList == null ) {
			manageablePersonList = new ArrayList<>();
		}
		if( StringUtils.isNotEmpty( distinguishedName ) ) {
			if( !manageablePersonList.contains( distinguishedName )) {
				manageablePersonList.add( distinguishedName );
			}
		}
	}
	
	public List<Calendar> list(EntityManagerContainer emc,  List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.calendarFactory().list(ids);
	}

	public List<String> listPublicCalendar(EntityManagerContainer emc) throws Exception {
		Business business =  new Business( emc );
		return business.calendarFactory().listPublicCalendar();
	}

	/**
	 * 关注一个日历
	 * @param emc
	 * @param distinguishedName
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Boolean follow(EntityManagerContainer emc, String followerName, String id) throws Exception {
		Calendar calendar = emc.find( id, Calendar.class );
		if( calendar != null ) {
			emc.beginTransaction( Calendar.class );
			calendar.addFollower(followerName);
			emc.check( calendar, CheckPersistType.all );
			emc.commit();
			return true;
		}
		return false;
	}
	
	/**
	 * 取消关注一个日历
	 * @param emc
	 * @param distinguishedName
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Boolean followCancel(EntityManagerContainer emc, String followerName, String id) throws Exception {
		List<String> followerNames = new ArrayList<>();
		Calendar calendar = emc.find( id, Calendar.class );
		if( calendar != null ) {
			emc.beginTransaction( Calendar.class );
			if( ListTools.isNotEmpty( calendar.getFollowers() )) {
				for( String _followerName : calendar.getFollowers() ) {
					if( !followerName.equals( _followerName )) {
						followerNames.add( _followerName );
					}
				}
			}
			calendar.setFollowers( followerNames );
			emc.check( calendar, CheckPersistType.all );
			emc.commit();
			return true;
		}
		return false;
	}
}
