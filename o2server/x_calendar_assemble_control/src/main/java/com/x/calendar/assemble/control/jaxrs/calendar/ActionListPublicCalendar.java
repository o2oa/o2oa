package com.x.calendar.assemble.control.jaxrs.calendar;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
 * 列示所有公开的日历信息
 * 
 * @author O2LEE
 *
 */
public class ActionListPublicCalendar extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListPublicCalendar.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<Calendar> calendarList = null;
		Boolean check = true;
		List<String> ids = null;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		
		if( check ){
			try {
				unitNames = userManagerService.listUnitNamesWithPerson(personName);
				groupNames = userManagerService.listGroupNamesByPerson(personName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "系统根据用户查询组织和群组信息列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}			
		}
		
		if( check ){
			try {
				ids = calendarServiceAdv.listPublicCalendar();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "系统查询所有公开的日历信息ID列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( ids )) {
				calendarList = calendarServiceAdv.list(ids);
				try {
					wraps = Wo.copier.copy( calendarList );
					if( ListTools.isNotEmpty( wraps )) {
						for( Wo wo : wraps ) {
							if( ListTools.isNotEmpty( wo.getFollowers() ) && wo.getFollowers().contains( effectivePerson.getDistinguishedName() )) {
								wo.setFollowed( true );
							}
							wo.setManageable( ThisApplication.isCalendarManager( effectivePerson, wo ) );
							wo.setPublishable( ThisApplication.isCalendarPublisher( effectivePerson, unitNames, groupNames, wo ) );	
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionCalendarInfoProcess( e, "将所有查询到的日历信息对象转换为可以输出的信息时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		result.setData(wraps);
		return result;
	}	
	
	
	public static class Wo extends Calendar  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		@FieldDescribe("用户是否可以对该日历进行管理.")
		private Boolean manageable = false;
		
		@FieldDescribe("用户是否可以在该日历中发布日程事件.")
		private Boolean publishable = false;
		
		@FieldDescribe("用户是否已经关注.")
		private Boolean followed = false;
		
		public static WrapCopier<Calendar, Wo> copier = WrapCopierFactory.wo( Calendar.class, Wo.class, null,Wo.Excludes);

		public Boolean getManageable() {
			return manageable;
		}

		public Boolean getPublishable() {
			return publishable;
		}

		public void setManageable(Boolean manageable) {
			this.manageable = manageable;
		}

		public void setPublishable(Boolean publishable) {
			this.publishable = publishable;
		}

		public Boolean getFollowed() {
			return followed;
		}

		public void setFollowed(Boolean followed) {
			this.followed = followed;
		}		
	}
}