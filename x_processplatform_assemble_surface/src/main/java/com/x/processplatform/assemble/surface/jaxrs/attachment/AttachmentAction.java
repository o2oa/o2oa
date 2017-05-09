package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.BooleanWo;
import com.x.base.core.project.jaxrs.FileWo;
import com.x.base.core.project.jaxrs.IdWo;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

@Path("attachment")
public class AttachmentAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AttachmentAction.class);

	@HttpMethodDescribe(value = "测试文件是否存在.", response = WrapOutId.class)
	@GET
	@Path("{id}/available")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void available(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("id") String id) {
		ActionResult<BooleanWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionAvailable().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "根据Work和附件Id获取单个附件信息.", response = ActionGetWithWork.Wo.class)
	@GET
	@Path("{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("workId") String workId, @PathParam("id") String id) {
		ActionResult<ActionGetWithWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWork().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "根据WorkCompleted和附件Id获取单个附件信息", response = ActionGetWithWorkCompleted.Wo.class)
	@GET
	@Path("{id}/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("workCompletedId") String workCompletedId, @PathParam("id") String id) {
		ActionResult<ActionGetWithWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "根据Work获取Attachment列表.", response = ActionListWithWork.Wo.class)
	@GET
	@Path("list/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("workId") String workId) {
		ActionResult<List<ActionListWithWork.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWork().execute(effectivePerson, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "根据WorkCompleted获取Attachment列表.", response = ActionListWithWorkCompleted.Wo.class)
	@GET
	@Path("list/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("workCompletedId") String workCompletedId) {
		ActionResult<List<ActionListWithWorkCompleted.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithWorkCompleted().execute(effectivePerson, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "删除指定work下的附件.", response = WrapOutId.class)
	@DELETE
	@Path("{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("workId") String workId) {
		ActionResult<IdWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, id, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "根据Work下载附件", response = FileWo.class)
	@GET
	@Path("download/{id}/work/{workId}/stream/{stream}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("workId") String workId, @PathParam("stream") Boolean stream) {
		ActionResult<FileWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWork().execute(effectivePerson, id, workId, stream);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "根据WorkCompleted下载附件", response = FileWo.class)
	@GET
	@Path("download/{id}/workcompleted/{workCompletedId}/stream/{stream}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void downloadWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("workCompletedId") String workCompletedId, @PathParam("stream") Boolean stream) {
		ActionResult<FileWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId, stream);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "上传附件.", response = IdWo.class)
	@POST
	@Path("upload/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void upload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("workId") String workId, @FormDataParam("site") String site,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<IdWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpload().execute(effectivePerson, workId, site, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@HttpMethodDescribe(value = "更新附件.", response = IdWo.class)
	@PUT
	@Path("update/{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void update(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("workId") String workId,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<IdWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdate().execute(effectivePerson, id, workId, bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}