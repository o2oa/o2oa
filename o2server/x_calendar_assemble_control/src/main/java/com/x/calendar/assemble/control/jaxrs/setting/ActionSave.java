package com.x.calendar.assemble.control.jaxrs.setting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.x.calendar.assemble.control.ExceptionWrapInConvert;
import com.x.calendar.core.entity.Calendar_Setting;

public class ActionSave extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionSave.class );

	@AuditLog(operation = "保存系统设置")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		Calendar_Setting report_S_Setting = new Calendar_Setting();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
				check = false;
				Exception exception = new ExceptionSettingCodeEmpty();
				result.error( exception );
			}
		}
		if( check ){
			if( wrapIn.getConfigName() == null || wrapIn.getConfigName().isEmpty() ){
				check = false;
				Exception exception = new ExceptionSettingNameEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				report_S_Setting = Wi.copier.copy( wrapIn );
				if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					report_S_Setting.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "系统将用户传入的数据转换为考勤系统配置对象时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				report_S_Setting = calendar_SettingServiceAdv.save( report_S_Setting );
				result.setData( new Wo( report_S_Setting.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSettingInfoProcess( e, "保存考勤系统配置信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends Calendar_Setting {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, Calendar_Setting > copier = WrapCopierFactory.wi( Wi.class, Calendar_Setting.class, null, Wi.Excludes );
		
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