package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("document/cipher")
public class DocumentCipherAction extends StandardJaxrsAction{
	
	private static  Logger logger = LoggerFactory.getLogger( DocumentCipherAction.class );
	
	@JaxrsMethodDescribe(value = "直接发布文档信息.", action = ActionPersistPublishByWorkFlow.class)
	@PUT
	@Path("publish/content")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void publishContent( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionPersistPublishByWorkFlow.Wo> result = new ActionResult<>();
		Boolean check = true;

		if( check ){
			try {
				result = new ActionPersistPublishByWorkFlow().execute( request, jsonElement, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}