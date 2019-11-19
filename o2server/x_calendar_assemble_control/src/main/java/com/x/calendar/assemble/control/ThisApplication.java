package com.x.calendar.assemble.control;

import java.util.List;

import com.x.base.core.project.Context;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.schedule.AlarmTrigger;
import com.x.calendar.assemble.control.schedule.CheckEventComment;
import com.x.calendar.assemble.control.service.UserManagerService;
import com.x.calendar.core.entity.Calendar;
import com.x.calendar.core.entity.Calendar_Event;

public class ThisApplication {

	protected static Context context;
	public static final String CalendarMANAGER = "CalendarManager";

	public static Context context() {
		return context;
	}

	public static void init() throws Exception {
		try {
			MessageConnector.start(context());
			//每30秒检查一次需要推送的消息
			context.schedule(AlarmTrigger.class, "0/30 * * * * ?");
			//每两小时检查一次comment信息的引用情况，删除多余的不必要的数据
			context.schedule(CheckEventComment.class, "* * */2 * * ?");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断用户是否有Calendar系统管理权限 1、系统管理员Manager或者xadmin 2、拥有CalendarManager角色的人员
	 * 
	 * @param effectivePerson
	 * @return
	 */
	public static Boolean isCalendarSystemManager(EffectivePerson effectivePerson) {
		UserManagerService userManagerService = new UserManagerService();
		try {
			if (userManagerService.isHasPlatformRole(effectivePerson.getDistinguishedName(),
					ThisApplication.CalendarMANAGER) || effectivePerson.isManager()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 判断用户是否拥有指定日历的管理权限
	 * 
	 * @param effectivePerson
	 * @param calendar
	 * @return
	 */
	public static Boolean isCalendarManager(EffectivePerson effectivePerson, Calendar calendar) {
		if (isCalendarSystemManager(effectivePerson)) {
			return true;
		}
		if (calendar != null) {
			// 判断管理权限
			if (calendar.getCreateor().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
				return true;
			}
			if (ListTools.isNotEmpty(calendar.getManageablePersonList())
					&& calendar.getManageablePersonList().contains(effectivePerson.getDistinguishedName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断用户是否拥有指定日历的日程事件发布权限
	 * 
	 * @param effectivePerson
	 * @param calendar
	 * @return
	 */
	public static Boolean isCalendarPublisher(EffectivePerson effectivePerson, List<String> unitNames,
			List<String> groupNames, Calendar calendar) {
		if (isCalendarSystemManager(effectivePerson)) {
			return true;
		}
		if (calendar != null) {
			if (isCalendarManager(effectivePerson, calendar)) {
				return true;
			}
			// 判断发布权限
			String personName = effectivePerson.getDistinguishedName();
			if (ListTools.isNotEmpty(calendar.getPublishablePersonList())
					&& calendar.getPublishablePersonList().contains(personName)) {
				return true;
			}
			if (ListTools.isNotEmpty(calendar.getPublishableUnitList()) && ListTools.isNotEmpty(unitNames)) {
				for (String publishUnitName : calendar.getPublishableUnitList()) {
					for (String unitName : unitNames) {
						if (unitName.equalsIgnoreCase(publishUnitName)) {
							return true;
						}
					}
				}
			}
			if (ListTools.isNotEmpty(calendar.getPublishableGroupList()) && ListTools.isNotEmpty(groupNames)) {
				for (String publishGroupName : calendar.getPublishableGroupList()) {
					for (String groupName : groupNames) {
						if (groupName.equalsIgnoreCase(publishGroupName)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 判断用户是否拥有指定日历的日程事件发布权限
	 * 
	 * @param effectivePerson
	 * @param calendar
	 * @return
	 * @throws Exception
	 */
	public static Boolean isCalendarPublisher(EffectivePerson effectivePerson, Calendar calendar) throws Exception {
		if (isCalendarSystemManager(effectivePerson)) {
			return true;
		}
		List<String> unitNames = null;
		List<String> groupNames = null;
		if (calendar != null) {
			if (isCalendarManager(effectivePerson, calendar)) {
				return true;
			}
			UserManagerService userManagerService = new UserManagerService();
			// 判断发布权限
			String personName = effectivePerson.getDistinguishedName();
			unitNames = userManagerService.listUnitNamesWithPerson(personName);
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			if (ListTools.isNotEmpty(calendar.getPublishablePersonList())
					&& calendar.getPublishablePersonList().contains(personName)) {
				return true;
			}
			if (ListTools.isNotEmpty(calendar.getPublishableUnitList()) && ListTools.isNotEmpty(unitNames)) {
				for (String publishUnitName : calendar.getPublishableUnitList()) {
					for (String unitName : unitNames) {
						if (unitName.equalsIgnoreCase(publishUnitName)) {
							return true;
						}
					}
				}
			}
			if (ListTools.isNotEmpty(calendar.getPublishableGroupList()) && ListTools.isNotEmpty(groupNames)) {
				for (String publishGroupName : calendar.getPublishableGroupList()) {
					for (String groupName : groupNames) {
						if (groupName.equalsIgnoreCase(publishGroupName)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 判断用户是否拥有指定日程事件的管理权限
	 * 
	 * @param effectivePerson
	 * @param event
	 * @return
	 * @throws Exception
	 */
	public static Boolean isEventManager(EffectivePerson effectivePerson, Calendar calendar, Calendar_Event event)
			throws Exception {
		if (isCalendarSystemManager(effectivePerson)) {
			return true;
		}
		List<String> unitNames = null;
		List<String> groupNames = null;
		if (event != null) {
			UserManagerService userManagerService = new UserManagerService();
			String personName = effectivePerson.getDistinguishedName();
			unitNames = userManagerService.listUnitNamesWithPerson(personName);
			groupNames = userManagerService.listGroupNamesByPerson(personName);
			// 判断日历的发布权限
			if (isCalendarPublisher(effectivePerson, unitNames, groupNames, calendar)) {
				return true;
			}
			// 判断事件的管理权限
			if (ListTools.isNotEmpty(event.getManageablePersonList())
					&& calendar.getManageablePersonList().contains(personName)) {
				return true;
			}
		}
		return false;
	}
}
