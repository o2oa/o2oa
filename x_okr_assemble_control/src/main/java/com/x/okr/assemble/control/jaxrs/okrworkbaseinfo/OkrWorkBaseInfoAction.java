package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

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
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapOutOkrCenterWorkInfo;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.WorkBaseInfoProcessException;

/**
 * 具体工作项有短期工作还长期工作，短期工作不需要自动启动定期汇报，由人工撰稿汇报即可
 */

@Path( "okrworkbaseinfo" )
public class OkrWorkBaseInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrWorkBaseInfoAction.class );
	
	@HttpMethodDescribe( value = "新建或者更新OkrWorkBaseInfo对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInOkrWorkBaseInfo wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkBaseInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteSave got an exception. "  );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	

	@HttpMethodDescribe(value = "正式部署工作事项.", response = WrapOutOkrWorkBaseInfo.class, request = JsonElement.class)
	@PUT
	@Path( "deploy" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deploy( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		WrapInOkrWorkBaseInfo wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkBaseInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteDeploy().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteDeploy got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID归档OkrWorkBaseInfo数据对象.", response = WrapOutOkrWorkBaseInfo.class)
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
	
	@HttpMethodDescribe(value = "根据ID对OkrWorkBaseInfo进行工作进度调整.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "progress/{id}/{percent}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response progressAdjust(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "percent" ) Integer percent) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteProgressAdjust().execute( request, effectivePerson, id, percent );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "收回已经部署的工作事项.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "recycle/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response recycle( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		try {
			result = new ExcuteRecycle().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteRecycle got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除OkrWorkBaseInfo数据对象.", response = WrapOutOkrWorkBaseInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteDelete got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取OkrWorkBaseInfo对象.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteGet got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取OkrWorkBaseInfo详细信息，展示用.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "view/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response view(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrWorkBaseViewInfo> result = new ActionResult<>();
		try {
			result = new ExcuteViewWork().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteViewWork got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
	
	@HttpMethodDescribe(value = "根据上级工作ID获取OkrWorkBaseInfo对象.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "list/sub/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubWork(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListSubWork().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteListSubWork got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "判断当前用户是否有权限拆解指定工作.", response = WrapOutBoolean.class)
	@GET
	@Path( "canDismantlingWork/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response canDismantlingWork(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ExcuteWorkCanDismantling().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteWorkCanDismantling got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据中心工作ID获取我可以看到的所有OkrWorkBaseInfo对象.", response = WrapOutOkrCenterWorkInfo.class)
	@GET
	@Path( "center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWorkByCenterId(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		try {
			result = new ExcuteListUsersWorkByCenterId().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteListUsersWorkByCenterId got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[草稿],下一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "draft/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryInfoStatus( "正常" );	
				wrapIn.addQueryWorkProcessStatus( "草稿" );
				wrapIn.addQueryProcessIdentity( "部署者" );
				result = new ExcuteListMyWorkByProcessIdentityNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListDraftNextWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],上一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "draft/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryInfoStatus( "正常" );	
				wrapIn.addQueryWorkProcessStatus( "草稿" );
				wrapIn.addQueryProcessIdentity( "部署者" );
				result = new ExcuteListMyWorkByProcessIdentityNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListDraftPrevWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[部署的],下一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "deployed/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployedNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "部署者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListDeployNextWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[部署],上一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "deployed/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployedPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "部署者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListDeployPrevWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[阅知者],下一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "read/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyReadNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "阅知者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListReadNextWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[阅知者],上一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "read/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyReadPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "阅知者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListReadPrevWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[负责的],下一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "responsibility/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyResponsibilityNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "责任者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListResponsibilityNextWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[部署],上一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "responsibility/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyResponsibilityPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "责任者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListResponsibilityPrevWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[授权者],下一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "delegate/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDelegateNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "授权者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListDelegatedNextWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[授权者],上一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "delegate/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDelegatePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "授权者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListDelegatedPrevWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[协助者],下一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "cooperate/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyCooperateNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "协助者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListCooperateNextWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[协助者],上一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "cooperate/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyCooperatePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "协助者" );
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				result = new ExcuteListMyWorkByProcessIdentityPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListCooperatePrevWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[已归档],下一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "archive/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchiveNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryInfoStatus( "已归档" );
				wrapIn.addQueryProcessIdentity( "观察者" );
				result = new ExcuteListMyWorkByProcessIdentityNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListArchiveNextWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[已归档],上一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "archive/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchivePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryInfoStatus( "已归档" );
				wrapIn.addQueryProcessIdentity( "观察者" );
				result = new ExcuteListMyWorkByProcessIdentityPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListArchivePrevWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[所有工作],下一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyWorkNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "观察者" );
				if( wrapIn.getSequenceField() == null || wrapIn.getSequenceField().isEmpty() ){
					wrapIn.setSequenceField( "completeDateLimitStr" );
				}
				result = new ExcuteListMyWorkByProcessIdentityNextWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListWorkSimpleInfoNextWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[所有工作],下一页.", response = WrapOutOkrWorkBaseSimpleInfo.class, request = JsonElement.class)
	@PUT
	@Path( "filter/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyWorkPrevWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class );
			if( wrapIn == null ){
				wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
			}
		} catch (Exception e ) {
			check = false;
			Exception exception = new WorkBaseInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.setProcessIdentities( null );
				wrapIn.addQueryProcessIdentity( "观察者" );
				if( wrapIn.getSequenceField() == null || wrapIn.getSequenceField().isEmpty() ){
					wrapIn.setSequenceField( "completeDateLimitStr" );
				}
				result = new ExcuteListMyWorkByProcessIdentityPrevWithFilter().execute( request, effectivePerson, id, count, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteListWorkSimpleInfoPrevWithFilter got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据中心工作ID获取我部署的所有OkrWorkBaseInfo对象，并且以上级工作进行归类.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "deploy/form/center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployWorkInCenterForForm(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListDeployWorkInCenterForForm().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteListDeployWorkInCenterForForm got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据中心工作ID获取我需要参与[负责，协助，阅知]的所有OkrWorkBaseInfo对象，并且以上级工作进行归类.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "process/form/center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyProcessWorkInCenterForForm(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListProcessWorkInCenterForForm().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteListProcessWorkInCenterForForm got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "统计登录者所有的工作数量.", response = WrapOutOkrWorkStatistic.class)
	@GET
	@Path( "statistic/my" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMyStatistic( @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrWorkStatistic> result = new ActionResult<>();
		try {
			result = new ExcuteGetMyWorkStatistic().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteGetMyWorkStatistic got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
}