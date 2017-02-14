package com.x.okr.assemble.control.jaxrs.login;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.OkrUserCache;


@Path( "logout" )
public class OkrLogoutAction extends StandardJaxrsAction{
	
	//private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	
	@HttpMethodDescribe(value = "用户登出，删除已经缓存的用户身份信息.", request = WrapInOkrLoginInfo.class, response = OkrUserCache.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(@Context HttpServletRequest request, WrapInOkrLoginInfo wrapIn ) {
		ActionResult<OkrUserCache> result = new ActionResult<>();
		//EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			//okrUserInfoService.deleteWithPersonName( currentPerson.getName() );
			result.setUserMessage( "用户成功登出!" );
		} catch (Exception e) {
			result.error(e);
			result.setUserMessage("系统在删除用户登录信息时发生异常！");
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
