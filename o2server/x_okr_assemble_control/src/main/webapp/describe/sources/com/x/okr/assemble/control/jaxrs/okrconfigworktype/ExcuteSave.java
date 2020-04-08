package com.x.okr.assemble.control.jaxrs.okrconfigworktype;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigSave;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWorkTypeConfigValidate;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrConfigWorkType;

public class ExcuteSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrConfigWorkType okrConfigWorkType = null;
		Wi wrapIn = null;
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
		}
		
		if( check ){
			try {
				wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionWrapInConvert( e, jsonElement );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getWorkTypeName() == null || wrapIn.getWorkTypeName().isEmpty() ) {
				check = false;
				Exception exception = new ExceptionWorkTypeConfigValidate( "工作类别排名称不能为空！" );
				result.error( exception );
			}
		}
		if( check ){
			if( wrapIn.getOrderNumber() == null ) {
				check = false;
				Exception exception = new ExceptionWorkTypeConfigValidate( "工作类别排序号不能为空！" );
				result.error( exception );
			}
		}
		if( check ){
			try {
				okrConfigWorkType = okrConfigWorkTypeService.save( wrapIn );
				result.setData( new Wo( okrConfigWorkType.getId() ));
				ApplicationCache.notify( OkrConfigWorkType.class );
				
				if( okrConfigWorkType != null ) {
					WrapInWorkDynamic.sendWithConfigWorkType( 
							okrConfigWorkType, 
							effectivePerson.getDistinguishedName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginUserName(),
							"保存工作级别信息",
							"保存工作级别信息成功！"
					);
				}
			} catch (Exception e) {
				Exception exception = new ExceptionWorkTypeConfigSave( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends OkrConfigWorkType {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}