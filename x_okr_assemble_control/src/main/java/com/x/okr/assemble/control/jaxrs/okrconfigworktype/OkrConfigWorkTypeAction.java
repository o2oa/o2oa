package com.x.okr.assemble.control.jaxrs.okrconfigworktype;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception.WrapInConvertException;


@Path( "okrconfigworktype" )
public class OkrConfigWorkTypeAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrConfigWorkTypeAction.class );
	
		
	@HttpMethodDescribe(value = "新建或者更新OkrConfigWorkType对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< WrapOutId > result = new ActionResult<>();
		WrapInOkrConfigWorkType wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrConfigWorkType.class );
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
				logger.warn( "system excute ExcuteSave got an exception. ");
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrConfigWorkType数据对象.", response = WrapOutOkrConfigWorkType.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< WrapOutId > result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteDelete got an exception.id:" + id);
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrConfigWorkType对象.", response = WrapOutOkrConfigWorkType.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< WrapOutOkrConfigWorkType > result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteGet got an exception. id：" + id);
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取OkrConfigWorkType对象列表.", response = WrapOutOkrConfigWorkType.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response all(@Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< List<WrapOutOkrConfigWorkType> > result = new ActionResult<>();
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
	
	@HttpMethodDescribe(value = "获取OkrConfigWorkType对象列表，并且统计每一类所有的中心工作数量.", response = WrapOutOkrConfigWorkType.class )
	@GET
	@Path( "countAll" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countAll(@Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< List<WrapOutOkrConfigWorkType> > result = new ActionResult<>();
		try {
			result = new ExcuteListTypeCount().execute( request, effectivePerson, null );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteListTypeCount got an exception.");
			logger.error( e, effectivePerson, request, null);
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
