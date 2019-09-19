package com.x.cms.assemble.control.jaxrs.permission;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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

@Path("docpermission")
@JaxrsDescribe("信息文档发布权限管理")
public class PermissionForDocumentAction extends StandardJaxrsAction{
	
	private static  Logger logger = LoggerFactory.getLogger( PermissionForDocumentAction.class );
	
	@JaxrsMethodDescribe(value = "刷新信息文档发布权限.", action = ActionRefreshDocumentPermission.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void refreshDocumentPermission( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionRefreshDocumentPermission.Wo> result = new ActionResult<>();
		Boolean check = true;

		if(check){
			try {
				result = new ActionRefreshDocumentPermission().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionServiceLogic( e, "系统在更新文档的访问和管理权限过程中发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}