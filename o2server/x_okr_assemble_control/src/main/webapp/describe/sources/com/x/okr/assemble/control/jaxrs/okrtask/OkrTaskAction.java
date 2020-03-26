package com.x.okr.assemble.control.jaxrs.okrtask;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 待办信息管理服务
 * 
 * @author O2LEE
 *
 */
@Path("okrtask")
@JaxrsDescribe("待办信息管理服务")
public class OkrTaskAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrTaskAction.class);

	@JaxrsMethodDescribe(value = "获取登录用户的所有工作汇报汇总的内容", action = ActionListTaskCollect.class)
	@GET
	@Path("unit/reportTaskCollect/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void showTaskCollect(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇总待办信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListTaskCollect.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListTaskCollect().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteListTaskCollect got an exception.id:" + id);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "处理指定的待阅信息", action = ActionReadProcess.class)
	@GET
	@Path("process/read/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void processRead(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("待办信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionReadProcess.Wo> result = new ActionResult<>();
		try {
			result = new ActionReadProcess().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteReadProcess got an exception.id:" + id);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取待办或者待阅信息", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("待办信息ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ExcuteGet got an exception.id:" + id);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据过滤条件列示我的待办列表,下一页", action = ActionListMyTaskNextWithFilter.class)
	@PUT
	@Path("filter/my/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMyTaskNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("最后一条信息数据的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListMyTaskNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListMyTaskNextWithFilter().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute ActionListMyTaskNextWithFilter got an exception.id:" + id);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "给指定处理人发送短消息提醒", action = ActionSendSms.class)
	@PUT
	@Path("sms/send/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendSms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("具体工作ID") @PathParam("workId") String workId, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionSendSms().execute(request, effectivePerson, workId, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("system excute sendSms got an exception.workId:" + workId);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
