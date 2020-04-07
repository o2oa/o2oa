package com.x.calendar.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster;


/**
 * 日程日历信息服务类
 * @author O2LEE
 *
 */
public class Calendar_EventRepeatMasterServiceAdv{

	private Calendar_EventRepeatMasterService calendar_EventRepeatMasterService = new Calendar_EventRepeatMasterService();

	public List<Calendar_EventRepeatMaster> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventRepeatMasterService.list(emc, ids);
		} catch ( Exception e ) {
			throw e;
		}
	}	
	
	/**
	 * 根据ID获取指定日历记录信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventRepeatMaster get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Calendar_EventRepeatMaster.class);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 保存日历记录信息
	 * @param calendar_record
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventRepeatMaster save( Calendar_EventRepeatMaster calendar_record ) throws Exception {
		Calendar_EventRepeatMaster calendar_record_old = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			calendar_record_old = business.calendar_EventRepeatMasterFactory().get( calendar_record.getId() );
			if( calendar_record_old != null ){
				calendar_record.setId( calendar_record_old.getId() );
				return calendar_EventRepeatMasterService.update( emc, calendar_record, true );	
			}else{
				return calendar_EventRepeatMasterService.create( emc, calendar_record, true );	
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据条件查询指定的日历记录信息ID列表
	 * @param title
	 * @param type
	 * @param source
	 * @param createor
	 * @param inFilterCalendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( String title, String type, String source, String createor, List<String> inFilterCalendarIds, 
			String personName, List<String> unitNames, List<String> groupNames, Date startTime, Date endTime ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendar_EventRepeatMasterService.listWithCondition(emc, title, type, source, createor, inFilterCalendarIds, personName, unitNames, 
					 groupNames, startTime, endTime );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据条件查询指定的日历记录信息ID列表
	 * @param inFilterCalendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( List<String> inFilterCalendarIds, 
			String personName, List<String> unitNames, List<String> groupNames, Date startTime, Date endTime ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendar_EventRepeatMasterService.listWithCondition(emc, null, null, null, null, inFilterCalendarIds, personName, unitNames, 
					 groupNames, startTime, endTime );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据ID彻底删除指定的日历记录信息
	 * @param id
	 * @throws Exception
	 */
	public void destoryWithMasterId(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 calendar_EventRepeatMasterService.destoryWithMasterId(emc, id, null );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 检查该repeatMaster下所有的记录是不是全都是已经删除了，如果没有有效的记录的话，就全部删除掉
	 * @param repeatMasterId
	 * @throws Exception
	 */
	public void checkRepeatMaster(String repeatMasterId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendar_EventRepeatMasterService.checkRepeatMaster( emc, repeatMasterId );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
