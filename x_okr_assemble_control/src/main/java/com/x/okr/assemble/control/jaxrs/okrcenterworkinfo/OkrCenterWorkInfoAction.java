package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

@Path( "okrcenterworkinfo" )
public class OkrCenterWorkInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrCenterWorkInfoAction.class );
	
	@HttpMethodDescribe(value = "新建或者更新OkrCenterWorkInfo对象.", request = JsonElement.class, response = WrapOutOkrCenterWorkInfo.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInOkrCenterWorkInfo wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrCenterWorkInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取OkrCenterWorkInfo对象.", response = WrapOutOkrCenterWorkInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "创建中心工作草稿, 未保存.", response = WrapOutOkrCenterWorkInfo.class)
	@GET
	@Path( "draft" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response draftNew(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		try {
			result = new ExcuteDraftNewCenter().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrCenterWorkInfo数据对象.", response = WrapOutOkrCenterWorkInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID归档OkrCenterWorkInfo数据对象.", response = WrapOutOkrCenterWorkInfo.class)
	@GET
	@Path( "archive/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response archive(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteArchive().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "正式部署中心工作.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInFilterCenterWorkInfo.class)
	@GET
	@Path( "deploy/{centerId}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deploy( @Context HttpServletRequest request, @PathParam( "centerId" ) String centerId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDeploy().execute( request, effectivePerson, centerId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],下一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "draft/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListDraftNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],上一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "draft/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListDraftPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[部署的],下一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "deployed/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployedNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListDeployNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],上一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "deployed/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployedPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListDeployPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[我可以阅知的],下一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "read/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyReadNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListReadNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[我可以阅知的],上一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "read/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyReadPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListReadPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[已归档],下一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "archive/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchiveNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListArchiveNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[已归档],上一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "archive/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchivePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListArchivePrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的OkrCenterWorkInfo,下一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的OkrCenterWorkInfo,上一页.", response = WrapOutOkrCenterWorkViewInfo.class, request = JsonElement.class)
	@PUT
	@Path( "filter/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteListPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	
}