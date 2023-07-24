package com.x.calendar.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.calendar.assemble.control.factory.CalendarFactory;
import com.x.calendar.assemble.control.factory.Calendar_EventCommentFactory;
import com.x.calendar.assemble.control.factory.Calendar_EventFactory;
import com.x.calendar.assemble.control.factory.Calendar_EventRepeatMasterFactory;
import com.x.calendar.assemble.control.factory.Calendar_SettingFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;
	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}
	
	private Calendar_SettingFactory calendar_SettingFactory;	
	public Calendar_SettingFactory calendar_SettingFactory() throws Exception {
		if (null == this.calendar_SettingFactory) {
			this.calendar_SettingFactory = new Calendar_SettingFactory( this );
		}
		return calendar_SettingFactory;
	}

	private CalendarFactory calendarFactory;	
	public CalendarFactory calendarFactory() throws Exception {
		if (null == this.calendarFactory) {
			this.calendarFactory = new CalendarFactory( this );
		}
		return calendarFactory;
	}
	
	private Calendar_EventFactory calendar_EventFactory;	
	public Calendar_EventFactory calendar_EventFactory() throws Exception {
		if (null == this.calendar_EventFactory) {
			this.calendar_EventFactory = new Calendar_EventFactory( this );
		}
		return calendar_EventFactory;
	}

	private Calendar_EventCommentFactory calendar_EventCommentFactory;
	public Calendar_EventCommentFactory calendar_EventCommentFactory() throws Exception {
		if (null == this.calendar_EventCommentFactory) {
			this.calendar_EventCommentFactory = new Calendar_EventCommentFactory( this );
		}
		return calendar_EventCommentFactory;
	}

	private Calendar_EventRepeatMasterFactory calendar_EventRepeatMasterFactory;	
	public Calendar_EventRepeatMasterFactory calendar_EventRepeatMasterFactory() throws Exception {
		if (null == this.calendar_EventRepeatMasterFactory) {
			this.calendar_EventRepeatMasterFactory = new Calendar_EventRepeatMasterFactory( this );
		}
		return calendar_EventRepeatMasterFactory;
	}
}
