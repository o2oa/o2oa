package com.x.teamwork.assemble.control.jaxrs.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.SystemConfig;

public class ActionSave extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		SystemConfig systemConfig = new SystemConfig();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionConfigInfoProcess( e, "Json格式错误："+jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
				check = false;
				Exception exception = new ExceptionConfigCodeEmpty();
				result.error( exception );
			}
		}
		if( check ){
			if( wrapIn.getConfigName() == null || wrapIn.getConfigName().isEmpty() ){
				check = false;
				Exception exception = new ExceptionConfigNameEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				systemConfig = Wi.copier.copy( wrapIn );
				if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					systemConfig.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionConfigInfoProcess( e, "系统将用户传入的数据转换为考勤系统配置对象时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				systemConfig = systemConfigPersistService.save( systemConfig );
				result.setData( new Wo( systemConfig.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionConfigInfoProcess( e, "保存考勤系统配置信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends SystemConfig {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);
		
		public static WrapCopier< Wi, SystemConfig > copier = WrapCopierFactory.wi( Wi.class, SystemConfig.class, null, Wi.Excludes );
		
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