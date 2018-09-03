package com.x.cms.assemble.search.jaxrs.search;

import java.util.List;

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
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.search.bean.WrapDocument;

@Path("search")
@JaxrsDescribe("CMS信息检索服务")
public class CMS_SearcjAction extends StandardJaxrsAction{
	
	private static  Logger logger = LoggerFactory.getLogger( CMS_SearcjAction.class );
	
	@JaxrsMethodDescribe(value = "检索CMS信息内容", action = ActionSearchDocuments.class)
	@Path("cms")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void search(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<List<WrapDocument>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSearchDocuments().execute( request, effectivePerson, jsonElement );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
}