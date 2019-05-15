package com.x.message.assemble.communicate.jaxrs.instant;

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
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("instant")
@JaxrsDescribe("主体消息")
public class InstantAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(InstantAction.class);

	@JaxrsMethodDescribe(value = "列示所有的主体消息,需要管理员权限或者MessageManager,下一页.", action = ActionListNext.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListNext.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListNext().execute(effectivePerson, id, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列示所有的主体消息,需要管理员权限或者MessageManager,上一页.", action = ActionListPrev.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListPrev.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPrev().execute(effectivePerson, id, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取当前人员的主体消息,倒序.", action = ActionListWithCurrentPersonDesc.class)
	@GET
	@Path("list/currentperson/count/{count}/desc")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithCurrentPersonDesc(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListWithCurrentPersonDesc.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCurrentPersonDesc().execute(effectivePerson, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取当前人员的主体消息,顺序.", action = ActionListWithCurrentPersonAsc.class)
	@GET
	@Path("list/currentperson/count/{count}/asc")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithCurrentPersonAsc(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListWithCurrentPersonAsc.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCurrentPersonAsc().execute(effectivePerson, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取当前人员已经消费的主体消息,倒序.", action = ActionListWithCurrentPersonConsumedDesc.class)
	@GET
	@Path("list/currentperson/consumed/count/{count}/desc")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithCurrentConsumedPersonDesc(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListWithCurrentPersonConsumedDesc.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCurrentPersonConsumedDesc().execute(effectivePerson, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取当前人员已经消费的主体消息,顺序.", action = ActionListWithCurrentPersonConsumedAsc.class)
	@GET
	@Path("list/currentperson/consumed/count/{count}/asc")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithCurrentPersonConsumedAsc(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListWithCurrentPersonConsumedAsc.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCurrentPersonConsumedAsc().execute(effectivePerson, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取当前人员还未消费的主体消息,倒序.", action = ActionListWithCurrentPersonNotConsumedDesc.class)
	@GET
	@Path("list/currentperson/not/consumed/count/{count}/desc")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithCurrentNotConsumedPersonDesc(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListWithCurrentPersonNotConsumedDesc.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCurrentPersonNotConsumedDesc().execute(effectivePerson, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取当前人员还未消费的主体消息,顺序.", action = ActionListWithCurrentPersonNotConsumedAsc.class)
	@GET
	@Path("list/currentperson/not/consumed/count/{count}/asc")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithCurrentPersonNotConsumedAsc(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListWithCurrentPersonNotConsumedAsc.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCurrentPersonNotConsumedAsc().execute(effectivePerson, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "将当前用户指定的主体消息标识为已消费.", action = ActionCurrentPersonConsumed.class)
	@PUT
	@Path("currentperson/consumed")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void currentPersonConsumed(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionCurrentPersonConsumed.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCurrentPersonConsumed().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "将当前用户所有主体消息全部标识为已消费.", action = ActionCurrentPersonConsumedAll.class)
	@GET
	@Path("currentperson/consumed/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void currentPersonConsumedAll(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ActionCurrentPersonConsumedAll.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCurrentPersonConsumedAll().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}