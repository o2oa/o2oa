package com.x.query.assemble.surface.jaxrs.importmodel;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

@Path("importmodel")
@JaxrsDescribe("数据导入")
public class ImportModelAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ImportModelAction.class);

	@JaxrsMethodDescribe(value = "生成唯一编码.", action = BaseAction.class)
	@GET
	@Path("uuid")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getUUID(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<String> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			String uuid = UUID.randomUUID().toString();
			result.setData(uuid);
		} catch (Exception e) {
			logger.warn("user[" + currentPerson.getDistinguishedName() + "] get a new UUID error！", e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据查询获取导入模型对象.", action = ActionGetWithQuery.class)
	@GET
	@Path("flag/{flag}/query/{queryFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithQuery(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("导入模型标识") @PathParam("flag") String flag,
			@JaxrsParameterDescribe("查询标识") @PathParam("queryFlag") String queryFlag) {
		ActionResult<ActionGetWithQuery.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithQuery().execute(effectivePerson, flag, queryFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据查询列示导入模型对象.", action = ActionListWithQuery.class)
	@GET
	@Path("list/query/{queryFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithQuery(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("查询标识") @PathParam("queryFlag") String queryFlag) {
		ActionResult<List<ActionListWithQuery.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithQuery().execute(effectivePerson, queryFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取导入模型.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取导入记录.", action = ActionGetRecord.class)
	@GET
	@Path("record/{recordId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getRecord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					@JaxrsParameterDescribe("导入记录标识") @PathParam("recordId") String recordId) {
		ActionResult<ActionGetRecord.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetRecord().execute(effectivePerson, recordId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "重新导入失败的记录(执行导入是异步过程，请关注记录的状态).", action = ActionReExecute.class)
	@GET
	@Path("execute/record/{recordId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void reExecuteRecord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						  @JaxrsParameterDescribe("导入记录标识") @PathParam("recordId") String recordId) {
		ActionResult<ActionReExecute.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionReExecute().execute(effectivePerson, recordId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取导入记录的执行状态.", action = ActionGetRecordStatus.class)
	@GET
	@Path("record/{recordId}/status")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getRecordStatus(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						  @JaxrsParameterDescribe("导入记录标识") @PathParam("recordId") String recordId) {
		ActionResult<ActionGetRecordStatus.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetRecordStatus().execute(effectivePerson, recordId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "执行数据导入(执行导入是异步过程，请关注记录的状态)", action = ActionExecute.class)
	@POST
	@Path("{id}/execute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void execute(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("导入模型标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionExecute.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExecute().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "导入记录分页查询.", action = ActionRecordListPaging.class)
	@POST
	@Path("list/record/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void recordListPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									 @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
									 @JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<ActionRecordListPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionRecordListPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "导入记录明细分页查询.", action = ActionRecordItemListPaging.class)
	@POST
	@Path("list/record/item/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void recordItemListPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
									 @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
									 @JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<ActionRecordItemListPaging.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionRecordItemListPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "删除导入记录.", action = ActionDeleteRecord.class)
	@DELETE
	@Path("record/{recordId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteRecord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						  @JaxrsParameterDescribe("导入记录标识") @PathParam("recordId") String recordId) {
		ActionResult<ActionDeleteRecord.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteRecord().execute(effectivePerson, recordId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除导入记录mockdeletetoget.", action = ActionDeleteRecord.class)
	@GET
	@Path("record/{recordId}/mockdeletetoget")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteRecordMockDeleteToGet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							 @JaxrsParameterDescribe("导入记录标识") @PathParam("recordId") String recordId) {
		ActionResult<ActionDeleteRecord.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteRecord().execute(effectivePerson, recordId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
