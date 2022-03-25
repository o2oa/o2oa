package com.x.calendar.assemble.control.jaxrs.calendar;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.core.entity.Calendar;

public class ActionSave extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionSave.class );

	@AuditLog(operation = "保存日历")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Boolean check = true;
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			if( StringUtils.isEmpty( wi.getName() ) ){
				check = false;
				Exception exception = new ExceptionCalendarNameEmpty();
				result.error( exception );
			}
		}
		
		if( check ) {
			if( StringUtils.isEmpty( wi.getType()  )) {
				wi.setType("PERSON");
			}
		}
		
		if( check ) {
			if( StringUtils.isEmpty( wi.getTarget()  )) {
				check = false;
				Exception exception = new ExceptionCalendarPropertyEmpty("target");
				result.error( exception );
			}
		}
		
		if( check ) {
			//校验target是否正常
			if( "UNIT".equals( wi.getType() )) {
				Unit unit = null;
				if( StringUtils.isNotEmpty( wi.getTarget() )) {
					unit = userManagerService.getUnitWIthFlag( wi.getTarget() );
				}else {
					unit = userManagerService.getUnitWIthFlag( effectivePerson.getDistinguishedName() );
				}
				if( unit == null ) {
					check = false;
					Exception exception = new ExceptionCalendarTargetInvalid( wi.getTarget() );
					result.error( exception );
				}else {
					wi.setTarget( unit.getDistinguishedName() );
				}
			}else {
				Person person = userManagerService.getPersonWithFlag( wi.getTarget() );
				if( person == null ) {
					wi.setTarget( effectivePerson.getDistinguishedName() );
				}else {
					wi.setTarget( person.getDistinguishedName() );
				}
			}
		}
		
		Calendar calendar = null;
		if( check ){
			try {
				calendar = Wi.copier.copy( wi );
				if( StringUtils.isNotEmpty( wi.getId())){
					calendar.setId( wi.getId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "系统将用户传入的数据转换为日历信息对象时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				calendar = calendarServiceAdv.save( calendar, effectivePerson );
				result.setData( new Wo( calendar.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCalendarInfoProcess( e, "保存日历信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends Calendar {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, Calendar > copier = WrapCopierFactory.wi( Wi.class, Calendar.class, null, Wi.Excludes );
		
		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}