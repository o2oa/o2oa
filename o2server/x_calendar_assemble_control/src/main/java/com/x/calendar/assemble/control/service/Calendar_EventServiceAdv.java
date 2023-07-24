package com.x.calendar.assemble.control.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.common.date.DateOperation;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.entity.Calendar_EventComment;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Comment;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.validate.ValidationException;

/**
 * 日程日历信息服务类
 * 
 * @author O2LEE
 *
 */
public class Calendar_EventServiceAdv {

	private DateOperation dateOperation = new DateOperation();
	private Calendar_EventService calendar_EventService = new Calendar_EventService();
	private Calendar_EventRepeatMasterService calendar_EventRepeatMasterService = new Calendar_EventRepeatMasterService();

	public List<Calendar_Event> list(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.list(emc, ids);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据ID获取指定日历记录信息
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event get(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, Calendar_Event.class);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存日历记录信息 2019-11-11
	 * 添加逻辑，适应超长的备注信息，如果备注信息超长，则将信息存储到Comment表中，并且在event和eventmaster里存储引用的ID
	 * 
	 * @param calendar_event
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event create(Calendar_Event calendar_event, EffectivePerson effectivePerson) throws Exception {

		if (StringUtils.isEmpty(calendar_event.getId())) {
			calendar_event.setId(Calendar_Event.createId());
		}

		// 对日历信息进行权限设置，至少自己创建的日历自己可以管理 ，可以发布，可以查看
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getParticipants());
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getManageablePersonList());
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getViewablePersonList());

		if (StringUtils.isEmpty(calendar_event.getCreatePerson())) {
			calendar_event.setCreatePerson(effectivePerson.getDistinguishedName());
		}
		if (StringUtils.isEmpty(calendar_event.getUpdatePerson())) {
			calendar_event.setUpdatePerson(effectivePerson.getDistinguishedName());
		}

		Calendar_EventComment calendar_EventComment = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendar_EventComment = calendar_EventService.createNewEventComment(emc, calendar_event);

			// 修改Comment信息
			if (calendar_EventComment != null) {
				calendar_event.setComment("{#CLOB#}");
				calendar_event.setCommentId(calendar_EventComment.getId());
			} else {
				calendar_event.setCommentId(null);
			}

			// 如果是复重的日程，保存为重复信息主体，否则，保存为普通日程信息
			if (StringUtils.isNotEmpty(calendar_event.getRecurrenceRule())) {
				Calendar_EventRepeatMaster calendar_EventRepeatMaster = composeEventRepeatMasterWithEvent(
						calendar_event);
				calendar_event.setRepeatMasterId(calendar_EventRepeatMaster.getId());
				emc.beginTransaction(Calendar_EventRepeatMaster.class);
				calendar_EventRepeatMasterService.create(emc, calendar_EventRepeatMaster, false);
			}

			if (StringUtils.isEmpty(calendar_event.getId())) {
				calendar_event.setId(Calendar_Event.createId());
			}
			emc.beginTransaction(Calendar_Event.class);
			// 保存日程信息
			calendar_event = calendar_EventService.create(emc, calendar_event, false);
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return calendar_event;
	}

	/**
	 * 保存日历记录信息
	 * 
	 * @param calendar_event
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event createByCipher(Calendar_Event calendar_event) throws Exception {

		if (StringUtils.isEmpty(calendar_event.getId())) {
			calendar_event.setId(Calendar_Event.createId());
		}
		String personName = calendar_event.getCreatePerson();
		// 对日历信息进行权限设置，至少自己创建的日历自己可以管理 ，可以发布，可以查看
		addStringToList(personName, calendar_event.getParticipants());
		addStringToList(personName, calendar_event.getManageablePersonList());
		addStringToList(personName, calendar_event.getViewablePersonList());

		if (StringUtils.isEmpty(calendar_event.getCreatePerson())) {
			calendar_event.setCreatePerson(personName);
		}
		if (StringUtils.isEmpty(calendar_event.getUpdatePerson())) {
			calendar_event.setUpdatePerson(personName);
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if (StringUtils.isEmpty(calendar_event.getId())) {
				calendar_event.setId(Calendar_Event.createId());
			}
			Calendar_EventComment calendar_EventComment = calendar_EventService.createNewEventComment(emc,
					calendar_event);
			// 修改Comment信息
			if (calendar_EventComment != null) {
				calendar_event.setComment("{#CLOB#}");
				calendar_event.setCommentId(calendar_EventComment.getId());
			} else {
				calendar_event.setCommentId(null);
			}
			emc.beginTransaction(Calendar_Event.class);
			// 保存日程信息
			calendar_event = calendar_EventService.create(emc, calendar_event, false);
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return calendar_event;
	}

	/**
	 * 更新所有重复日程事件(一定是重复日程)
	 * 
	 * 1、更新当前日程事件对应的重复主体信息 RepeatMaster信息 删除该事件重复主体信息中所有的已经生成的月份信息
	 * 2、删除该RepeatMaster已经生成的所有日程事件
	 * 
	 * @param repeatMasterId
	 * @param calendar_event
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Integer updateWithMaster(String repeatMasterId, Calendar_Event calendar_event,
			EffectivePerson effectivePerson) throws Exception {

		if (StringUtils.isEmpty(repeatMasterId)) {
			throw new Exception("calendar repeat master id is empty, calendar can not save!");
		}

		if (calendar_event == null) {
			throw new Exception("calendar event is null, calendar can not save!");
		}

		Integer count = 0;
		Business business = null;
		Calendar_EventRepeatMaster calendar_EventRepeatMaster = null;
		List<String> eventIds = null;
		List<Calendar_Event> calendarEvents = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			business = new Business(emc);
			// 1、更新当前日程事件对应的重复主体信息 RepeatMaster信息
			calendar_EventRepeatMaster = emc.find(repeatMasterId, Calendar_EventRepeatMaster.class);
			if (calendar_EventRepeatMaster == null) {
				throw new Exception(
						"calendar repeat master not exists, calendar can not save! MasterID:" + repeatMasterId);
			}

			calendar_EventRepeatMaster = copyEventPropertyToMaster(calendar_event, calendar_EventRepeatMaster);

			// 对日历事件重复信息主体进行权限设置，至少自己创建的日历自己可以管理 ，可以发布，可以查看
			addStringToList(effectivePerson.getDistinguishedName(), calendar_EventRepeatMaster.getParticipants());
			addStringToList(effectivePerson.getDistinguishedName(),
					calendar_EventRepeatMaster.getManageablePersonList());
			addStringToList(effectivePerson.getDistinguishedName(), calendar_EventRepeatMaster.getViewablePersonList());

			if (StringUtils.isEmpty(calendar_EventRepeatMaster.getCreatePerson())) {
				calendar_EventRepeatMaster.setCreatePerson(effectivePerson.getDistinguishedName());
			}
			if (StringUtils.isEmpty(calendar_EventRepeatMaster.getUpdatePerson())) {
				calendar_EventRepeatMaster.setUpdatePerson(effectivePerson.getDistinguishedName());
			}
			calendar_EventRepeatMaster.setCreatedMonthList(new ArrayList<>());

			// 判断事件的备注信息是否超长，如果已经存在则需要更新一下
			Calendar_EventComment calendar_EventComment = calendar_EventService.createOrUpdateEventComment(emc,
					calendar_event);
			if (calendar_EventComment != null) {
				calendar_EventRepeatMaster.setComment("{#CLOB#}");
				calendar_EventRepeatMaster.setCommentId(calendar_EventComment.getId());
			} else {
				calendar_EventRepeatMaster.setCommentId(null);
			}

			emc.beginTransaction(Calendar_EventRepeatMaster.class);
			emc.check(calendar_EventRepeatMaster, CheckPersistType.all);

			// 2、删除该RepeatMaster已经生成的所有日程事件，等待重新生成
			eventIds = business.calendar_EventFactory().listWithRepeatMaster(repeatMasterId, null, null);
			if (ListTools.isNotEmpty(eventIds)) {
				count = eventIds.size();
				calendarEvents = business.calendar_EventFactory().list(eventIds);
			}
			if (ListTools.isNotEmpty(calendarEvents)) {
				emc.beginTransaction(Calendar_Event.class);
				for (Calendar_Event calendar_Event : calendarEvents) {
					emc.remove(calendar_Event, CheckRemoveType.all);
				}
			}
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	/**
	 * 更新重复日程事件中某一事件开始后续所有的日程事件信息(一定是重复日程)
	 * 
	 * 1、删除当前事件以及该重复事件主体信息已经生成的在该ID事件后续所有已经生成的日程事件信息
	 * 2、删除该事件重复主体信息中相应的已经生成的月份信息（已经删除所有指定月份以及以后所有的月份）
	 * 3、更新日程事件重复信息主体，将截止日期改为指定ID所在的日期的前一天的23:59:59
	 * 4、新增一个新的日程重复主体，从指定ID日程所在的日期开始，信息及重复规则更新为最新的信息
	 * 
	 * @param repeatMasterId
	 * @param calendar_event
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Integer updateAfterEventId(String repeatMasterId, Calendar_Event calendar_event,
			EffectivePerson effectivePerson) throws Exception {

		if (StringUtils.isEmpty(repeatMasterId)) {
			throw new Exception("calendar repeat master id is empty, calendar can not save!");
		}
		if (calendar_event == null) {
			throw new Exception("calendar event is null, calendar can not save!");
		}

		Integer count = 0;
		Business business = null;
		String eventYearAndMonth = null;
		Date eventMonth = null;
		Date createdMonth = null;
		Calendar_EventRepeatMaster calendar_EventRepeatMaster = null;
		Calendar_EventRepeatMaster calendar_EventRepeatMaster_new = null;
		List<String> eventIds = null;
		List<String> createdMonthList = new ArrayList<>();
		List<Calendar_Event> calendarEvents = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			business = new Business(emc);

			// -----------------------------------------------------------------------------------------------------------------//
			// 1、删除当前事件以及该重复事件主体信息已经生成的该ID事件后续时间内所有已经生成的日程事件信息
			eventIds = business.calendar_EventFactory().listWithRepeatMaster(repeatMasterId,
					calendar_event.getStartTime(), null);
			if (ListTools.isNotEmpty(eventIds)) {
				count = eventIds.size();
				calendarEvents = business.calendar_EventFactory().list(eventIds);
				if (ListTools.isNotEmpty(calendarEvents)) {
					emc.beginTransaction(Calendar_Event.class);
					for (Calendar_Event calendar_Event : calendarEvents) {
						emc.remove(calendar_Event, CheckRemoveType.all);
					}
				}
			}

			// -----------------------------------------------------------------------------------------------------------------//
			// 2、删除该事件重复主体信息中相应的已经生成的月份信息（已经删除所有指定月份以及以后所有的月份）
			// 3、更新日程事件重复信息主体，将截止日期改为指定ID所在的日期的前一天的23:59:59
			calendar_EventRepeatMaster = emc.find(repeatMasterId, Calendar_EventRepeatMaster.class);
			if (calendar_EventRepeatMaster == null) {
				throw new Exception(
						"calendar repeat master not exists, calendar can not save! MasterID:" + repeatMasterId);
			}
			// 事件权限保障：对日历事件重复信息主体进行权限设置，保证至少自己创建的日历自己可以管理 ，可以发布，可以查看
			addStringToList(effectivePerson.getDistinguishedName(), calendar_EventRepeatMaster.getParticipants());
			addStringToList(effectivePerson.getDistinguishedName(),
					calendar_EventRepeatMaster.getManageablePersonList());
			addStringToList(effectivePerson.getDistinguishedName(), calendar_EventRepeatMaster.getViewablePersonList());
			if (StringUtils.isEmpty(calendar_EventRepeatMaster.getCreatePerson())) {
				calendar_EventRepeatMaster.setCreatePerson(effectivePerson.getDistinguishedName());
			}
			if (StringUtils.isEmpty(calendar_EventRepeatMaster.getUpdatePerson())) {
				calendar_EventRepeatMaster.setUpdatePerson(effectivePerson.getDistinguishedName());
			}
			// 删除事件当月和之后的所有已经生成的月份信息记录，便于后续再次重新生成
			if (ListTools.isNotEmpty(calendar_EventRepeatMaster.getCreatedMonthList())) {
				eventYearAndMonth = dateOperation.getYearAndMonth(calendar_event.getStartTime());
				if (StringUtils.isNotEmpty(eventYearAndMonth)) {
					eventMonth = dateOperation.format_year_month.parse(eventYearAndMonth);
					for (String yearAndMonth : calendar_EventRepeatMaster.getCreatedMonthList()) {
						// 判断是否在eventYearAndMonth之前，在则添加到createdMonthList，否删除不添加
						createdMonth = dateOperation.format_year_month.parse(yearAndMonth);
						if (eventMonth.getTime() >= createdMonth.getTime()) {
							createdMonthList.add(yearAndMonth);
						}
					}
				}
			}
			calendar_EventRepeatMaster.setCreatedMonthList(createdMonthList);
			// 将截止日期改为指定ID所在的日期的前一天的23:59:59
			Date repeatEndTime = dateOperation
					.getEndTimeInDay(dateOperation.getDayAddDate(calendar_event.getStartTime(), -1));
			RRule rule = new RRule(calendar_EventRepeatMaster.getRecurrenceRule());
			Recur recur = rule.getRecur();
			recur.setUntil(new net.fortuna.ical4j.model.Date(repeatEndTime));
			calendar_EventRepeatMaster.setRecurrenceRule(new RRule(recur).getValue());

			calendar_EventRepeatMaster_new = new Calendar_EventRepeatMaster();
			calendar_EventRepeatMaster_new.setRecurrenceStartTime(calendar_EventRepeatMaster.getRecurrenceStartTime());
			calendar_EventRepeatMaster_new.setStartTime(calendar_EventRepeatMaster.getStartTime());
			calendar_EventRepeatMaster_new = copyEventPropertyToMaster(calendar_event, calendar_EventRepeatMaster_new);
			calendar_EventRepeatMaster_new.setRecurrenceStartTime(calendar_event.getStartTime());

			// 处理新的calendar_EventRepeatMaster_new的备注信息，原来的calendar_EventRepeatMaster不动
			Calendar_EventComment calendar_EventComment = calendar_EventService.createNewEventComment(emc,
					calendar_event);
			if (calendar_EventComment != null) {
				calendar_EventRepeatMaster_new.setComment("{#CLOB#}");
				calendar_EventRepeatMaster_new.setCommentId(calendar_EventComment.getId());
			} else {
				calendar_EventRepeatMaster_new.setCommentId(null);
			}

			emc.beginTransaction(Calendar_EventRepeatMaster.class);
			emc.check(calendar_EventRepeatMaster, CheckPersistType.all);
			emc.persist(calendar_EventRepeatMaster_new, CheckPersistType.all);
			emc.commit();

		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	/**
	 * 更新单个日历事件记录信息
	 * 
	 * @param calendar_event
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event updateSingleEvent(Calendar_Event calendar_event, EffectivePerson effectivePerson)
			throws Exception {

		// 对日历信息进行权限设置，至少自己创建的日历自己可以管理 ，可以发布，可以查看
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getParticipants());
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getManageablePersonList());
		addStringToList(effectivePerson.getDistinguishedName(), calendar_event.getViewablePersonList());

		if (StringUtils.isEmpty(calendar_event.getCreatePerson())) {
			calendar_event.setCreatePerson(effectivePerson.getDistinguishedName());
		}
		if (StringUtils.isEmpty(calendar_event.getUpdatePerson())) {
			calendar_event.setUpdatePerson(effectivePerson.getDistinguishedName());
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Calendar_Event old_event = emc.find(calendar_event.getId(), Calendar_Event.class);
			if (old_event == null) {
				throw new Exception("calendar event is not exsits. ID:" + calendar_event.getId());
			}
			calendar_event.copyTo(old_event, JpaObject.FieldsUnmodify);
			if (StringUtils.isEmpty(calendar_event.getId())) {
				old_event.setId(Calendar_Event.createId());
			}
			// 检查日程备注信息的长度，如果超长，则需要新建一个Calendar_EventComment记录
			// 如果超长，则创建一个新的则需要新建一个Calendar_EventComment记录
			Calendar_EventComment calendar_EventComment = calendar_EventService.createNewEventComment(emc,
					calendar_event);
			if (calendar_EventComment != null) {
				old_event.setComment("{#CLOB#}");
				old_event.setCommentId(calendar_EventComment.getId());
			} else {
				old_event.setCommentId(null);
			}

			emc.beginTransaction(Calendar_Event.class);
			if (StringUtils.isEmpty(old_event.getRepeatMasterId())) {
				// 原来不是重复的日程，修改后复重的日程，保存为重复信息主体
				if (StringUtils.isNotEmpty(calendar_event.getRecurrenceRule())) {
					Calendar_EventRepeatMaster calendar_EventRepeatMaster = composeEventRepeatMasterWithEvent(
							calendar_event);
					old_event.setRepeatMasterId(calendar_EventRepeatMaster.getId());
					emc.beginTransaction(Calendar_EventRepeatMaster.class);
					calendar_EventRepeatMasterService.create(emc, calendar_EventRepeatMaster, false);
				}
			} else {
				if (StringUtils.isEmpty(calendar_event.getRecurrenceRule())) {
					// 原来是重复的，现在不重复了，需要把repeatMaster删除，还有已经生成的所有event
					if (StringUtils.isNotEmpty(old_event.getRepeatMasterId())) {
						ArrayList arrayList = new ArrayList<>();
						arrayList.add(old_event.getId());
						calendar_EventRepeatMasterService.destoryWithMasterId(emc, old_event.getRepeatMasterId(),
								arrayList);
					}
				}
			}

			// 保存日程信息
			calendar_event = calendar_EventService.update(emc, old_event, false);
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return calendar_event;
	}

	/**
	 * 将一个字符串添加到一个List里
	 * 
	 * @param distinguishedName
	 * @param manageablePersonList
	 */
	private void addStringToList(String distinguishedName, List<String> manageablePersonList) {
		if (manageablePersonList == null) {
			manageablePersonList = new ArrayList<>();
		}
		if (StringUtils.isNotEmpty(distinguishedName)) {
			if (!manageablePersonList.contains(distinguishedName)) {
				manageablePersonList.add(distinguishedName);
			}
		}
	}

	private Calendar_EventRepeatMaster composeEventRepeatMasterWithEvent(Calendar_Event calendar_event)
			throws Exception {
		Calendar_EventRepeatMaster calendar_EventRepeatMaster = new Calendar_EventRepeatMaster(
				Calendar_EventRepeatMaster.createId(), calendar_event.getTitle(), calendar_event.getStartTime(),
				calendar_event.getEndTime(), calendar_event.getRecurrenceRule());
		calendar_event.copyTo(calendar_EventRepeatMaster, JpaObject.FieldsUnmodify);
		calendar_EventRepeatMaster.setCalendarId(calendar_event.getCalendarId());
		return calendar_EventRepeatMaster;
	}

	/**
	 * 根据条件查询指定的日历记录信息ID列表
	 * 
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
	public List<String> listWithCondition(String key, String eventType, String source, String createPerson,
			List<String> calendarIds, String personName, List<String> unitNames, List<String> groupNames,
			Date startTime, Date endTime) throws Exception {
		if (startTime == null) {
			startTime = new Date();
		}
		if (endTime == null) {
			endTime = new Date();
		}
		if (startTime.after(endTime)) {
			// 换个位置
			Date tmp = startTime;
			startTime = endTime;
			endTime = tmp;
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 两个时间之间的月份查出来，看看这些月份是否需要生成日历事件
			List<String> needCreateMonths = new DateOperation().listMonthsBetweenDate(startTime, endTime);

			if (ListTools.isNotEmpty(needCreateMonths)) {
				List<String> repeatMasterIds = new ArrayList<>();
				List<String> repeatMasterIds_month = null;

				for (String createMonth : needCreateMonths) {
					// 先看看有没有需要生成的日历重复主体
					repeatMasterIds_month = calendar_EventRepeatMasterService.listNeedRepeatMaster(emc, calendarIds,
							eventType, createMonth, personName, unitNames, groupNames);
					if (ListTools.isNotEmpty(repeatMasterIds_month)) {
						repeatMasterIds.addAll(repeatMasterIds_month);
					}
				}
				if (ListTools.isNotEmpty(repeatMasterIds)) {
					// 根据日期范围为指定的日期重复主体生成日历事件信息
					calendar_EventService.createCalendarWithMaster(emc, repeatMasterIds, startTime, endTime,
							needCreateMonths);
				}
			}

			// 最后从日历事件信息表里按条件进行数据查询返回
			return calendar_EventService.listWithCondition(emc, key, eventType, source, createPerson, calendarIds,
					personName, unitNames, groupNames, startTime, endTime);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据条件查询指定的日历记录信息ID列表
	 * 
	 * @param inFilterCalendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition(List<String> inFilterCalendarIds, String personName, List<String> unitNames,
			List<String> groupNames, Date startTime, Date endTime) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.listWithCondition(emc, null, null, null, null, inFilterCalendarIds, personName,
					unitNames, groupNames, startTime, endTime);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据重复主体以及日期范围查询指定的日历记录信息ID列表
	 * 
	 * @param repeatMasterId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithRepeatMaster(String repeatMasterId, Date startTime, Date endTime) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.listWithRepeatMaster(emc, repeatMasterId, startTime, endTime);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据ID彻底删除指定的日历记录信息
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void destory(String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendar_EventService.destory(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	public void destory(List<String> ids) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			calendar_EventService.destory(emc, ids);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 删除重复日程事件中某一事件开始后续所有的日程事件信息(一定是重复日程)
	 * 
	 * 1、删除当前事件以及该重复事件主体信息已经生成的该ID事件后续时间内所有已经生成的日程事件信息
	 * 2、更新日程事件重复信息主体，将截止日期改为指定ID所在的日期的前一天的23:59:59
	 * 
	 * @param repeatMasterId
	 * @param calendar_event
	 * @return
	 * @throws Exception
	 */
	public Integer destoryAfterEventId(String repeatMasterId, Calendar_Event calendar_event) throws Exception {

		if (StringUtils.isEmpty(repeatMasterId)) {
			throw new Exception("calendar repeat master id is empty, calendar can not delete!");
		}

		if (calendar_event == null) {
			throw new Exception("calendar event is null, calendar can not delete!");
		}

		Integer count = 0;
		Business business = null;
		List<String> eventIds = null;
		List<Calendar_Event> calendarEvents = null;
		Calendar_EventRepeatMaster calendar_EventRepeatMaster = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			calendar_EventRepeatMaster = emc.find(calendar_event.getRepeatMasterId(), Calendar_EventRepeatMaster.class);

			// -----------------------------------------------------------------------------------------------------------------
			// 1、删除当前事件以及该重复事件主体信息已经生成的该ID事件后续时间内所有已经生成的日程事件信息
			eventIds = business.calendar_EventFactory().listWithRepeatMaster(repeatMasterId,
					calendar_event.getStartTime(), null);
			if (ListTools.isNotEmpty(eventIds)) {
				count = eventIds.size();
				calendarEvents = business.calendar_EventFactory().list(eventIds);
			}
			if (ListTools.isNotEmpty(calendarEvents)) {
				emc.beginTransaction(Calendar_Event.class);
				for (Calendar_Event calendar_Event : calendarEvents) {
					emc.remove(calendar_Event, CheckRemoveType.all);
				}
			}

			// 将截止日期改为指定ID所在的日期的前一天的23:59:59
			Date repeatEndTime = dateOperation
					.getEndTimeInDay(dateOperation.getDayAddDate(calendar_event.getStartTime(), -1));
			RRule rule = new RRule(calendar_EventRepeatMaster.getRecurrenceRule());
			Recur recur = rule.getRecur();
			recur.setUntil(new net.fortuna.ical4j.model.Date(repeatEndTime));
			calendar_EventRepeatMaster.setRecurrenceRule(new RRule(recur).getValue());

			emc.beginTransaction(Calendar_EventRepeatMaster.class);
			emc.check(calendar_EventRepeatMaster, CheckPersistType.all);
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return count;
	}

	/**
	 * 将一个日程事件对象转换为一个iCal4j的Calendar对象
	 * 
	 * @param o2_calendar_event
	 * @return
	 */
	public Calendar parseToiCal(Calendar_Event o2_calendar_event) {
		// 创建日历
		Calendar calendar = null;
		try {
			calendar = new Calendar();
			calendar.getProperties().add(new ProdId("-//O2OA//iCal4j 1.0//EN"));
			calendar.getProperties().add(Version.VERSION_2_0);
			calendar.getProperties().add(CalScale.GREGORIAN);

			// 时间主题
			String summary = null;
			if (StringUtils.isNotEmpty(o2_calendar_event.getTitle())) {
				summary = o2_calendar_event.getTitle();
			} else {
				summary = "未命名-日程事件";
			}

			if (o2_calendar_event.getStartTime() == null) {
				throw new Exception("event start time is null!");
			}

			if (o2_calendar_event.getEndTime() == null) {
				throw new Exception("event end time is null!");
			}

			// 开始时间
			DateTime start = new DateTime(o2_calendar_event.getStartTime().getTime());
			// 开始时间转换为UTC时间（UTC ＋ 时区差 ＝ 本地时间 ）
			start.setUtc(true);
			// 结束时间
			DateTime end = new DateTime(o2_calendar_event.getEndTime().getTime());
			// 结束时间设置成UTC时间（UTC ＋ 时区差 ＝ 本地时间 ）
			end.setUtc(true);

			VEvent event = new VEvent(start, end, summary);
			if (StringUtils.isNotEmpty(o2_calendar_event.getLocationName())) {
				event.getProperties().add(new Location(o2_calendar_event.getLocationName()));
			}

			// 生成唯一标示
			event.getProperties().add(new Uid(o2_calendar_event.getId()));

			// 创建一个时区（TimeZone）
			TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			TimeZone timezone = registry.getTimeZone("Asia/Shanghai");
			VTimeZone tz = timezone.getVTimeZone();
			// 添加时区信息
			event.getProperties().add(tz.getTimeZoneId());

			// 添加备注信息
			if (StringUtils.isNotEmpty(o2_calendar_event.getComment())) {
				Comment comment = new Comment(o2_calendar_event.getComment());
				event.getProperties().add(comment);
			}

			// 添加参与者
			if (ListTools.isNotEmpty(o2_calendar_event.getParticipants())) {
				Attendee attendee = null;
				for (String participant : o2_calendar_event.getParticipants()) {
					attendee = new Attendee();
					attendee.getParameters().add(Role.OPT_PARTICIPANT);
					attendee.getParameters().add(new Cn(participant));
					event.getProperties().add(attendee);
				}
			}

			// 重复事件 START------------------------------------------------------------
			if (StringUtils.isNotEmpty(o2_calendar_event.getRecurrenceRule())) {
				RRule rule = new RRule(o2_calendar_event.getRecurrenceRule());
				event.getProperties().add(rule);
			}
			// 重复事件 END------------------------------------------------------------

			// 提醒,提前10分钟
			if (StringUtils.isNotEmpty(o2_calendar_event.getValarmTime_config())) {
				String[] time_config = o2_calendar_event.getValarmTime_config().split(",");
				VAlarm valarm = null;
				if (time_config != null && time_config.length == 4) {
					int day = Integer.parseInt(time_config[0]);
					int hour = Integer.parseInt(time_config[1]);
					int min = Integer.parseInt(time_config[2]);
					int sec = Integer.parseInt(time_config[3]);

					// P2DT3H4M5S"
					valarm = new VAlarm(Duration.parse("P" + day + "DT" + hour + "H" + min + "M" + sec + "S"));
					// valarm = new VAlarm(new Dur(day, hour, min, sec));
					if (StringUtils.isNotEmpty(o2_calendar_event.getValarm_Summary())) {
						valarm.getProperties().add(new Summary(o2_calendar_event.getValarm_Summary()));
					} else {
						valarm.getProperties().add(new Summary("日程事件提醒"));
					}
					valarm.getProperties().add(Action.AUDIO);
					valarm.getProperties().add(Action.DISPLAY);
					// valarm.getProperties().add(Action.EMAIL);
					if (StringUtils.isNotEmpty(o2_calendar_event.getValarm_description())) {
						valarm.getProperties().add(new Description(o2_calendar_event.getValarm_description()));
					}
					// 将VAlarm加入VEvent
					event.getAlarms().add(valarm);
				}
			}
			// 添加事件
			calendar.getComponents().add(event);
			// 验证
			calendar.validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return calendar;
	}

	/**
	 * 验证一个日历是否符合ICAL要求
	 * 
	 * @param o2_calendar_event
	 * @throws ValidationException
	 * @throws IOException
	 */
	public Boolean validateCalendar(Calendar_Event o2_calendar_event) throws ValidationException, IOException {
		Calendar calendar = parseToiCal(o2_calendar_event);
		calendar.validate();
		return true;
	}

	/**
	 * 将一个日程事件写为一个ical文件
	 * 
	 * @param o2_calendar_event
	 * @param path
	 * @throws ValidationException
	 * @throws IOException
	 */
//	public void writeiCal(Calendar_Event o2_calendar_event, String path) throws ValidationException, IOException {
//		Calendar calendar = parseToiCal(o2_calendar_event);
//		FileOutputStream fout = new FileOutputStream("D://2.ics");
//		CalendarOutputter outputter = new CalendarOutputter();
//		outputter.output(calendar, fout);
//	}

	/**
	 * 将一个日程事件写为一个ical文件
	 * 
	 * @param o2_calendar_event
	 * @param out
	 * @throws ValidationException
	 * @throws IOException
	 */
	public void wirteiCal(Calendar_Event o2_calendar_event, OutputStream out) throws ValidationException, IOException {
		Calendar calendar = parseToiCal(o2_calendar_event);
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(calendar, out);
	}

	/**
	 * 获取一个日程事件转换为ICAL后的内容信息
	 * 
	 * @param o2_calendar_event
	 * @throws ValidationException
	 * @throws IOException
	 */
	public String getiCalContent(Calendar_Event o2_calendar_event) throws ValidationException, IOException {
		Calendar calendar = parseToiCal(o2_calendar_event);
		CalendarOutputter calendarOutputter = new CalendarOutputter(false);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			calendarOutputter.output(calendar, baos);
			return new String(baos.toByteArray(), "utf-8");
		}
	}

	/**
	 * 查询需要提醒的日程事件ID列表
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedAlarmEventIds(Date date) throws Exception {
		if (date == null) {
			date = new Date();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.listNeedAlarmEventIds(emc, date);
		}
	}

	/**
	 * 根据calendar_event信息更新一个新的重复主体
	 * 
	 * @param calendar_event
	 * @return
	 */
	private Calendar_EventRepeatMaster copyEventPropertyToMaster(Calendar_Event calendar_event,
			Calendar_EventRepeatMaster calendar_EventRepeatMaster) {
		if (calendar_event != null && calendar_EventRepeatMaster != null) {
			Date recurrenceStartTime = calendar_EventRepeatMaster.getRecurrenceStartTime();
			Date startTime = calendar_EventRepeatMaster.getStartTime();

			Date startTime_event = calendar_event.getStartTime();
			Date recurrenceStartTime_new = null;
			Date startTime_new = null;

			// 修改一下开始时间的时分秒
			String newRecurrenceStartTime_str = dateOperation.getDate(recurrenceStartTime, "yyyy-MM-dd") + " "
					+ dateOperation.getDate(startTime_event, "HH:mm:ss");
			String newStartTime_str = dateOperation.getDate(startTime_event, "yyyy-MM-dd") + " "
					+ dateOperation.getDate(startTime_event, "HH:mm:ss");
			try {
				recurrenceStartTime_new = dateOperation.getDateFromString(newRecurrenceStartTime_str,
						"yyyy-MM-dd HH:mm:ss");
			} catch (Exception e) {
				recurrenceStartTime_new = recurrenceStartTime;
				e.printStackTrace();
			}
			try {
				startTime_new = dateOperation.getDateFromString(newStartTime_str, "yyyy-MM-dd HH:mm:ss");
			} catch (Exception e) {
				startTime_new = startTime;
				e.printStackTrace();
			}
			calendar_EventRepeatMaster.setAlarm(calendar_event.getAlarm());
			calendar_EventRepeatMaster.setAlarmTime(calendar_event.getAlarmTime());
			calendar_EventRepeatMaster.setCalendarId(calendar_event.getCalendarId());
			calendar_EventRepeatMaster.setComment(calendar_event.getComment());
			calendar_EventRepeatMaster.setCreatePerson(calendar_event.getCreatePerson());
			calendar_EventRepeatMaster.setDaysOfDuration(calendar_event.getDaysOfDuration());
			calendar_EventRepeatMaster.setEventType(calendar_event.getEventType());
			calendar_EventRepeatMaster.setIsAllDayEvent(calendar_event.getIsAllDayEvent());
			calendar_EventRepeatMaster.setIsPublic(calendar_event.getIsPublic());
			calendar_EventRepeatMaster.setLatitude(calendar_event.getLatitude());
			calendar_EventRepeatMaster.setLocationName(calendar_event.getLocationName());
			calendar_EventRepeatMaster.setLongitude(calendar_event.getLongitude());
			calendar_EventRepeatMaster.setManageablePersonList(calendar_event.getManageablePersonList());
			calendar_EventRepeatMaster.setParticipants(calendar_event.getParticipants());
			calendar_EventRepeatMaster.setRecurrenceExc(calendar_event.getRecurrenceExc());
			calendar_EventRepeatMaster.setRecurrenceRule(calendar_event.getRecurrenceRule());
			calendar_EventRepeatMaster.setRepeatStatus("等待生成");
			calendar_EventRepeatMaster.setSource(calendar_event.getSource());
			calendar_EventRepeatMaster.setTargetType(calendar_event.getTargetType());
			calendar_EventRepeatMaster.setTitle(calendar_event.getTitle());
			calendar_EventRepeatMaster.setUpdatePerson(calendar_event.getUpdatePerson());
			calendar_EventRepeatMaster.setValarm_description(calendar_event.getValarm_description());
			calendar_EventRepeatMaster.setValarm_mailto(calendar_event.getValarm_mailto());
			calendar_EventRepeatMaster.setValarm_Summary(calendar_event.getValarm_Summary());
			calendar_EventRepeatMaster.setValarmTime_config(calendar_event.getValarmTime_config());
			calendar_EventRepeatMaster.setViewableGroupList(calendar_event.getViewableGroupList());
			calendar_EventRepeatMaster.setViewableUnitList(calendar_event.getViewableUnitList());
			calendar_EventRepeatMaster.setColor(calendar_event.getColor());
			calendar_EventRepeatMaster.setRecurrenceStartTime(recurrenceStartTime_new);
			calendar_EventRepeatMaster.setStartTime(startTime_new);
			calendar_EventRepeatMaster.setEndTime(calendar_event.getEndTime());
		}
		return calendar_EventRepeatMaster;
	}

	public List<String> destoryWithBundle(String bundle) throws Exception {
		if (StringUtils.isEmpty(bundle)) {
			throw new Exception("bundle is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return calendar_EventService.destoryWithBundle(emc, bundle);
		}  
	}

	/**
	 * 根据commentId获取Calendar_EventComment信息
	 * 
	 * @param commentId
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventComment getCommentWithCommentId(String commentId) throws Exception {
		if (StringUtils.isEmpty(commentId)) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(commentId, Calendar_EventComment.class);
		}
	}
}
