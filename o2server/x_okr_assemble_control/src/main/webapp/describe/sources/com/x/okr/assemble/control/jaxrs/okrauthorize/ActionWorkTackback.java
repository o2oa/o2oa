package com.x.okr.assemble.control.jaxrs.okrauthorize;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionAuthorizeWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWorkTackbackProcess;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeService;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionWorkTackback extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionWorkTackback.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<Wo>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Boolean check = true;
		Wi wrapIn = null;
		OkrUserCache  okrUserCache  = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}		
		}
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
			wrapIn.setAuthorizeIdentity( okrUserCache.getLoginIdentityName()  );
		}
		if( check ){
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				Exception exception = new ExceptionAuthorizeWorkIdEmpty();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		//2、工作信息是否已经存在
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new ExceptionWorkNotExists( wrapIn.getWorkId() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkQueryById( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			OkrWorkAuthorizeService okrWorkAuthorizeService = new OkrWorkAuthorizeService();
			try {
				okrWorkAuthorizeService.tackback( okrWorkBaseInfo, wrapIn.getAuthorizeIdentity() );
				
				WrapInWorkDynamic.sendWithWorkInfo( 
						okrWorkBaseInfo, 
						effectivePerson.getDistinguishedName(),
						okrUserCache.getLoginUserName(),
						okrUserCache.getLoginIdentityName(),
						"授权收回",
						"工作授权收回成功！"
				);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkTackbackProcess( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return result;
	}
	
	public static class Wi {
		
		@FieldDescribe( "授权工作ID." )
		private String workId = null;

		@FieldDescribe( "授权者身份." )
		private String authorizeIdentity = null;
		
		@FieldDescribe( "承担者身份." )
		private String undertakerIdentity = null;

		@FieldDescribe( "授权意见." )
		private String authorizeOpinion = null;

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}

		public String getAuthorizeIdentity() {
			return authorizeIdentity;
		}

		public void setAuthorizeIdentity(String authorizeIdentity) {
			this.authorizeIdentity = authorizeIdentity;
		}

		public String getUndertakerIdentity() {
			return undertakerIdentity;
		}

		public void setUndertakerIdentity(String undertakerIdentity) {
			this.undertakerIdentity = undertakerIdentity;
		}

		public String getAuthorizeOpinion() {
			return authorizeOpinion;
		}

		public void setAuthorizeOpinion(String authorizeOpinion) {
			this.authorizeOpinion = authorizeOpinion;
		}
		
	}
	
	public static class Wo extends WoId {

	}
}