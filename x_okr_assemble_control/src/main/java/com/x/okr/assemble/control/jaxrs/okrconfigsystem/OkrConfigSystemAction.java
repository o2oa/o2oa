package com.x.okr.assemble.control.jaxrs.okrconfigsystem;
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
import com.x.okr.assemble.control.jaxrs.okrconfigsystem.exception.WrapInConvertException;


@Path( "okrconfigsystem" )
public class OkrConfigSystemAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrConfigSystemAction.class );
	
	@HttpMethodDescribe(value = "新建或者更新OkrConfigSystem对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< WrapOutId > result = new ActionResult<>();
		WrapInOkrConfigSystem wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrConfigSystem.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteSave got an exception. " );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrConfigSystem数据对象.", response = WrapOutOkrConfigSystem.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< WrapOutId > result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteDelete got an exception.id:" + id );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrConfigSystem对象.", response = WrapOutOkrConfigSystem.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< WrapOutOkrConfigSystem > result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteGet got an exception. id：" + id );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取OkrConfigSystem列表.", response = WrapOutOkrConfigSystem.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response all(@Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< List<WrapOutOkrConfigSystem> > result = new ActionResult<>();
		try {
			result = new ExcuteListAll().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteListAll got an exception.");
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@Path( "code" )
	@HttpMethodDescribe( value = "根据CODE获取OkrConfigSystem对象.", request = JsonElement.class, response = WrapOutOkrConfigSystem.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response getByCode( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< WrapOutOkrConfigSystem > result = new ActionResult<>();
		WrapInOkrConfigSystem wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrConfigSystem.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteGetByCode().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "system excute ExcuteGetByCode got an exception. code：" + wrapIn.getConfigCode() );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取OkrPersonPermission对象.", response = WrapOutBoolean.class)
	@GET
	@Path( "allowed/deploy" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response allowedDeployWork(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ExcuteAllowedDeployWork().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteAllowedDeployWork got an exception. "  );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
