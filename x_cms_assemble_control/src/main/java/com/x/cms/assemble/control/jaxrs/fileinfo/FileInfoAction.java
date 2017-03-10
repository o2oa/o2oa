package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

@Path("fileinfo")
public class FileInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( FileInfoAction.class );
	
	@HttpMethodDescribe(value = "获取全部的文件或者附件列表", response = WrapOutFileInfo.class)
	@GET
	@Path( "list/all" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listAllFileInfo( @Context HttpServletRequest request ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutFileInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListAll().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定文档的全部附件信息列表", response = WrapOutFileInfo.class)
	@GET
	@Path("list/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listFileInfoByDocumentId(@Context HttpServletRequest request, @PathParam("documentId")String documentId ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutFileInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListByDocId().execute( request, effectivePerson, documentId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取fileInfo对象.", response = WrapOutFileInfo.class)
	@GET
	@Path("{id}/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("documentId") String documentId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutFileInfo> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id, documentId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除FileInfo应用信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "将图片附件转为base64编码.", response = WrapOutString.class)
	@GET
	@Path("{id}/binary/base64/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response imageToBase64(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("size") String size ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			result = new ExcuteImageToBase64().execute( request, effectivePerson, id, size );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	
}