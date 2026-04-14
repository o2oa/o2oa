package com.x.teamwork.assemble.control.jaxrs.extfield;

import java.util.List;

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

@Path("extfield")
@JaxrsDescribe("扩展属性关联信息管理")
public class CustomExtFieldReleAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(CustomExtFieldReleAction.class);
	
	@JaxrsMethodDescribe(value = "查询所有扩展属性信息.", action = ActionListAllExtFields.class)
	@GET
	@Path("list/fields/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllUseableFields(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request ) {
		ActionResult<ActionListAllExtFields.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);	
		try {
			result = new ActionListAllExtFields().execute( request, effectivePerson );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据指定ID查询扩展属性关联信息.", action = ActionGet.class)
	@GET
	@Path("rele/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, @JaxrsParameterDescribe("关联信息ID") @PathParam("id") String id ) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);	
		try {
			result = new ActionGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据关联ID，需要的扩展属性类别获取下一个可用的属性名称.", action = ActionGetNextUseableExtFieldName.class)
	@GET
	@Path("next/field/{correlationId}/{fieldType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getNextUseableFieldName(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("关联ID") @PathParam("correlationId") String correlationId,
			@JaxrsParameterDescribe("属性类别：TEXT|SELECT|MUTISELECT|RICHTEXT|DATE|DATETIME|PERSON|IDENTITY|UNIT|GROUP|") @PathParam("fieldType") String fieldType ) {
		ActionResult<ActionGetNextUseableExtFieldName.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);	
		try {
			result = new ActionGetNextUseableExtFieldName().execute( request, effectivePerson, correlationId, fieldType );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据关联ID查询用户创建的所有扩展属性关联信息列表.", action = ActionListWithCorrelation.class)
	@GET
	@Path("list/{correlationId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listFieldsWithCorrelation(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("关联ID") @PathParam("correlationId") String correlationId ) {
		ActionResult<List<ActionListWithCorrelation.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);	
		try {
			result = new ActionListWithCorrelation().execute( request, effectivePerson, correlationId );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据扩展属性类型查询扩展属性信息列表.", action = ActionListWithType.class)
	@GET
	@Path("listPublicFields/{type}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listFieldsWithType(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("扩展属性类型") @PathParam("type") String type ) {
		ActionResult<List<ActionListWithType.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);	
		try {
			result = new ActionListWithType().execute( request, effectivePerson, type );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "创建或者更新一个扩展属性关联信息.", action = ActionSave.class)
	@POST
	@Path("relevance")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("需要保存的扩展属性关联信息") JsonElement jsonElement ) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSave().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
	
	@JaxrsMethodDescribe(value = "根据标识删除扩展属性关联信息.", action = ActionDelete.class)
	@DELETE
	@Path("relevance/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("标识") @PathParam("id") String id ) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(request, effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}