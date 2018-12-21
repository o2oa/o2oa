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
import com.x.okr.assemble.control.dataadapter.webservice.sms.SmsMessageOperator;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionAuthorizeOpinionEmpty;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionAuthorizeTakerIdentityEmpty;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionAuthorizeWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionPersonNotExists;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionSystemConfigFetch;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionSystemConfigNotFetch;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWorkAuthorizeNotOpen;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWorkAuthorizeProcess;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeService;
import com.x.okr.entity.OkrConfigSystem;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ActionWorkAuthorize extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionWorkAuthorize.class );
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	
	/**
	 * 工作处理授权服务<br/>
	 * 
	 * 问题：<br/>
	 * 1、授权后，原有责任者是否有处理权限，是否应用把身份转换为授权者，取消继续处理权限？<br/>
	 * 2、工作被拆解或者直接确认后，授权者是否需要收到一个通知，或者待阅等<br/>
	 * 
	 * 处理思路：<br/>
	 * 1、将工作责任者身份替换为被授权者<br/>
	 * 2、将干系人信息中的责任者身份转换为授权者（或者不用转换）<br/>
	 * 3、删除原责任者的待办，转换为原责任者已办<br/>
	 * 4、新增新的责任者待办<br/>
	 * <br/>
	 * PUT PARAMETER : workId (工作ID)<br/>
	 * PUT PARAMETER : authorizeOpinion (工作授权意见)<br/>
	 * PUT PARAMETER : authorizeIdentity (工作授权者身份)<br/>
	 * PUT PARAMETER : undertakerIdentity (工作授权承担人身份)<br/>
	 * 
	 * @param request
	 * @return
	 */
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<Wo>();
		OkrConfigSystem okrConfigSystem  = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrUserCache  okrUserCache  = null;
		String personName = null;
		String workId = null;
		String authorizeOpinion = null;
		String authorizeIdentity = null;
		String undertakerIdentity = null;
		Wi wrapIn = null;
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
			if( wrapIn != null ){
				workId = wrapIn.getWorkId();
				authorizeOpinion = wrapIn.getAuthorizeOpinion();
				undertakerIdentity = wrapIn.getUndertakerIdentity();
			}
		}
		if( check ){
			try {//1、判断系统是否已经开启授权操作WORK_AUTHORIZE
				okrConfigSystem = okrConfigSystemService.getWithConfigCode( "WORK_AUTHORIZE" );
				if( okrConfigSystem == null ){
					check = false;
					Exception exception = new ExceptionSystemConfigNotFetch( "WORK_AUTHORIZE");
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSystemConfigFetch( e, "WORK_AUTHORIZE");
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( !"OPEN".equals( okrConfigSystem.getConfigValue() )){
				check = false;
				Exception exception = new ExceptionWorkAuthorizeNotOpen();
				result.error( exception );
			}
		}
		if( check ){
			personName = okrUserManagerService.getPersonNameByIdentity( undertakerIdentity );
			if( personName == null ){
				check = false;
				Exception exception = new ExceptionPersonNotExists( undertakerIdentity );
				result.error( exception );
			}
		}
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch (Exception e ) {
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
		}
		if( check ){
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
				result.error( exception );
			}
			authorizeIdentity = okrUserCache.getLoginIdentityName();
		}
		if( check ){
			if( workId == null || workId.isEmpty() ){
				check = false;
				Exception exception = new ExceptionAuthorizeWorkIdEmpty();
				result.error( exception );
			}
		}
		if( check ){
			if( authorizeOpinion == null || authorizeOpinion.isEmpty() ){
				check = false;
				Exception exception = new ExceptionAuthorizeOpinionEmpty();
				result.error( exception );
			}
		}
		if( check ){
			if( undertakerIdentity == null || undertakerIdentity.isEmpty() ){
				check = false;
				Exception exception = new ExceptionAuthorizeTakerIdentityEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {//2、工作信息是否已经存在
				okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new ExceptionWorkNotExists( workId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkQueryById( e, workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			OkrWorkAuthorizeService okrWorkAuthorizeService = new OkrWorkAuthorizeService();
			try {
				okrWorkAuthorizeService.authorize( okrWorkBaseInfo, authorizeIdentity, undertakerIdentity, authorizeOpinion );
				
				WrapInWorkDynamic.sendWithWorkInfo( 
						okrWorkBaseInfo, 
						effectivePerson.getDistinguishedName(),
						okrUserCache.getLoginUserName(),
						okrUserCache.getLoginIdentityName(),
						"工作授权",
						"工作授权成功！"
				);

				//工作授权承接收短信
				SmsMessageOperator.sendWithPersonName( personName, "您有工作'"+okrWorkBaseInfo.getTitle()+"'已经授权给您，请及时办理！");

			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkAuthorizeProcess( e, workId );
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