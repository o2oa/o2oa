package com.x.cms.assemble.control.jaxrs.documentpermission;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
import com.x.cms.assemble.control.jaxrs.documentpermission.exception.ServiceLogicException;
import com.x.cms.assemble.control.jaxrs.documentpermission.exception.WrapInConvertException;

@Path("docpermission")
public class DocumentPermissionAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( DocumentPermissionAction.class );
	
	@HttpMethodDescribe(value = "创建Document文档信息对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response refreshDocumentPermission( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInDocumentPermission wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInDocumentPermission.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteRefreshDocumentPermission().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ServiceLogicException( e, "系统在更新文档的访问和管理权限过程中发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}