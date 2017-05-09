package com.x.okr.assemble.control.jaxrs.okrauthorize;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
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
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.OkrAttachmentFileInfoAction;
import com.x.okr.assemble.control.jaxrs.okrauthorize.exception.WrapInConvertException;

@Path( "okrauthorize" )
public class OkrWorkAuthorizeAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrAttachmentFileInfoAction.class );
	
	@HttpMethodDescribe(value = "工作处理授权服务.", response = WrapOutId.class, request = JsonElement.class)
	@PUT
	@Path( "work" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response workAuthorize( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInFilterWorkAuthorize wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterWorkAuthorize.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteWorkAuthorize().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "系统进行工作授权执行过程发生异常。" );
				logger.error( e, effectivePerson, request, null);
				
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 工作授权收回服务
	 * 
	 * PUT PARAMETER : workId
	 * 
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "工作授权收回服务.", response = WrapOutId.class, request = JsonElement.class)
	@PUT
	@Path( "takeback" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response takeback( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInFilterWorkAuthorize wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterWorkAuthorize.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			try {
				result = new ExcuteWorkTackback().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.warn( "系统对已经授权的工作进行收回操作过程发生异常。" );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}