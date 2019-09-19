package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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


@Path("viewfieldconfig")
@JaxrsDescribe("列表列信息配置")
public class ViewFieldConfigAction extends StandardJaxrsAction{
	
	private static  Logger logger = LoggerFactory.getLogger( ViewFieldConfigAction.class );
	
	@JaxrsMethodDescribe(value = "获取全部的展示列配置信息列表.", action = ActionListAll.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllView( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListAll.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListAll().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "获取指定列表的全部展示列配置信息列表.", action = ActionListByViewId.class)
	@GET
	@Path("list/view/{viewId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listViewFieldConfigByViewId( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("列表ID") @PathParam("viewId")String viewId ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListByViewId.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListByViewId().execute( request, effectivePerson, viewId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID获取列表列信息配置对象.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("列配置ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute( request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "创建列表列信息配置对象.", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void post( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionSave.Wo> result = new ActionResult<>();		
		Boolean check = true;
		
		if( check ){
			try {
				result = new ActionSave().execute( request, effectivePerson, null, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "更新列表列信息配置对象.", action = ActionSave.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void put( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("列配置ID") @PathParam("id") String id, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			try {
				result = new ActionSave().execute( request, effectivePerson, id, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID删除列表列信息配置对象.", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("列配置ID") @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute( request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
}