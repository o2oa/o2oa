package com.x.calendar.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar;


/**
 * 日程日历信息服务类
 * @author O2LEE
 *
 */
public class CalendarServiceAdv{

	private CalendarService calendarInfoService = new CalendarService();
	
	/**
	 * 根据用户获取用户可以访问到的所有日历日历信息
	 * @return
	 * @throws Exception
	 */
	public List<Calendar> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.listAll( emc );	
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<Calendar> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.list(emc, ids);
		} catch ( Exception e ) {
			throw e;
		}
	}	
	
	/**
	 * 根据ID获取指定日历日历信息
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Calendar.class);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 保存日历信息
	 * @param calendar
	 * @param effectivePerson 
	 * @return
	 * @throws Exception
	 */
	public Calendar save( Calendar calendar, EffectivePerson effectivePerson ) throws Exception {
		Calendar calendar_old = null;
		Business business = null;
		
		if( StringUtils.isEmpty( calendar.getCreateor() )) {
			calendar.setCreateor( effectivePerson.getDistinguishedName() );
		}
		
		//管理者应该都有可见权限
		if( ListTools.isNotEmpty( calendar.getManageablePersonList() )) {
			for( String managerName : calendar.getManageablePersonList() ) {
				addStringToList( managerName, effectivePerson, calendar.getViewablePersonList() );
			}
		}
		
		//对日历信息进行权限设置，至少自己创建的日历自己可以管理 ，可以发布，可以查看
		addStringToList( calendar.getCreateor(), effectivePerson, calendar.getManageablePersonList() );
		addStringToList( calendar.getCreateor(), effectivePerson, calendar.getPublishablePersonList() );
		addStringToList( calendar.getCreateor(), effectivePerson, calendar.getViewablePersonList() );
		
		if( "UNIT".equals( calendar.getType() )) {
			if( ListTools.isEmpty(calendar.getViewableUnitList() )
				&& ListTools.isEmpty(calendar.getViewableGroupList() )
				&& ListTools.isEmpty(calendar.getViewablePersonList() )){
				addStringToList( calendar.getTarget(), effectivePerson, calendar.getViewableUnitList() );
			}
		}else {
			addStringToList( calendar.getTarget(), effectivePerson, calendar.getViewablePersonList() );
		}		
		
		if( StringUtils.isEmpty( calendar.getColor() )) {
			calendar.setColor( "#1462be" );
		}
		if( StringUtils.isEmpty( calendar.getType() )) {
			calendar.setType("PERSON");
		}else {
			calendar.setType( calendar.getType().toUpperCase() );
		}
		if( StringUtils.isEmpty( calendar.getSource() )) {
			calendar.setSource("PERSON");
		}else {
			calendar.setSource( calendar.getSource().toUpperCase() );
		}
		if( "OPEN".equalsIgnoreCase( calendar.getStatus() ) || StringUtils.isEmpty(  calendar.getStatus() )) {
			calendar.setStatus( "OPEN" );
		}else {
			calendar.setStatus( "CLOSE" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			calendar_old = business.calendarFactory().get( calendar.getId() );
			if( calendar_old != null ){
				calendar.setId( calendar_old.getId() );
				return calendarInfoService.update( emc, calendar );	
			}else{
				return calendarInfoService.create( emc, calendar );	
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 将一个字符串添加到一个List里
	 * @param distinguishedName
	 * @param manageablePersonList
	 */
	private void addStringToList(String distinguishedName, EffectivePerson effectivePerson, List<String> manageablePersonList) {
		if( manageablePersonList == null ) {
			manageablePersonList = new ArrayList<>();
		}
		if( StringUtils.equalsAnyIgnoreCase( distinguishedName, "SYSTEM")){
			distinguishedName = effectivePerson.getDistinguishedName();
		}
		if( StringUtils.isNotEmpty( distinguishedName ) ) {
			if( !manageablePersonList.contains( distinguishedName )) {
				manageablePersonList.add( distinguishedName );
			}
		}
	}

	/**
	 * 根据条件查询指定的日历信息ID列表
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
	public List<String> listWithCondition( String name, String type, String source, String createor,
			 List<String> inFilterCalendarIds, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendarInfoService.listWithCondition(emc, name, type, source, createor, inFilterCalendarIds, 
					 personName, unitNames, groupNames);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据条件查询指定的日历信息ID列表（忽略权限）
	 * @param name
	 * @param type
	 * @param source
	 * @param createor
	 * @param inFilterCalendarIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( String name, String type, String source, String createor,
			 List<String> inFilterCalendarIds ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendarInfoService.listWithCondition(emc, name, type, source, createor, inFilterCalendarIds, 
					 null, null, null );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据权限查询日历信息ID列表
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listMyCalendarIds( String personName ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendarInfoService.listMyCalenders(emc, personName);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据权限查询日历信息ID列表
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			 return calendarInfoService.listWithCondition(emc, null, null, null, null, null, personName, unitNames, groupNames);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据Id删除日历信息，要删除日历中所有的记录信息以及重复主体信息
	 * @param id
	 * @throws Exception 
	 */
	public void destory(String id) throws Exception {
		if(StringUtils.isEmpty( id )) {
			throw new Exception("id is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendarInfoService.destory(emc, id);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listPublicCalendar() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.listPublicCalendar(emc);
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 关注一个日历
	 * @param effectivePerson 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Boolean follow( EffectivePerson effectivePerson, String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.follow(emc, effectivePerson.getDistinguishedName(), id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 取消关注一个日历
	 * @param effectivePerson 
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public Boolean followCancel( EffectivePerson effectivePerson, String id ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendarInfoService.followCancel(emc, effectivePerson.getDistinguishedName(), id );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
