package com.x.calendar.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.common.date.DateOperation;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster;


/**
 * 日历记录信息服务类
 * @author O2LEE
 *
 */
public class Calendar_EventService {
	
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 根据ID彻底删除指定的日历记录信息
	 * @param id
	 * @throws Exception
	 */
	public void destory( EntityManagerContainer emc, String id ) throws Exception {
		Calendar_Event calendar_Event = null;
		if( StringUtils.isEmpty( id ) ){
			throw new Exception( "id is empty, system can not delete any object." );
		}
		calendar_Event = emc.find( id, Calendar_Event.class );
		if ( null == calendar_Event ) {
			throw new Exception( "object is not exist {'id':'"+ id +"'}" );
		}else{
			emc.beginTransaction( Calendar_Event.class );
			if(calendar_Event != null  ) {
				emc.remove( calendar_Event, CheckRemoveType.all );
			}
			emc.commit();
		}
	}

	public void destory(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ListTools.isNotEmpty( ids )) {
			Calendar_Event calendar_Event = null;
			emc.beginTransaction( Calendar_Event.class );
			for(String id : ids ) {
				if( StringUtils.isNotEmpty( id ) ){
					calendar_Event = emc.find( id, Calendar_Event.class );
					if ( null != calendar_Event ) {
						if(calendar_Event != null  ) {
							emc.remove( calendar_Event, CheckRemoveType.all );
						}
					}
				}
			}
			emc.commit();
		}
	}
	
	/**
	 * 创建日历记录信息
	 * @param emc
	 * @param calendar_record
	 * @param calendar_EventDetail 
	 * @param autoTransaction - 是否自动提交
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event create( EntityManagerContainer emc,  Calendar_Event calendar_record,
			Boolean autoTransaction ) throws Exception {
		if( autoTransaction == null ) {
			autoTransaction = true;
		}
		Calendar_Event calendar_record_old = null;
		calendar_record_old = emc.find( calendar_record.getId(), Calendar_Event.class );
		if( calendar_record_old != null ){
			throw new Exception("calendar_record{'id':' "+ calendar_record.getId() +" '} exists, can not create new object");
		}else{
			if( autoTransaction ) {
				emc.beginTransaction( Calendar_Event.class );
			}
			emc.persist( calendar_record, CheckPersistType.all);	
			if( autoTransaction ) {
				emc.commit();
			}
		}
		return calendar_record;
	}
	
	/**
	 * 更新日历记录信息
	 * @param emc
	 * @param calendar_record
	 * @param autoTransaction  - 是否自动提交
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event update( EntityManagerContainer emc, Calendar_Event calendar_record, Boolean autoTransaction ) throws Exception {
		if( calendar_record == null ){
			throw new Exception("calendar_record is null, can not update object!");
		}
		if( autoTransaction == null ) {
			autoTransaction = true;
		}
		Calendar_Event calendar_record_old = null;
		Business business = new Business(emc);
		calendar_record_old = business.calendar_EventFactory().get( calendar_record.getId() );
		if( calendar_record_old != null ){
			if( autoTransaction ) {
				emc.beginTransaction( Calendar_Event.class );
			}			
			calendar_record.copyTo(calendar_record_old, JpaObject.FieldsUnmodify);
			emc.check( calendar_record_old, CheckPersistType.all);
			if( autoTransaction ) {
				emc.commit();
			}
		}else{
			throw new Exception("old object calendar_record{'id':' "+ calendar_record.getId() +" '} is not exists. ");
		}
		return calendar_record_old;
	}

	/**
	 * 根据条件查询指定的日历信息ID列表
	 * @param emc
	 * @param key
	 * @param eventType
	 * @param source
	 * @param createPerson
	 * @param calendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( EntityManagerContainer emc,  String key, String eventType, String source, String createPerson,
			 List<String> calendarIds, String personName, List<String> unitNames, List<String> groupNames, Date startTime, Date endTime
			 ) throws Exception {
		Business business =  new Business( emc );
		return business.calendar_EventFactory().listWithCondition( key, eventType, source, createPerson, calendarIds, personName, 
				unitNames, groupNames, startTime, endTime);
	}
	
	/**
	 * 根据重复主体以及日期落雷查询指定的日历记录信息ID列表
	 * @param emc
	 * @param repeatMasterId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithRepeatMaster( EntityManagerContainer emc,  String repeatMasterId, Date startTime, Date endTime ) throws Exception {
		Business business =  new Business( emc );
		return business.calendar_EventFactory().listWithRepeatMaster(repeatMasterId, startTime, endTime);
	}

	public List<Calendar_Event> list(EntityManagerContainer emc,  List<String> ids) throws Exception {
		Business business =  new Business( emc );
		return business.calendar_EventFactory().list(ids);
	}

	/**
	 * 根据日期范围为指定的日期重复主体生成日历事件信息
	 * @param emc
	 * @param repeatMasterIds
	 * @param startTime
	 * @param endTime
	 * @param needCreateMonths 
	 * @return
	 * @throws Exception 
	 */
	public List<String> createCalendarWithMaster( EntityManagerContainer emc, List<String> repeatMasterIds, Date startTime, Date endTime, List<String> needCreateMonths ) throws Exception {
		List<String> event_ids = new ArrayList<>();
		if( ListTools.isNotEmpty( repeatMasterIds )) {
			Business business =  new Business( emc );
			List<Calendar_Event> calendarEvents = null;
			Calendar_EventRepeatMaster eventRepeatMaster = null;
			for( String repeatMasterId : repeatMasterIds ) {
				eventRepeatMaster = emc.find( repeatMasterId , Calendar_EventRepeatMaster.class );
				if( eventRepeatMaster != null ) {
					calendarEvents = eventRepeatMaster.getRecurringDatesInPeriod( startTime, endTime );
				}
				if( ListTools.isNotEmpty( calendarEvents )) {
					for( Calendar_Event calendar_Event : calendarEvents ) {
						//判断该事件是否已经存在，如果不存在，则进行数据添加
						if ( !business.calendar_EventFactory().eventExists( calendar_Event ) ) {							
							calendar_Event.setCreatePerson( eventRepeatMaster.getCreatePerson() );
							calendar_Event.addParticipants( eventRepeatMaster.getCreatePerson() );
							calendar_Event.setRepeatMasterId(eventRepeatMaster.getId());							
		
							//计算提醒时间，以开始时间为基准
							if( calendar_Event.getAlarm() ) {
								String[] alarm_config = calendar_Event.getValarmTime_config().trim().split(","); 
								if( alarm_config.length == 4 ) {
									int day, hour, min, sec = 0;
									Date alarmTime = null;
									try {
										day = Integer.parseInt( alarm_config[0].trim() );
										hour = Integer.parseInt( alarm_config[01].trim() );
										min = Integer.parseInt( alarm_config[2].trim() );
										sec = Integer.parseInt( alarm_config[3].trim() );
										alarmTime = dateOperation.caculateNewDate( calendar_Event.getStartTime(), day, hour, min, sec );
										calendar_Event.setAlarmTime(alarmTime);
									}catch( Exception e ) {
										calendar_Event.setAlarm( false );
										calendar_Event.setValarmTime_config( "0,0,0,0" );
									}				 
								}else {
									calendar_Event.setAlarm( false );
									calendar_Event.setValarmTime_config( "0,0,0,0" );
								}
							}							
							event_ids.add( calendar_Event.getId() );
							
							emc.beginTransaction( Calendar_Event.class );
							emc.persist( calendar_Event, CheckPersistType.all  );
							emc.commit();
						}
					}
					
					if( ListTools.isNotEmpty( needCreateMonths )) {
						for( String date : needCreateMonths ) {
							eventRepeatMaster.addCreatedMonth( date );	
						}
					}
					
					emc.beginTransaction( Calendar_EventRepeatMaster.class );
					emc.check( eventRepeatMaster, CheckPersistType.all );
					emc.commit();
				}
			}
		}
		return event_ids;
	}
	
	/**
	 * 查询需要提醒的日程事件列表
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public List<String> listNeedAlarmEventIds(EntityManagerContainer emc, Date date) throws Exception {
		Business business =  new Business( emc );
		return business.calendar_EventFactory().listNeedAlarmEventIds( date );
	}

	public List<String> destoryWithBundle(EntityManagerContainer emc, String bundle) throws Exception {
		if( StringUtils.isEmpty( bundle ) ) {
			throw new Exception("bundle is null!");
		}
		Business business =  new Business( emc );
		List<String> ids = business.calendar_EventFactory().listWithBundle( bundle );
		if( ListTools.isEmpty( ids )) {
			return new ArrayList<>();
		}else {
			List<Calendar_Event> events = business.calendar_EventFactory().list(ids);
			for( Calendar_Event event : events ) {
				emc.beginTransaction( Calendar_Event.class );
				emc.remove( event, CheckRemoveType.all );
				emc.commit();
			}
			return ids;
		}
	}	
}
