package com.x.bbs.assemble.control.jaxrs.userinfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.userinfo.exception.PropertyEmptyException;
import com.x.bbs.assemble.control.jaxrs.userinfo.exception.UserInfoQueryByNameException;
import com.x.bbs.assemble.control.jaxrs.userinfo.exception.UserInfoWrapOutException;
import com.x.bbs.assemble.control.jaxrs.userinfo.exception.WrapInConvertException;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSUserInfo;

@Path("userinfo")
public class UserInfoAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( UserInfoAction.class );
	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	private UserManagerService userManagerService = new UserManagerService();

	@HttpMethodDescribe(value = "列示根据过滤条件的UserInfo", response = WrapOutUserInfo.class, request = JsonElement.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterUserInfo( @Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutUserInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutUserInfo wrap = null;
		BBSUserInfo userInfo = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}

		if( check ){
			if( wrapIn.getUserName() == null ){
				check = false;
				Exception exception = new PropertyEmptyException( "用户姓名(userName)" );
				result.error( exception );
			}
		}
		//查询版块信息是否存在
		if (check) {
			try {
				userInfo = userInfoService.getByUserName( wrapIn.getUserName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new UserInfoQueryByNameException( e, wrapIn.getUserName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if (check) {
			if ( userInfo == null ) {
				try {
					userInfo = userManagerService.refreshUserRoleAndPermission( wrapIn.getUserName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new UserInfoQueryByNameException( e, wrapIn.getUserName() );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			try {
				wrap = WrapTools.userInfo_wrapout_copier.copy( userInfo );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new UserInfoWrapOutException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}		
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}