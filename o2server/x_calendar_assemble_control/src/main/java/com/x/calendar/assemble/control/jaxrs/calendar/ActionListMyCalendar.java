package com.x.calendar.assemble.control.jaxrs.calendar;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;

/**
 * 列示所有我能访问到的日历信息
 * 
 * @author O2LEE
 *
 */
public class ActionListMyCalendar extends BaseAction {

	private final Logger LOGGER = LoggerFactory.getLogger(ActionListMyCalendar.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		List<Calendar> calendarList = null;
		List<String> ids = null;

		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();

		unitNames = userManagerService.listUnitNamesWithPerson(personName);
		groupNames = userManagerService.listGroupNamesByPerson(personName);

		ids = calendarServiceAdv.listWithCondition(personName, unitNames, groupNames);

		if (ListTools.isNotEmpty(ids)) {
			calendarList = calendarServiceAdv.list(ids);
		}

		if (ListTools.isNotEmpty(calendarList)) {
			result.setCount(Long.parseLong(calendarList.size() + ""));
			WoCalendar woCalendar = null;
			Boolean existsSystemDefaultCalendar = false;
			for (Calendar calendar : calendarList) {
				woCalendar = WoCalendar.copier.copy(calendar);
				woCalendar.setManageable(ThisApplication.isCalendarManager(effectivePerson, calendar));
				woCalendar.setPublishable(
						ThisApplication.isCalendarPublisher(effectivePerson, unitNames, groupNames, calendar));
				// 将所有的日历分为三类，放在三个不同的LIST里
				// 组织日历
				if (ListTools.isNotEmpty(calendar.getFollowers())
						&& calendar.getFollowers().contains(effectivePerson.getDistinguishedName())) {
					// 我关注的日历
					wo.addFollowCalendar(woCalendar);
				} else {
					// 个人或者组织日历
					if ("UNIT".equalsIgnoreCase(calendar.getType())) {
						// 组织日历
						wo.addUnitCalendar(woCalendar);
					} else {
						// 个人日历
//							Boolean isMineCalendar = calendar.getCreateor().equalsIgnoreCase( effectivePerson.getDistinguishedName() );
						Boolean isSystemCreate = StringUtils.equalsAnyIgnoreCase("SYSTEM", calendar.getCreateor());
						if (isSystemCreate) {
							if (!existsSystemDefaultCalendar) {
								woCalendar.setCreateor(effectivePerson.getDistinguishedName());
								wo.addMyCalendar(woCalendar);
								existsSystemDefaultCalendar = true;
							} else {
								// 多了一个系统日历，删除当前这个日历
								calendarServiceAdv.destory(calendar.getId());
							}
						} else {
							wo.addMyCalendar(woCalendar);
						}
					}
				}
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo {

		@FieldDescribe("我创建的个人日历列表.")
		private List<WoCalendar> myCalendars = new ArrayList<>();

		@FieldDescribe("可见的组织日历列表.")
		private List<WoCalendar> unitCalendars = new ArrayList<>();

		@FieldDescribe("关注的日历列表.")
		private List<WoCalendar> followCalendars = new ArrayList<>();

		public List<WoCalendar> getMyCalendars() {
			return myCalendars;
		}

		public List<WoCalendar> getUnitCalendars() {
			return unitCalendars;
		}

		public List<WoCalendar> getFollowCalendars() {
			return followCalendars;
		}

		public void setMyCalendars(List<WoCalendar> myCalendars) {
			this.myCalendars = myCalendars;
		}

		public void setUnitCalendars(List<WoCalendar> unitCalendars) {
			this.unitCalendars = unitCalendars;
		}

		public void setFollowCalendars(List<WoCalendar> followCalendars) {
			this.followCalendars = followCalendars;
		}

		public void addMyCalendar(WoCalendar calendar) {
			if (this.myCalendars == null) {
				this.myCalendars = new ArrayList<>();
			}
			this.myCalendars.add(calendar);
		}

		public void addUnitCalendar(WoCalendar calendar) {
			if (this.unitCalendars == null) {
				this.unitCalendars = new ArrayList<>();
			}
			this.unitCalendars.add(calendar);
		}

		public void addFollowCalendar(WoCalendar calendar) {
			if (this.followCalendars == null) {
				this.followCalendars = new ArrayList<>();
			}
			this.followCalendars.add(calendar);
		}

	}

	public static class WoCalendar extends Calendar {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static {
			Excludes.add(JpaObject.sequence_FIELDNAME);
			Excludes.add("manageablePersonList");
			Excludes.add("viewablePersonList");
			Excludes.add("viewableUnitList");
			Excludes.add("viewableGroupList");
			Excludes.add("publishablePersonList");
			Excludes.add("publishableUnitList");
			Excludes.add("publishableGroupList");
			// Excludes.add( "status" );
			Excludes.add("distributeFactor");
			Excludes.add("followers");

		}

		@FieldDescribe("用户是否可以对该日历进行管理.")
		private Boolean manageable = false;

		@FieldDescribe("用户是否可以在该日历中发布日程事件.")
		private Boolean publishable = false;

		public static WrapCopier<Calendar, WoCalendar> copier = WrapCopierFactory.wo(Calendar.class, WoCalendar.class,
				null, WoCalendar.Excludes);

		public Boolean getManageable() {
			return manageable;
		}

		public void setManageable(Boolean manageable) {
			this.manageable = manageable;
		}

		public Boolean getPublishable() {
			return publishable;
		}

		public void setPublishable(Boolean publishable) {
			this.publishable = publishable;
		}
	}
}