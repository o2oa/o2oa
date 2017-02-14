package com.x.bbs.assemble.control.jaxrs.userinfo;

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

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSUserInfo;

@Path("userinfo")
public class UserInfoAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( UserInfoAction.class );
	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	private UserManagerService userManagerService = new UserManagerService();
	private BeanCopyTools< BBSUserInfo, WrapOutUserInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSUserInfo.class, WrapOutUserInfo.class, null, WrapOutUserInfo.Excludes);

	@HttpMethodDescribe(value = "列示根据过滤条件的UserInfo", response = WrapOutUserInfo.class, request = WrapInFilter.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterUserInfo( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<WrapOutUserInfo> result = new ActionResult<>();
		WrapOutUserInfo wrap = null;
		BBSUserInfo userInfo = null;
		Boolean check = true;
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法查询主题信息！" ) );
				result.setUserMessage( "传入的参数为空，无法查询主题信息！" );
			}
		}
		if( check ){
			if( wrapIn.getUserName() == null ){
				check = false;
				result.error( new Exception("传入的参数userName为空，无法继续查询用户信息！" ) );
				result.setUserMessage( "传入的参数userName为空，无法继续查询用户信息！" );
			}
		}
		//查询版块信息是否存在
		if (check) {
			try {
				userInfo = userInfoService.getByUserName( wrapIn.getUserName() );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据userName查询用户信息时发生异常！");
				logger.error("system query user info with userName got an exceptin. userName:" + wrapIn.getUserName(), e);
			}
		}
		if (check) {
			if ( userInfo == null ) {
				try {
					userInfo = userManagerService.refreshUserRoleAndPermission( wrapIn.getUserName() );
				} catch (Exception e) {
					check = false;
					result.error(e);
					result.setUserMessage("系统在根据userName查询用户信息时发生异常！");
					logger.error("system refresh user role and permissoin got an exceptin. userName:" + wrapIn.getUserName(), e);
				}
			}
		}
		if (check) {
			try {
				wrap = wrapout_copier.copy( userInfo );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage("系统在转换对象为输出格式时发生异常！");
				logger.error("system copy user info to wrap got an exceptin. userName:" + wrapIn.getUserName(), e);
			}		
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}