package com.x.calendar.assemble.control.jaxrs.calendar;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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

public class ActionGet extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Calendar calendar = null;
		Boolean check = true;
		List<String> unitNames = null;
		List<String> groupNames = null;
		String personName = effectivePerson.getDistinguishedName();
		
		if( check ){
			if( StringUtils.isEmpty( id ) ){
				check = false;
				Exception exception = new ExceptionCalendarIdEmpty();
				result.error( exception );
			}
		}

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
				calendar = calendarServiceAdv.get( id );
				if( calendar == null ) {
					check = false;
					Exception exception = new ExceptionCalendarNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "系统根据ID查询指定日历信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrap = Wo.copier.copy( calendar );
				if( StringUtils.equalsAnyIgnoreCase("SYSTEM", calendar.getCreateor() )){
					wrap.setCreateor( effectivePerson.getDistinguishedName() );
				}
				wrap.setManageable( ThisApplication.isCalendarManager( effectivePerson, wrap ) );
				wrap.setPublishable( ThisApplication.isCalendarPublisher( effectivePerson, unitNames, groupNames, wrap ) );

				if(ListTools.isNotEmpty( wrap.getManageablePersonList() )){
					for( String person : wrap.getManageablePersonList() ){
						if( StringUtils.equalsAnyIgnoreCase( person, "SYSTEM")){
							if( !wrap.getManageablePersonList().contains( effectivePerson.getDistinguishedName() )){
								wrap.getManageablePersonList().add( effectivePerson.getDistinguishedName() );
							}
						}
					}
				}

				if(ListTools.isNotEmpty( wrap.getViewablePersonList() )){
					for( String person : wrap.getViewablePersonList() ){
						if( StringUtils.equalsAnyIgnoreCase( person, "SYSTEM")){
							if( !wrap.getViewablePersonList().contains( effectivePerson.getDistinguishedName() )){
								wrap.getViewablePersonList().add( effectivePerson.getDistinguishedName() );
							}
						}
					}
				}

				if(ListTools.isNotEmpty( wrap.getViewablePersonList() )){
					for( String person : wrap.getViewablePersonList() ){
						if( StringUtils.equalsAnyIgnoreCase( person, "SYSTEM")){
							if( !wrap.getViewablePersonList().contains( effectivePerson.getDistinguishedName() )){
								wrap.getViewablePersonList().add( effectivePerson.getDistinguishedName() );
							}
						}
					}
				}
			} catch (Exception e) {
				Exception exception = new ExceptionCalendarInfoProcess( e, "将所有查询到的日历信息对象转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		result.setData( wrap );
		return result;
	}

	public static class Wo extends Calendar  {
		
		@FieldDescribe("用户是否可以对该日历进行管理.")
		private Boolean manageable = false;
		
		@FieldDescribe("用户是否可以在该日历中发布日程事件.")
		private Boolean publishable = false;
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
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