package com.x.organization.assemble.authentication.jaxrs.oauth;

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

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
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

    @JaxrsMethodDescribe(value = "GET方法实现oauth认证auth方法", action = ActionAuth.class)
    @GET
    @Path("auth")
    public void auth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("表示授权类型，必选项，此处的值固定为code") @QueryParam("response_type") String response_type,
            @JaxrsParameterDescribe("表示客户端的ID") @QueryParam("client_id") String client_id,
            @JaxrsParameterDescribe("表示重定向URI") @QueryParam("redirect_uri") String redirect_uri,
            @JaxrsParameterDescribe("表示申请的权限范围") @QueryParam("scope") String scope,
            @JaxrsParameterDescribe("表示客户端的当前状态，可以指定任意值，认证服务器会原封不动地返回这个值") @QueryParam("state") String state) {
        ActionResult<ActionAuth.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAuth().execute(effectivePerson, response_type, client_id, redirect_uri, scope, state);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "POST方法实现oauth认证token方法", action = ActionToken.class)
    @POST
    @Path("token")
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
    public void postToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("码") @FormParam("code") String code,
            @JaxrsParameterDescribe("授权类型") @FormParam("grant_type") String grant_type,
            @JaxrsParameterDescribe("response CONTENT_TYPE 设置 默认为text/plain; charset=UTF-8") @FormParam("contentType") String contentType) {
        ActionResult<ActionToken.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionToken().execute(effectivePerson, code, grant_type, contentType);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "POST方法实现oauth认证token方法,适配jira.", action = ActionToken.class)
    @POST
    @Path("token/jira")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON })
    public void postTokenJira(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response, @JaxrsParameterDescribe("码") @FormParam("code") String code,
            @JaxrsParameterDescribe("授权类型") @FormParam("grant_type") String grant_type) {
        ActionResult<ActionToken.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionToken().execute(effectivePerson, code, grant_type, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "GET方法实现oauth认证token方法", action = ActionToken.class)
    @GET
    @Path("token")
    public void getToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response, @JaxrsParameterDescribe("code") @QueryParam("code") String code,
            @JaxrsParameterDescribe("grant_type") @QueryParam("grant_type") String grant_type,
            @JaxrsParameterDescribe("response CONTENT_TYPE 设置 默认为text/plain; charset=UTF-8") @QueryParam("contentType") String contentType) {
        ActionResult<ActionToken.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionToken().execute(effectivePerson, code, grant_type, contentType);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "POST方法实现oauth认证info方法.", action = ActionInfo.class)
    @POST
    @Path("info")
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
    public void postInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("access_token") @FormParam("access_token") String access_token,
            @JaxrsParameterDescribe("response CONTENT_TYPE 设置 默认为text/plain; charset=UTF-8") @FormParam("contentType") String contentType) {
        ActionResult<ActionInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionInfo().execute(request, effectivePerson, access_token, contentType);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "POST方法实现oauth认证info方法,适配jira.", action = ActionInfo.class)
    @POST
    @Path("info/jira")
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void postInfoJira(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("访问令牌") @FormParam("access_token") String access_token) {
        ActionResult<ActionInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionInfo().execute(request, effectivePerson, access_token, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "GET方法实现oauth认证info方法.", action = ActionInfo.class)
    @GET
    @Path("info")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void getInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("access_token") @QueryParam("access_token") String access_token,
            @JaxrsParameterDescribe("response CONTENT_TYPE 设置 默认为text/plain; charset=UTF-8") @QueryParam("contentType") String contentType) {
        ActionResult<ActionInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionInfo().execute(request, effectivePerson, access_token, contentType);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "GET方法实现oauth认证info方法,适配jira.", action = ActionInfo.class)
    @GET
    @Path("info/jira")
    @Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_FORM_URLENCODED })
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void getInfoJira(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @Context HttpServletResponse response,
            @JaxrsParameterDescribe("access_token") @QueryParam("access_token") String access_token) {
        ActionResult<ActionInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionInfo().execute(request, effectivePerson, access_token, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}