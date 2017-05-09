package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;
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

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

@Path( "okrattachmentfileinfo" )
public class OkrAttachmentFileInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrAttachmentFileInfoAction.class );

	@HttpMethodDescribe(value = "根据中心工作ID获取OkrAttachmentFileInfo列表.", response = WrapOutOkrAttachmentFileInfo.class)
	@GET
	@Path( "list/center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByCenterId(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrAttachmentFileInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListWithCenterId().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "系统根据中心工作ID获取中心工作所有附件信息过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据工作ID获取OkrAttachmentFileInfo列表.", response = WrapOutOkrAttachmentFileInfo.class)
	@GET
	@Path( "list/work/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByWorkId(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrAttachmentFileInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListWithWorkId().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "系统根据工作ID获取工作所有附件信息过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据工作ID获取OkrAttachmentFileInfo列表.", response = WrapOutOkrAttachmentFileInfo.class)
	@GET
	@Path( "list/report/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByWorkReportId(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrAttachmentFileInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListWithReportId().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "系统根据工作ID获取工作汇报所有附件信息过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除中心工作的OkrAttachmentFileInfo数据对象.", response = WrapOutOkrAttachmentFileInfo.class)
	@DELETE
	@Path( "center/attachment/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteCenterAttachment(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDeleteCenterAttachment().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "系统附件信息ID删除中心工作附件信息过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除OkrAttachmentFileInfo数据对象.", response = WrapOutOkrAttachmentFileInfo.class)
	@DELETE
	@Path( "work/attachment/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWorkAttachment(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDeleteWorkAttachment().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "系统附件信息ID删除工作附件信息过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除OkrAttachmentFileInfo数据对象.", response = WrapOutOkrAttachmentFileInfo.class)
	@DELETE
	@Path( "report/attachment/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteReportAttachment(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDeleteReportAttachment().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "系统附件信息ID删除工作汇报附件信息过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrAttachmentFileInfo对象.", response = WrapOutOkrAttachmentFileInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrAttachmentFileInfo> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "系统附件信息ID查询附件信息过程发生异常。" );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
}
