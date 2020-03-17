package com.x.okr.assemble.control.jaxrs.identity;

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
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("error/identity")
@JaxrsDescribe("工作管理系统数据身份检查服务")
public class OkrSystemErrorIdentityProcessAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(OkrSystemErrorIdentityProcessAction.class);

	@JaxrsMethodDescribe(value = "对系统中的所有数据信息进行身份检查", action = ActionCheck.class)
	@GET
	@Path("check")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void check(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			result = new ActionCheck().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统对系统中的所有数据信息进行身份检查过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "Test对所有数据的人员姓名，身份以及组织名称进行替换", action = ActionReplaceOrganWithCheckTable.class)
	@GET
	@Path("test_replace")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void testReplaceAll(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			result = new ActionReplaceOrganWithCheckTable().execute(request, effectivePerson);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统对系统中的所有数据信息进行身份检查过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据要求将为指定的数据替换身份信息", action = ActionChangeIdentity.class)
	@PUT
	@Path("change")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changeIdentity(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			result = new ActionChangeIdentity().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统根据要求将为指定的数据替换身份信息过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据条件分页查询有问题的身份信息", action = ActionErrorInfoFilterListNextWithFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void errorInfoFilterListNextWithFilter(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("上一页最后一条信息的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionErrorInfoFilterListNextWithFilter.Wo>> result = new ActionResult<>();
		try {
			result = new ActionErrorInfoFilterListNextWithFilter().execute(request, effectivePerson, id, count,
					jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统根据条件分页查询有问题的身份信息过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据条件查询身份有问题的工作管理数据信息", action = ActionGetErrorRecords.class)
	@PUT
	@Path("detail")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getErrorRecords(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionGetErrorRecords.Wo>> result = new ActionResult<>();
		try {
			result = new ActionGetErrorRecords().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统根据条件查询身份有问题的工作管理数据信息过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据条件分页查询身份有问题工作管理数据信息", action = ActionGetOkrErrorIdentityRecords.class)
	@PUT
	@Path("errorrecords/filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getOkrErrorIdentityRecords(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("上一页最后一条信息的ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionGetOkrErrorIdentityRecords.Wo>> result = new ActionResult<>();
		try {
			result = new ActionGetOkrErrorIdentityRecords().execute(request, effectivePerson, id, count, jsonElement);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn("系统根据条件分页查询身份有问题工作管理数据信息过程发生异常。");
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
