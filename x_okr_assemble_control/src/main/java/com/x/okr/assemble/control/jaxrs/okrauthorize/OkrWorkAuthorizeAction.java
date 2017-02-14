package com.x.okr.assemble.control.jaxrs.okrauthorize;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.OkrAttachmentFileInfoAction;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrConfigSystem;
import com.x.okr.entity.OkrWorkBaseInfo;

@Path( "okrauthorize" )
public class OkrWorkAuthorizeAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrAttachmentFileInfoAction.class );
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	/**
	 * 工作处理授权服务
	 * 
	 * 问题：
	 * 1、授权后，原有责任者是否有处理权限，是否应用把身份转换为授权者，取消继续处理权限？
	 * 2、工作被拆解或者直接确认后，授权者是否需要收到一个通知，或者待阅等
	 * 
	 * 处理思路：
	 * 1、将工作责任者身份替换为被授权者
	 * 2、将干系人信息中的责任者身份转换为授权者（或者不用转换）
	 * 3、删除原责任者的待办，转换为原责任者已办
	 * 4、新增新的责任者待办
	 * 
	 * PUT PARAMETER : workId
	 * PUT PARAMETER : authorizeOpinion
	 * PUT PARAMETER : authorizeIdentity
	 * 
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "工作处理授权服务.", response = WrapOutOkrAuthorize.class, request = WrapInFilter.class)
	@PUT
	@Path( "work" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response workAuthorize( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<WrapOutOkrAuthorize> result = new ActionResult<WrapOutOkrAuthorize>();
		OkrConfigSystem okrConfigSystem  = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("请求传入的参数为空，无法进行查询。") );
			result.setUserMessage( "请求传入的参数为空，无法进行查询." );
			logger.error( "wrapIn is null." );
		}
		
		if( check ){
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
				logger.error( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
			}
			wrapIn.setAuthorizeIdentity( okrUserCache.getLoginIdentityName()  );
		}
		
		if( check ){
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				result.error( new Exception("请求传入的要授权的工作ID为空，无法进行查询。") );
				result.setUserMessage( "请求传入的要授权的工作ID为空，无法进行查询." );
				logger.error( "authorize work id is null." );
			}
		}
		
		if( check ){
			if( wrapIn.getAuthorizeOpinion() == null || wrapIn.getAuthorizeOpinion().isEmpty() ){
				check = false;
				result.error( new Exception("请求传入的要授权意见为空，无法进行查询。") );
				result.setUserMessage( "请求传入的要授权意见为空，无法进行查询." );
				logger.error( "authorize opinion work id is null." );
			}
		}
		
		if( check ){
			if( wrapIn.getUndertakerIdentity() == null || wrapIn.getUndertakerIdentity().isEmpty() ){
				check = false;
				result.error( new Exception("请求传入的要授权工作的承担者身份为空，无法进行授权操作。") );
				result.setUserMessage( "请求传入的要授权工作的承担者身份为空，无法进行授权操作." );
				logger.error( "undertaker identity is null." );
			}
		}
		
		if( check ){
			//1、判断系统是否已经开启授权操作WORK_AUTHORIZE
			try {
				okrConfigSystem = okrConfigSystemService.getWithConfigCode( "WORK_AUTHORIZE" );
				if( okrConfigSystem == null ){
					check = false;
					result.error( new Exception("系统参数配置'WORK_AUTHORIZE'不存在。") );
					result.setUserMessage( "系统参数配置'WORK_AUTHORIZE'不存在." );
					logger.error( "system config 'WORK_AUTHORIZE' not exists." );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在查询参数配置'WORK_AUTHORIZE'时发生异常." );
				logger.error( "system search system config 'WORK_AUTHORIZE' got an exception.", e );
			}
		}
		
		//2、工作信息是否已经存在
		if( check ){
			if( "OPEN".equals( okrConfigSystem.getConfigValue() )){
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
					if( okrWorkBaseInfo == null ){
						check = false;
						result.error( new Exception("需要授权的工作信息不存在。") );
						result.setUserMessage( "需要授权的工作信息不存在." );
						logger.error( "okrWorkBaseInfo{'id':'"+wrapIn.getWorkId()+"'} not exists." );
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在查询工作信息时发生异常." );
					logger.error( "system search okrWorkBaseInfo{'id':'"+wrapIn.getWorkId()+"'} got an exception.", e );
				}
			}else{
				check = false;
				result.error( new Exception("系统工作授权功能未开启。") );
				result.setUserMessage( "系统工作授权功能未开启." );
				logger.error( "system config 'WORK_AUTHORIZE' is not open." );
			}
		}
		
		if( check ){
			OkrWorkAuthorizeService okrWorkAuthorizeService = new OkrWorkAuthorizeService();
			try {
				okrWorkAuthorizeService.authorize( okrWorkBaseInfo, wrapIn.getAuthorizeIdentity(), wrapIn.getUndertakerIdentity(), wrapIn.getAuthorizeOpinion() );
				okrWorkDynamicsService.workDynamic(
						okrWorkBaseInfo.getCenterId(), 
						okrWorkBaseInfo.getId(),
						okrWorkBaseInfo.getTitle(),
						"工作授权", 
						currentPerson.getName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"授权工作：" + okrWorkBaseInfo.getTitle(), 
						"工作授权成功"
				);
				result.setUserMessage( "工作授权成功！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统工作授权过程中发生异常." );
				logger.error( "work authorize got an exception.", e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 工作授权收回服务
	 * 
	 * PUT PARAMETER : workId
	 * 
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "工作授权收回服务.", response = WrapOutOkrAuthorize.class, request = WrapInFilter.class)
	@PUT
	@Path( "takeback" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response takeback( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<WrapOutOkrAuthorize> result = new ActionResult<WrapOutOkrAuthorize>();
		OkrConfigSystem okrConfigSystem  = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("请求传入的参数为空，无法进行查询。") );
			result.setUserMessage( "请求传入的参数为空，无法进行查询." );
			logger.error( "wrapIn is null." );
		}
		
		if( check ){
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
				logger.error( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
			}
			wrapIn.setAuthorizeIdentity( okrUserCache.getLoginIdentityName()  );
		}
		
		if( check ){
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				result.error( new Exception("请求传入的要授权的工作ID为空，无法进行查询。") );
				result.setUserMessage( "请求传入的要授权的工作ID为空，无法进行查询." );
				logger.error( "authorize work id is null." );
			}
		}
		
		if( check ){
			//1、判断系统是否已经开启授权操作WORK_AUTHORIZE
			try {
				okrConfigSystem = okrConfigSystemService.getWithConfigCode( "WORK_AUTHORIZE" );
				if( okrConfigSystem == null ){
					check = false;
					result.error( new Exception("系统参数配置'WORK_AUTHORIZE'不存在。") );
					result.setUserMessage( "系统参数配置'WORK_AUTHORIZE'不存在." );
					logger.error( "system config 'WORK_AUTHORIZE' not exists." );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在查询参数配置'WORK_AUTHORIZE'时发生异常." );
				logger.error( "system search system config 'WORK_AUTHORIZE' got an exception.", e );
			}
		}
		
		//2、工作信息是否已经存在
		if( check ){
			if( "OPEN".equals( okrConfigSystem.getConfigValue() )){
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
					if( okrWorkBaseInfo == null ){
						check = false;
						result.error( new Exception("需要授权的工作信息不存在。") );
						result.setUserMessage( "需要授权的工作信息不存在." );
						logger.error( "okrWorkBaseInfo{'id':'"+wrapIn.getWorkId()+"'} not exists." );
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在查询工作信息时发生异常." );
					logger.error( "system search okrWorkBaseInfo{'id':'"+wrapIn.getWorkId()+"'} got an exception.", e );
				}
			}else{
				check = false;
				result.error( new Exception("系统工作授权功能未开启。") );
				result.setUserMessage( "系统工作授权功能未开启." );
				logger.error( "system config 'WORK_AUTHORIZE' is not open." );
			}
		}
		
		if( check ){
			OkrWorkAuthorizeService okrWorkAuthorizeService = new OkrWorkAuthorizeService();
			try {
				okrWorkAuthorizeService.tackback( okrWorkBaseInfo, wrapIn.getAuthorizeIdentity() );
				okrWorkDynamicsService.workDynamic(
						okrWorkBaseInfo.getCenterId(), 
						okrWorkBaseInfo.getId(),
						okrWorkBaseInfo.getTitle(),
						"授权收回", 
						currentPerson.getName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"工作授权收回：" + okrWorkBaseInfo.getTitle(), 
						"工作授权收回成功"
				);
				result.setUserMessage( "工作授权收回成功！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统收回工作授权过程中发生异常." );
				logger.error( "work authorize take back got an exception.", e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}