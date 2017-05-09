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

import com.google.gson.Gson;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
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
		Boolean check = true;
		
		if( wrapIn != null ){
			if( wrapIn.getLoginIdentity() != null && !wrapIn.getLoginIdentity().isEmpty() ){
				//查询用户是否拥有该身份（兼职）
				try{
					hasIdentity = okrUserManagerService.hasIdentity( currentPerson.getName(), wrapIn.getLoginIdentity() );
				}catch( Exception e ){
					check = false;
					Exception exception = new UserProxyQueryException( e, currentPerson.getName(), wrapIn.getLoginIdentity() );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
				if( hasIdentity ){
					//以自己的身份登录
					try {
						okrUserCache = setUserLoginIdentity( request, currentPerson, wrapIn.getLoginIdentity() );
						result.setData( okrUserCache );
					} catch (Exception e) {
						Exception exception = new GetOkrUserCacheException( e, currentPerson.getName(), wrapIn.getLoginIdentity() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
				}else{
					//查询用户是否有该身份的代理配置（秘书）
					try {
						ids = okrConfigSecretaryService.listIdsByLeaderIdentity( currentPerson.getName(), wrapIn.getLoginIdentity() );
						if( ids == null || ids.isEmpty() ){
							check = false;
							Exception exception = new UserHasNoProxyIdentityException( currentPerson.getName(), wrapIn.getLoginIdentity() );
							result.error( exception );
							//logger.error( e, currentPerson, request, null);
						}else{
							//顺利查询到身份代理配置信息，向cache里新增用户相关信息
							okrUserCache = setUserLoginIdentity( request, currentPerson, wrapIn.getLoginIdentity() );
							result.setData( okrUserCache );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new UserProxyQueryException( e, currentPerson.getName(), wrapIn.getLoginIdentity() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
				}
			}else{ //没有传入身份，即以自己的第一个身份登入系统
				if( !"xadmin".equals( currentPerson.getName().toLowerCase() ) ){
					try{
						identity = okrUserManagerService.getFistIdentityNameByPerson( currentPerson.getName() );
						if( identity != null && !identity.isEmpty() ){
							okrUserCache = setUserLoginIdentity( request, currentPerson, identity );
							result.setData( okrUserCache );
						}
					}catch( Exception e ){
						check = false;
						Exception exception = new UserIdentityQueryException( e, currentPerson.getName() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
				}else{
					try {
						okrUserCache = setUserXadmin( request, currentPerson );
						result.setData( okrUserCache );
					} catch (Exception e) {
						Exception exception = new GetOkrUserCacheException( e, currentPerson.getName(), wrapIn.getLoginIdentity() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
					
				}
			}
		}else{
			check = false;
			result.error( new Exception( "请求传入的参数'wrapIn'为空，无法继续登录应用!" ));
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 设置用户登录的代理身份
	 * 
	 * @param name
	 * @throws Exception
	 */
	public OkrUserCache setUserLoginIdentity( HttpServletRequest request, EffectivePerson currentPerson, String identity ) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String customContent = null;
		Boolean isOkrSystemAdmin = false;
		OkrUserCache okrUserCache = new OkrUserCache();
		okrUserCache.setLoginIdentityName(identity);
		// 查询name的第一个身份所属的组织和公司
		okrUserCache.setOperationUserName( currentPerson.getName() );
		okrUserCache.setOperationUserOrganizationName( okrUserManagerService.getDepartmentNameByEmployeeName( currentPerson.getName() ) );
		okrUserCache.setOperationUserCompanyName( okrUserManagerService.getCompanyNameByEmployeeName( currentPerson.getName() ) );
		// 查询identity所属的组织和公司
		WrapPerson person = okrUserManagerService.getUserByIdentity(identity);
		okrUserCache.setLoginUserName(person.getName());
		okrUserCache.setLoginUserOrganizationName(okrUserManagerService.getDepartmentNameByIdentity(identity));
		okrUserCache.setLoginUserCompanyName(okrUserManagerService.getCompanyNameByIdentity(identity));
		
		try {
			isOkrSystemAdmin = okrUserManagerService.isHasRole( currentPerson.getName(), "OkrSystemAdmin" );
		} catch ( Exception e ) {
			Exception exception = new OkrSystemAdminCheckException( e, currentPerson.getName() );
			logger.error( e, currentPerson, request, null);
		}
		okrUserCache.setOkrSystemAdmin( isOkrSystemAdmin );
		
		//===============================================================================================
		customContent = gson.toJson( okrUserCache, OkrUserCache.class );
		OkrUserInfo userInfo = new OkrUserInfo();
		userInfo.setUserName( currentPerson.getName() );
		userInfo.setCustomContent(customContent);
		okrUserInfoService.save( userInfo );
		return okrUserCache;
	}
	
	/**
	 * 设置用户登录的代理身份
	 * 
	 * @param name
	 * @throws Exception
	 */
	public OkrUserCache setUserXadmin( HttpServletRequest request, EffectivePerson currentPerson ) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String customContent = null;
		OkrUserCache okrUserCache = new OkrUserCache();
		okrUserCache.setLoginIdentityName( "xadmin" );
		// 查询name的第一个身份所属的组织和公司
		okrUserCache.setOperationUserName( "xadmin" );
		okrUserCache.setOperationUserOrganizationName( "xadmin" );
		okrUserCache.setOperationUserCompanyName( "xadmin" );
		okrUserCache.setLoginUserName( "xadmin" );
		okrUserCache.setLoginUserOrganizationName( "xadmin" );
		okrUserCache.setLoginUserCompanyName( "xadmin" );
		okrUserCache.setOkrSystemAdmin( true );
		
		customContent = gson.toJson( okrUserCache, OkrUserCache.class );
		OkrUserInfo userInfo = new OkrUserInfo();
		userInfo.setUserName( "xadmin" );
		userInfo.setCustomContent(customContent);
		okrUserInfoService.save( userInfo );
		return okrUserCache;
	}
	
	/**
	 * 根据用户姓名，获取一个用户登录信息缓存
	 * 
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public OkrUserCache getCacheElementByName( String name ) throws Exception {
		//尝试从数据库中根据用户姓名来获取登录的对象
		Gson gson = XGsonBuilder.instance();
		OkrUserCache okrUserCache = null;
		OkrUserInfo okrUserInfo = null;
		okrUserInfo = okrUserInfoService.getWithPersonName( name );
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
