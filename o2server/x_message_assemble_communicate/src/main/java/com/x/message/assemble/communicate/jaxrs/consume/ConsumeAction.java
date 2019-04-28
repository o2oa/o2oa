package com.x.message.assemble.communicate.jaxrs.consume;

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

@Path("consume")
@JaxrsDescribe("消费")
public class ConsumeAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ConsumeAction.class);

	@JaxrsMethodDescribe(value = "获取消息.", action = ActionList.class)
	@GET
	@Path("list/{consume}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void list(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("消费者") @PathParam("consume") String consume,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionList.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionList().execute(effectivePerson, consume, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取消息,当前人员.", action = ActionListWithCurrentPerson.class)
	@GET
	@Path("list/{consume}/currentperson/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithCurrentPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("消费者") @PathParam("consume") String consume,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListWithCurrentPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithCurrentPerson().execute(effectivePerson, consume, count);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取消息,指定人员.", action = ActionListWithPerson.class)
	@GET
	@Path("list/{consume}/person/{person}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("消费者") @PathParam("consume") String consume,
			@JaxrsParameterDescribe("人员") @PathParam("person") String person,
			@JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionListWithPerson.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPerson().execute(effectivePerson, consume, count, person);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新消息,将消息标志为已处理.", action = ActionUpdateSingle.class)
	@GET
	@Path("{id}/type/{type}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateSingle(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id,
			@JaxrsParameterDescribe("消费类型") @PathParam("type") String type) {
		ActionResult<ActionUpdateSingle.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateSingle().execute(effectivePerson, id, type);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新消息,将消息标志为已处理.", action = ActionUpdate.class)
	@PUT
	@Path("type/{type}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("消费类型") @PathParam("type") String type, JsonElement jsonElement) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdate().execute(effectivePerson, type, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}