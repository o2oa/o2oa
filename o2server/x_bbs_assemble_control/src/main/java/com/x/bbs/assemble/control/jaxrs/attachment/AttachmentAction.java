package com.x.bbs.assemble.control.jaxrs.attachment;

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

@Path("attachment")
@JaxrsDescribe("附件操作")
public class AttachmentAction extends StandardJaxrsAction {
    private static Logger logger = LoggerFactory.getLogger(AttachmentAction.class);

    @JaxrsMethodDescribe(value = "上传附件.", action = StandardJaxrsAction.class)
    @POST
    @Path("upload/subject/{subjectId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void upload(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("主贴ID") @PathParam("subjectId") String subjectId,
            @JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpload().execute(request, effectivePerson, subjectId, site, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "上传附件(带回调).", action = ActionUploadCallback.class)
    @POST
    @Path("upload/subject/{subjectId}/callback/{callback}")
    @Produces(HttpMediaType.TEXT_HTML_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadCallback(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("主贴ID") @PathParam("subjectId") String subjectId,
            @JaxrsParameterDescribe("回调函数名") @PathParam("callback") String callback,
            @JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUploadCallback.Wo<ActionUploadCallback.WoObject>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadCallback().execute(request, effectivePerson, subjectId, callback, site, bytes,
                    disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据附件Id获取单个附件信息.", action = ActionAttachmentGet.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getWithId(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件ID") @PathParam("id") String id) {
        ActionResult<ActionAttachmentGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAttachmentGet().execute(request, effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据Subject获取Attachment列表.", action = ActionAttachmentListBySubjectId.class)
    @GET
    @Path("list/subject/{subjectId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithSubjectId(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("贴子ID") @PathParam("subjectId") String subjectId) {
        ActionResult<List<ActionAttachmentListBySubjectId.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAttachmentListBySubjectId().execute(request, effectivePerson, subjectId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据ID下载指定附件", action = StandardJaxrsAction.class)
    @GET
    @Path("download/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithSubject(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionDownloadWithId.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithId().execute(request, effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据Work下载附件,设定是否使用stream输出", action = ActionDownloadWithIdStream.class)
    @GET
    @Path("download/{id}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithSubjectStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
        ActionResult<ActionDownloadWithIdStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithIdStream().execute(request, effectivePerson, id, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "删除指定subject下指定的附件.", action = ActionAttachmentDelete.class)
    @DELETE
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionAttachmentDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAttachmentDelete().execute(request, effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
