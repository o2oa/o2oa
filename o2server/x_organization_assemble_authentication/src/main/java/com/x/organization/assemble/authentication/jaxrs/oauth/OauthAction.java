package com.x.organization.assemble.authentication.jaxrs.oauth;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("oauth")
@JaxrsDescribe("Oauth2点单登录")
public class OauthAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OauthAction.class);

	// response_type：表示授权类型，必选项，此处的值固定为"code"
	// client_id：表示客户端的ID，必选项
	// redirect_uri：表示重定向URI，可选项
	// scope：表示申请的权限范围，可选项
	// state：表示客户端的当前状态，可以指定任意值，认证服务器会原封不动地返回这个值。

	@JaxrsMethodDescribe(value = "POST方法实现oauth认证auth方法", action = ActionAuth.class)
	@POST
	@Path("auth")
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
	public void postAuth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, @FormParam("response_type") String response_type,
			@FormParam("client_id") String client_id, @FormParam("redirect_uri") String redirect_uri,
			@FormParam("scope") String scope, @FormParam("state") String state) {
		ActionResult<ActionAuth.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAuth().execute(effectivePerson, response_type, client_id, redirect_uri, scope, state);
			asyncResponse.resume(Response.seeOther(new URI(result.getData().getValue())).build());
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
	}

	@JaxrsMethodDescribe(value = "GET方法实现oauth认证auth方法", action = ActionAuth.class)
	@GET
	@Path("auth")
	public void getAuth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, @QueryParam("response_type") String response_type,
			@QueryParam("client_id") String client_id, @QueryParam("redirect_uri") String redirect_uri,
			@QueryParam("scope") String scope, @QueryParam("state") String state) {
		ActionResult<ActionAuth.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAuth().execute(effectivePerson, response_type, client_id, redirect_uri, scope, state);
			asyncResponse.resume(Response.seeOther(new URI(result.getData().getValue())).build());
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
	}

	@JaxrsMethodDescribe(value = "POST方法实现oauth认证token方法", action = ActionToken.class)
	@POST
	@Path("token")
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
	public void postToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, @FormParam("code") String code,
			@FormParam("grant_type") String grant_type) {
		ActionResult<ActionToken.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionToken().execute(effectivePerson, code, grant_type);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "GET方法实现oauth认证token方法", action = ActionToken.class)
	@GET
	@Path("token")
	public void getToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, @QueryParam("code") String code,
			@QueryParam("grant_type") String grant_type) {
		ActionResult<ActionToken.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionToken().execute(effectivePerson, code, grant_type);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "POST方法实现oauth认证info方法.", action = ActionInfo.class)
	@POST
	@Path("info")
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
	public void postInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, @FormParam("access_token") String access_token) {
		ActionResult<ActionInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionInfo().execute(effectivePerson, access_token);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "GET方法实现oauth认证info方法.", action = ActionInfo.class)
	@GET
	@Path("info")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void getInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, @QueryParam("access_token") String access_token) {
		ActionResult<ActionInfo.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionInfo().execute(effectivePerson, access_token);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}