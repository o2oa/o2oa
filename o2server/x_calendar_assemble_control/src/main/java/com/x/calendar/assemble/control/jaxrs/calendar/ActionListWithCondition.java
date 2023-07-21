package com.x.calendar.assemble.control.jaxrs.calendar;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.assemble.control.ThisApplication;
import com.x.calendar.core.entity.Calendar;

/**
 * 根据条件和权限列示能访问到的符合条件的日历信息
 * @author O2LEE
 *
 */
public class ActionListWithCondition extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionListWithCondition.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson,  JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = null;
		List<Wo> wraps = null;
		List<Calendar> calendarList = null;
		Boolean check = true;
		//Boolean manager = false;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		List<String> ids = null;
		
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
		

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
//		try {
//			manager = ThisApplication.isCalendarSystemManager(effectivePerson);
//		} catch (Exception e) {
//			check = false;
//			Exception exception = new ExceptionCalendarInfoProcess(e, "系统在检查用户是否是平台管理员时发生异常。Name:" + personName);
//			result.error(exception);
//			logger.error(e, effectivePerson, request, null);
//		}
		
		if( check ){
			try {
				ids = calendarServiceAdv.listWithCondition( wi.getName(), wi.getType(), wi.getSource(), wi.getCreateor(), null, personName, 
						unitNames, groupNames );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "系统根据条件查询指定的日历信息ID列表时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
//			if( manager) { //不带权限
//				try {
//					ids = calendarServiceAdv.listWithCondition( wi.getName(), wi.getType(), wi.getSource(), wi.getCreateor(), null );
//				} catch (Exception e) {
//					check = false;
//					Exception exception = new ExceptionCalendarInfoProcess( e, "系统根据条件查询指定的日历信息ID列表（忽略权限）时发生异常." );
//					result.error( exception );
//					logger.error( e, effectivePerson, request, null);
//				}
//			}else {
//				
//			}
		}
		
		if( check ){
			if( ListTools.isNotEmpty( ids )) {
				calendarList = calendarServiceAdv.list(ids);
				try {
					wraps = Wo.copier.copy( calendarList );
					if( ListTools.isNotEmpty( wraps )) {
						for( Wo wo : wraps ) {
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
	
	public static class Wi{
		
		@FieldDescribe( "日历名称" )
		private String name = null;
		
		@FieldDescribe( "日历类型" )
		private String type = null; 
		
		@FieldDescribe( "日历来源" )
		private String source = null; 
		
		@FieldDescribe( "创建者" )
		private String createor = null;

		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}
		public String getSource() {
			return source;
		}
		public String getCreateor() {
			return createor;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setType(String type) {
			this.type = type;
		}
		public void setSource(String source) {
			this.source = source;
		}
		public void setCreateor(String createor) {
			this.createor = createor;
		}
	}

	public static class Wo extends Calendar  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		@FieldDescribe("用户是否可以对该日历进行管理.")
		private Boolean manageable = false;
		
		@FieldDescribe("用户是否可以在该日历中发布日程事件.")
		private Boolean publishable = false;
		
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
	}
}