package com.x.general.assemble.control.jaxrs.invoice;

import com.google.gson.JsonElement;
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
import com.x.general.assemble.control.jaxrs.invoice.ActionListPaging.Wo;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.BooleanUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("invoice")
@JaxrsDescribe("电子发票服务.")
public class InvoiceAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceAction.class);

	@JaxrsMethodDescribe(value = "获取发票信息.", action = ActionGet.class)
	@GET
	@Path("get/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("发票标识") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "下载发票.", action = ActionDownload.class)
	@GET
	@Path("download/flag/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("*附件标志") @PathParam("flag") String flag,
			@JaxrsParameterDescribe("是否直接下载(true|false)") @QueryParam("stream") Boolean stream) {
		ActionResult<ActionDownload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDownload().execute(effectivePerson, flag, BooleanUtils.isTrue(stream));
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "上传电子发票文件，pdf格式.", action = ActionUpload.class)
	@POST
	@Path("upload")
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM })
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void upload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("上传文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUpload.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpload().execute(effectivePerson, bytes, disposition);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据url上传电子发票，pdf格式.", action = ActionUploadWithUrl.class)
	@POST
	@Path("upload/with/url")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void uploadWithUrl(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionUploadWithUrl.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadWithUrl().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "手工上传电子发票.", action = ActionUploadTemp.class)
	@POST
	@Path("upload/for/create")
	@Consumes({ MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM })
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void uploadForCreate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@JaxrsParameterDescribe("上传文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult<ActionUploadTemp.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadTemp().execute(effectivePerson, bytes, disposition);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "手工填写电子发票.", action = ActionCreate.class)
	@POST
	@Path("create")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionCreate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreate().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新指定的电子发票.", action = ActionUpdate.class)
	@POST
	@Path("update/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdate().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新发票的报销状态为已报销.", action = ActionUpdateApplyStatus.class)
	@POST
	@Path("update/apply/status/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateApplyStatus(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("标识") @PathParam("id") String id) {
		ActionResult<ActionUpdateApplyStatus.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateApplyStatus().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "删除指定发票.", action = ActionDelete.class)
	@GET
	@Path("delete/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("电子发票标识") @PathParam("id") String id) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "分页查询.", action = ActionListPaging.class)
	@POST
	@Path("list/paging/{page}/size/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size, JsonElement jsonElement) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListPaging().execute(effectivePerson, page, size, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}
