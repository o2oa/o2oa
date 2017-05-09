package com.x.cms.assemble.control.jaxrs.document;

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
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentInfoProcessException;

@Path("document/cipher")
public class DocumentCipherAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( DocumentCipherAction.class );
	
	@HttpMethodDescribe(value = "直接发布文档信息.", response = WrapOutId.class)
	@PUT
	@Path("publish/content")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response publishContent(@Context HttpServletRequest request, JsonElement jsonElement ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInDocument wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to convert json element to wrap in......" );
		
		System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>jsonElement:" + jsonElement.toString() );
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInDocument.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new DocumentInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>system try to publish content......" );
			try {
				result = new ExcutePublishContentByWorkFlow().execute( request, wrapIn, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}