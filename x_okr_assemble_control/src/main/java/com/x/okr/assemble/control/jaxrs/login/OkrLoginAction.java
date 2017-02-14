package com.x.okr.assemble.control.jaxrs.login;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrConfigSecretaryService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.entity.OkrUserInfo;
import com.x.organization.core.express.wrap.WrapPerson;


@Path( "login" )
public class OkrLoginAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrLoginAction.class );
	private OkrConfigSecretaryService okrConfigSecretaryService = new OkrConfigSecretaryService();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	
	@HttpMethodDescribe(value = "用户进入系统，获取并且缓存用户身份信息.", request = WrapInOkrLoginInfo.class, response = OkrUserCache.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login( @Context HttpServletRequest request, WrapInOkrLoginInfo wrapIn ) {
		ActionResult<OkrUserCache> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		List<String> ids = null;
		String identity = null;
		OkrUserCache okrUserCache = null;
		boolean hasIdentity = false;
		boolean check = true;
		
		logger.debug( "Okr login:" + this.effectivePerson( request ) );
		if( wrapIn != null ){
			//logger.debug( currentPerson.getName() + "选择的身份名称:" + wrapIn.getLoginIdentity() );
			if( wrapIn.getLoginIdentity() != null && !wrapIn.getLoginIdentity().isEmpty() ){
				//查询用户是否拥有该身份（兼职）
				try{
					hasIdentity = okrUserManagerService.hasIdentity( currentPerson.getName(), wrapIn.getLoginIdentity() );
					if( hasIdentity ){
						//以自己的身份登录
						okrUserCache = setUserLoginIdentity( currentPerson.getName(), wrapIn.getLoginIdentity() );
						result.setUserMessage( "用户以["+wrapIn.getLoginIdentity()+"]身份登录成功!" );
						result.setData( okrUserCache );
						//logger.debug( "用户["+currentPerson.getName()+"]以["+wrapIn.getLoginIdentity()+"]身份登录成功!" );
					}else{
						//查询用户是否有该身份的代理配置（秘书）
						try {
							ids = okrConfigSecretaryService.listIdsByLeaderIdentity( currentPerson.getName(), wrapIn.getLoginIdentity() );
							if( ids == null || ids.isEmpty() ){
								check = false;
								result.error( new Exception( "用户'" + currentPerson.getName()+"'没有用户'"+wrapIn.getLoginIdentity()+"'的代理身份，无法继续登录应用，请联系管理员!" ) );
								result.setUserMessage( "用户'" + currentPerson.getName()+"'没有用户'"+wrapIn.getLoginIdentity()+"'的代理身份，无法继续登录应用，请联系管理员!" );
							}else{
								//顺利查询到身份代理配置信息，向cache里新增用户相关信息
								okrUserCache = setUserLoginIdentity( currentPerson.getName(), wrapIn.getLoginIdentity() );
								result.setUserMessage( "用户以["+wrapIn.getLoginIdentity()+"]身份登录成功!" );
								result.setData( okrUserCache );
								//logger.debug( "用户["+currentPerson.getName()+"]以["+wrapIn.getLoginIdentity()+"]身份登录成功!" );
							}
						} catch (Exception e1) {
							check = false;
							result.setUserMessage( "根据员工和代理员工姓名查询秘书配置信息时发生异常" );
							result.error( e1);
							logger.error( "system query okrConfigSecretary by SecretaryName and LeaderName got an exception:", e1 );
						}
					}
				}catch( Exception e ){
					check = false;
					result.setUserMessage( "查询员工是否拥有兼职身份'"+wrapIn.getLoginIdentity()+"'时发生异常。" );
					result.error( e );
					logger.error( "system query identity '"+ wrapIn.getLoginIdentity()+"' for user '"+currentPerson.getName()+"' got an exception:", e );
				}
			}else{
				//没有传入身份，即以自己的第一个身份登入系统
				try{
					identity = okrUserManagerService.getFistIdentityNameByPerson( currentPerson.getName() );
					okrUserCache = setUserLoginIdentity( currentPerson.getName(), identity );
					result.setUserMessage( "用户以["+ identity +"]身份登录成功!" );
					result.setData( okrUserCache );
					//logger.debug( "用户["+currentPerson.getName()+"]以自己的身份["+identity+"]登录成功!" );
				}catch( Exception e ){
					check = false;
					result.setUserMessage( "系统根据当前操作人获取第一个系统身份时发生异常，无法继续登录，请联系管理员！" );
					result.error( e );
					logger.error( "system query the first identity for user '"+ currentPerson.getName() +"' got an exception:", e );
				}
			}
		}else{
			check = false;
			result.error( new Exception( "请求传入的参数'wrapIn'为空，无法继续登录应用!" ));
			result.setUserMessage( "请求传入的参数'wrapIn'为空，无法继续登录应用!" );
		}
		
		if( check ){
			if( okrUserCache != null ){
				boolean isOkrSystemAdmin = false; //系统管理员
				//boolean isOkrCompanyWorkAdmin = false; //公司工作管理员
				//boolean isOkrCompanyReportAdmin = false; //公司工作汇报管理员
				
				//说明登录是正常的，需要进一步获取用户的角色信息
				try {
					isOkrSystemAdmin = okrUserManagerService.isHasRole( currentPerson.getName(), "OkrSystemAdmin" );
					//isOkrCompanyWorkAdmin = okrUserManagerService.isHasRole( currentPerson.getName(), "OkrCompanyWorkAdmin" );
					//isOkrCompanyReportAdmin = okrUserManagerService.isHasRole( currentPerson.getName(), "OkrCompanyReportAdmin" );
				} catch ( Exception e ) {
					logger.error( "system check role name got an exception." , e );
				}				
				//okrUserCache.setOkrCompanyReportAdmin( isOkrCompanyReportAdmin );
				//okrUserCache.setOkrCompanyWorkAdmin( isOkrCompanyWorkAdmin );
				okrUserCache.setOkrSystemAdmin( isOkrSystemAdmin );
				result.setData( okrUserCache );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 设置用户登录的代理身份
	 * 
	 * @param name
	 * @throws Exception
	 */
	public OkrUserCache setUserLoginIdentity( String name, String identity ) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String customContent = null;
		OkrUserCache okrUserCache = new OkrUserCache();
		okrUserCache.setLoginIdentityName(identity);
		// 查询name的第一个身份所属的组织和公司
		okrUserCache.setOperationUserName( name );
		okrUserCache.setOperationUserOrganizationName( okrUserManagerService.getDepartmentNameByEmployeeName(name) );
		okrUserCache.setOperationUserCompanyName( okrUserManagerService.getCompanyNameByEmployeeName(name) );
		// 查询identity所属的组织和公司
		WrapPerson person = okrUserManagerService.getUserNameByIdentity(identity);
		okrUserCache.setLoginUserName(person.getName());
		okrUserCache.setLoginUserOrganizationName(okrUserManagerService.getDepartmentNameByIdentity(identity));
		okrUserCache.setLoginUserCompanyName(okrUserManagerService.getCompanyNameByIdentity(identity));
		
		//===============================================================================================
		customContent = gson.toJson( okrUserCache, OkrUserCache.class );
		OkrUserInfo userInfo = new OkrUserInfo();
		userInfo.setUserName( name );
		userInfo.setCustomContent(customContent);
		okrUserInfoService.save( userInfo );
		return okrUserCache;
	}
	
	/**
	 * 根据用户姓名，获取一个用户登录信息缓存
	 * 
	 * @param name
	 * @return
	 */
	public OkrUserCache getCacheElementByName( String name ) {
		//尝试从数据库中根据用户姓名来获取登录的对象
		Gson gson = XGsonBuilder.instance();
		OkrUserCache okrUserCache = null;
		OkrUserInfo okrUserInfo = null;
		try {
			okrUserInfo = okrUserInfoService.getWithPersonName( name );
		} catch (Exception e) {
			logger.error( "system query user info with person name got an exception", e );
		}
		if( okrUserInfo != null ){
			if( okrUserInfo.getCustomContent() != null && !okrUserInfo.getCustomContent().isEmpty() ){
				//有信息，则进行JSON转换
				okrUserCache = gson.fromJson( okrUserInfo.getCustomContent(), OkrUserCache.class );
				return okrUserCache;
			}
		}
		return okrUserCache;
	}
}
