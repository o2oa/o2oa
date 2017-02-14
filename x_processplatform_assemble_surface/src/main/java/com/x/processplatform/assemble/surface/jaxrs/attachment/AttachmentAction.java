package com.x.processplatform.assemble.surface.jaxrs.attachment;

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
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.processplatform.assemble.surface.wrapout.content.WrapOutAttachment;

@Path("attachment")
public class AttachmentAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "根据Work和附件Id获取单个附件信息.", response = WrapOutAttachment.class)
	@GET
	@Path("{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWork(@Context HttpServletRequest request, @PathParam("workId") String workId,
			@PathParam("id") String id) {
		ActionResult<WrapOutAttachment> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWork().execute(effectivePerson, id, workId);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据WorkCompleted和附件Id获取单个附件信息", response = WrapOutAttachment.class)
	@GET
	@Path("{id}/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithWorkCompleted(@Context HttpServletRequest request,
			@PathParam("workCompletedId") String workCompletedId, @PathParam("id") String id) {
		ActionResult<WrapOutAttachment> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionGetWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据Work获取Attachment列表.", response = WrapOutAttachment.class)
	@GET
	@Path("list/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithWork(@Context HttpServletRequest request, @PathParam("workId") String workId) {
		ActionResult<List<WrapOutAttachment>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListWithWork().execute(effectivePerson, workId);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据WorkCompleted获取Attachment列表.", response = WrapOutAttachment.class)
	@GET
	@Path("list/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithWorkCompleted(@Context HttpServletRequest request,
			@PathParam("workCompletedId") String workCompletedId) {
		ActionResult<List<WrapOutAttachment>> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionListWithWorkCompleted().execute(effectivePerson, workCompletedId);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除指定work下的附件,同时删除work中的attachmentList中的记录", response = WrapOutId.class)
	@DELETE
	@Path("{id}/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("workId") String workId) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			result = new ActionDelete().execute(effectivePerson, id, workId);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}