package com.x.cms.assemble.control.jaxrs.documentviewrecord;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.WrapOutAppCategoryAdmin;

@Path("viewrecord")
public class DocumentViewRecordAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( DocumentViewRecordAction.class );
	
	@HttpMethodDescribe(value = "根据文档ID获取该文档的访问用户记录信息，按时间倒序，前50条", response = WrapOutAppCategoryAdmin.class)
	@GET
	@Path("document/{docId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByDocument( @Context HttpServletRequest request, @PathParam("docId") String docId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutDocumentViewRecord>> result = null;
		try {
			result = new ExcuteListByDocument().execute( request, effectivePerson, docId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ServiceLogicException( e,"系统查询文档访问信息时发生未知异常。" );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "根据人员姓名，获取该用户访问的文档记录，按时间倒序，前50条", response = WrapOutAppCategoryAdmin.class)
	@GET
	@Path( "person/{name}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByPerson( @Context HttpServletRequest request, @PathParam("name") String name ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutDocumentViewRecord>> result = null;
		try {
			result = new ExcuteListByPerson().execute( request, effectivePerson, name );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ServiceLogicException( e,"系统查询文档访问信息时发生未知异常。" );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
}