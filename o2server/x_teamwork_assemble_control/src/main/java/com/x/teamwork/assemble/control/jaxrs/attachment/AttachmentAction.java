package com.x.teamwork.assemble.control.jaxrs.attachment;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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

@Path( "attachment" )
@JaxrsDescribe("工作任务附件信息管理服务")
public class AttachmentAction extends StandardJaxrsAction{
	
	private static  Logger logger = LoggerFactory.getLogger( AttachmentAction.class );

	@JaxrsMethodDescribe(value = "根据项目ID获取工作附件信息列表", action = ActionListWithProject.class)
	@GET
	@Path( "list/project/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByProjectId( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("项目ID") @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListWithProject.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWithProject().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn( "系统根据项目ID获取项目所有附件信息过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据工作ID获取工作附件信息列表", action = ActionListWithTask.class)
	@GET
	@Path( "list/task/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByTaskId( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("工作ID") @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<ActionListWithTask.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListWithTask().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn( "系统根据工作ID获取工作所有附件信息过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID删除附件信息对象", action = ActionDelete.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("附件ID") @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		try {
			result = new ActionDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn( "系统根据ID删除附件信息对象过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据ID获取附件信息对象", action = ActionGet.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("附件信息ID") @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.warn( "系统根据ID获取附件信息对象过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID下载指定附件", action = ActionDownload.class)
	@GET
	@Path("download/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downLoad(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("附件信息ID") @PathParam("id") String id) {
		ActionResult<ActionDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownload().execute(request, effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "下载指定附件,设定是否使用stream输出", action = ActionDownloadStream.class)
	@GET
	@Path("download/{id}/stream/{stream}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadStream(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("附件信息ID") @PathParam("id") String id, 
			@JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
		ActionResult<ActionDownloadStream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadStream().execute(request, effectivePerson, id, stream);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "上传项目信息附件.", action = ActionProjectAttachmentUpload.class)
	@POST
	@Path("upload/project/{id}/site/{site}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void projectAttachmentUpload(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("项目信息ID") @PathParam("id") String id, 
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site, 
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionProjectAttachmentUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionProjectAttachmentUpload().execute(request, effectivePerson, id, site, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "为工作信息上传附件.", action = ActionTaskAttachmentUpload.class)
	@POST
	@Path("upload/task/{id}/site/{site}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void taskAttachmentUpload(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@JaxrsParameterDescribe("工作任务ID") @PathParam("id") String id, 
			@JaxrsParameterDescribe("位置") @FormDataParam("site") String site, 
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionTaskAttachmentUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionTaskAttachmentUpload().execute(request, effectivePerson, id, site, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}
