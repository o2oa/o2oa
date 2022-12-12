package com.x.meeting.assemble.control.jaxrs.attachment;

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

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("attachment")
@JaxrsDescribe("附件")
public class AttachmentAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(AttachmentAction.class);

    @JaxrsMethodDescribe(value = "获取指定的Attachment信息.", action = ActionGet.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGet().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "将流程平台中的work附带的附件作为会议的附件.", action = ActionCreateFormProcessPlatform.class)
    @POST
    @Path("create/from/processplatform")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createFormProcessPlatform(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<ActionCreateFormProcessPlatform.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCreateFormProcessPlatform().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示Attachment对象,下一页.仅管理员可用", action = ActionListNext.class)
    @GET
    @Path("list/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
        ActionResult<List<ActionListNext.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListNext().execute(effectivePerson, id, count);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示Attachment对象,上一页.仅管理员可用", action = ActionListPrev.class)
    @GET
    @Path("list/{id}/prev/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("数量") @PathParam("count") Integer count) {
        ActionResult<List<ActionListPrev.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPrev().execute(effectivePerson, id, count);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示指定会议的附件.", action = ActionListWithMeeting.class)
    @GET
    @Path("list/meeting/{meetingId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithMeeting(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("会议标识") @PathParam("meetingId") String meetingId) {
        ActionResult<List<ActionListWithMeeting.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithMeeting().execute(effectivePerson, meetingId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "删除Attachment.", action = ActionDelete.class)
    @DELETE
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("id") String id) {
        ActionResult<WrapOutId> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDelete().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "下载附件内容.", action = ActionDownload.class)
    @GET
    @Path("{id}/download/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("是否直接下载附件,不使用浏览器打开") @PathParam("stream") boolean stream) {
        ActionResult<ActionDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownload().execute(effectivePerson, id, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "创建会议附件.", action = ActionUpload.class)
    @POST
    @Path("meeting/{meetingId}/upload/{summary}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void upload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("会议标识") @PathParam("meetingId") String meetingId,
            @JaxrsParameterDescribe("是否是会议纪要") @PathParam("summary") boolean summary,
            @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @JaxrsParameterDescribe("文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpload().execute(effectivePerson, meetingId, summary, fileName, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    /** callback方法与前台ie低版本兼容使用post方法 */
    @JaxrsMethodDescribe(value = "创建会议附件.", action = ActionUploadCallback.class)
    @POST
    @Path("meeting/{meetingId}/upload/{summary}/callback/{callback}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.TEXT_HTML_UTF_8)
    public void uploadCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("会议标识") @PathParam("meetingId") String meetingId,
            @JaxrsParameterDescribe("是否是会议纪要") @PathParam("summary") boolean summary,
            @JaxrsParameterDescribe("回调函数名") @PathParam("callback") String callback,
            @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @JaxrsParameterDescribe("文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUploadCallback.Wo<ActionUploadCallback.WoObject>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadCallback().execute(effectivePerson, meetingId, summary, callback, fileName, bytes,
                    disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "更新会议附件内容", action = ActionUpdate.class)
    @PUT
    @Path("{id}/update")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void update(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("会议标识") @PathParam("id") String id, @FormDataParam(FILE_FIELD) final byte[] bytes,
            @JaxrsParameterDescribe("文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdate().execute(effectivePerson, id, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    /** callback方法与前台ie低版本兼容使用post方法 */
    @JaxrsMethodDescribe(value = "更新会议附件内容", action = ActionUpdateCallback.class)
    @POST
    @Path("{id}/update/callback/{ballback}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.TEXT_HTML_UTF_8)
    public void updateCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("会议标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("回调函数名") @PathParam("callback") String callback,
            @FormDataParam(FILE_FIELD) final byte[] bytes,
            @JaxrsParameterDescribe("文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdateCallback.Wo<ActionUpdateCallback.WoObject>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateCallback().execute(effectivePerson, id, callback, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}