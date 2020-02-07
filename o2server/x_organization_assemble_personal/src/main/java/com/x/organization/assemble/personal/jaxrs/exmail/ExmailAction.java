package com.x.organization.assemble.personal.jaxrs.exmail;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("exmail")
@JaxrsDescribe("腾讯企业邮")
public class ExmailAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ExmailAction.class);

	@JaxrsMethodDescribe(value = "获取当前用户的exmail邮件数量.", action = ActionNewCount.class)
	@GET
	@Path("new/count")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void newCount(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionNewCount.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionNewCount().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取当前用户的exmail邮件数量,通过回调写入PersonAttribute", action = ActionNewCountPassive.class)
	@GET
	@Path("new/count/passive")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void newCountPassive(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionNewCountPassive.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionNewCountPassive().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示当前用户腾讯企业邮标题,通过回调写入PersonAttribute", action = ActionListTitlePassive.class)
	@GET
	@Path("list/title/passive")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTitlePassive(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionListTitlePassive.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListTitlePassive().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取当前用户邮件单点登录地址.", action = ActionSso.class)
	@GET
	@Path("sso")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void sso(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionSso.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSso().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "接收腾讯企业邮回调Get方法.", action = ActionGet.class)
	@GET
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@QueryParam("msg_signature") String msg_signature, @QueryParam("timestamp") String timestamp,
			@QueryParam("nonce") String nonce, @QueryParam("echostr") String echostr) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute(effectivePerson, msg_signature, timestamp, nonce, echostr);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "接收腾讯企业邮回调Post方法.", action = ActionPost.class)
	@POST
	public void post(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@QueryParam("msg_signature") String msg_signature, @QueryParam("timestamp") String timestamp,
			@QueryParam("nonce") String nonce, String body) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionPost.Wo> result = new ActionResult<>();
		try {
			result = new ActionPost().execute(effectivePerson, msg_signature, timestamp, nonce, body);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}