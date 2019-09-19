package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("anonymous/fileinfo")
@JaxrsDescribe("可匿名访问的附件信息管理服务")
public class FileInfoAnonymousAction extends StandardJaxrsAction{
	
	private static  Logger logger = LoggerFactory.getLogger( FileInfoAnonymousAction.class );
	
	@JaxrsMethodDescribe(value = "获取指定文档的全部附件信息列表.", action = ActionListByDocId.class)
	@GET
	@Path("list/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listFileInfoByDocumentId( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("documentId")String documentId ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListByDocId.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListByDocId().execute( request, effectivePerson, documentId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID获取fileInfo对象.", action = ActionGet.class)
	@GET
	@Path("{id}/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("附件信息ID") @PathParam("id") String id, 
			@JaxrsParameterDescribe("信息文档ID") @PathParam("documentId") String documentId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute( request, effectivePerson, id, documentId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID下载指定附件", action = ActionFileDownload.class)
	@GET
	@Path("download/document/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void attachmentDownLoad(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
		ActionResult<ActionFileDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFileDownload().execute(request, effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据附件ID下载附件,设定是否使用stream输出", action = ActionFileDownloadStream.class)
	@GET
	@Path("download/document/{id}/stream")
	@Consumes(MediaType.APPLICATION_JSON)
	public void attachmentDownloadStream(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("附件标识") @PathParam("id") String id ) {
		ActionResult<ActionFileDownloadStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFileDownloadStream().execute(request, effectivePerson, id );
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}