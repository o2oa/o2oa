package com.x.processplatform.assemble.surface.jaxrs.attachment;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AttachmentAction", description = "附件接口.")
@Path("attachment")
@JaxrsDescribe("附件接口.")
public class AttachmentAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentAction.class);
    private static final String OPERATIONID_PREFIX = "AttachmentAction::";

    @Operation(summary = "判断文件是否存在.", operationId = OPERATIONID_PREFIX + "available", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionAvailable.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "判断文件是否存在.", action = ActionAvailable.class)
    @GET
    @Path("{id}/available")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void available(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionAvailable.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionAvailable().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据工作标识和附件标识获取附件信息.", operationId = OPERATIONID_PREFIX + "getWithWork", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识和附件标识获取附件信息.", action = ActionGetWithWork.class)
    @GET
    @Path("{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionGetWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetWithWork().execute(effectivePerson, id, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据工作标识和附件标识获取附件信息.", operationId = OPERATIONID_PREFIX + "getWithWorkCompleted", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据WorkCompleted和附件Id获取单个附件信息", action = ActionGetWithWorkCompleted.class)
    @GET
    @Path("{id}/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionGetWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据工作标识或者已完成工作标识和附件标识获取附件信息.", operationId = OPERATIONID_PREFIX
            + "getWithWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionGetWithWorkOrWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识或者已完成工作标识和附件标识获取附件信息.", action = ActionGetWithWorkOrWorkCompleted.class)
    @GET
    @Path("{id}/workorworkcompleted/{workOrWorkCompleted}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionGetWithWorkOrWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetWithWorkOrWorkCompleted().execute(effectivePerson, id, workOrWorkCompleted);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据工作标识列示附件.", operationId = OPERATIONID_PREFIX + "listWithWork", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWork.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "根据工作标识列示附件.", action = ActionListWithWork.class)
    @GET
    @Path("list/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
        ActionResult<List<ActionListWithWork.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithWork().execute(effectivePerson, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据已完成工作标识列示附件.", operationId = OPERATIONID_PREFIX + "listWithWorkCompleted", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWorkCompleted.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "根据已完成工作标识列示附件.", action = ActionListWithWorkCompleted.class)
    @GET
    @Path("list/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
        ActionResult<List<ActionListWithWorkCompleted.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithWorkCompleted().execute(effectivePerson, workCompletedId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据工作或已完成工作标识获取附件列表.", operationId = OPERATIONID_PREFIX
            + "listWithWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithWorkOrWorkCompleted.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "根据工作或已完成工作标识获取附件列表.", action = ActionListWithWorkOrWorkCompleted.class)
    @GET
    @Path("list/workorworkcompleted/{workOrWorkCompleted}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted) {
        ActionResult<List<ActionListWithWorkOrWorkCompleted.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据任务标识获取附件列表.", operationId = OPERATIONID_PREFIX + "listWithJob", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListWithJob.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "根据任务标识获取附件列表.", action = ActionListWithJob.class)
    @GET
    @Path("list/job/{job}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithJob(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作的job") @PathParam("job") String job) {
        ActionResult<List<ActionListWithJob.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithJob().execute(effectivePerson, job);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "删除指定标识的附件.", operationId = OPERATIONID_PREFIX + "delete", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDelete.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "删除指定标识的附件.", action = ActionDelete.class)
    @DELETE
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
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

    @Operation(summary = "删除指定标识的附件(mock delete to get).", operationId = OPERATIONID_PREFIX
            + "deleteMockDeleteToGet", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDelete.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "删除指定标识的附件(mock delete to get).", action = ActionDelete.class)
    @GET
    @Path("{id}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteMockDeleteToGet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
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

    @Deprecated
    @Operation(summary = "根据工作标识和附件标识删除附件..", operationId = OPERATIONID_PREFIX + "deleteWithWork", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDeleteWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识和附件标识删除附件.", action = ActionDeleteWithWork.class)
    @DELETE
    @Path("{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
        ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteWithWork().execute(effectivePerson, id, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据工作标识和附件标识删除附件(mock delete to get).", operationId = OPERATIONID_PREFIX
            + "deleteWithWorkMockDeleteToGet", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDeleteWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识和附件标识删除附件(mock delete to get).", action = ActionDeleteWithWork.class)
    @GET
    @Path("{id}/work/{workId}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteWithWorkMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
        ActionResult<ActionDeleteWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteWithWork().execute(effectivePerson, id, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据已完成工作标识和附件标识删除附件.", operationId = OPERATIONID_PREFIX
            + "deleteWithWorkMockDeleteToGet", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDeleteWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据已完成工作标识和附件标识删除附件. ", action = ActionDeleteWithWorkCompleted.class)
    @DELETE
    @Path("{id}/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
        ActionResult<ActionDeleteWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据已完成工作标识和附件标识删除附件(mock delete to get).", operationId = OPERATIONID_PREFIX
            + "deleteWithWorkCompletedMockDeleteToGet", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDeleteWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据已完成工作标识和附件标识删除附件(mock delete to get).", action = ActionDeleteWithWorkCompleted.class)
    @GET
    @Path("{id}/workcompleted/{workCompletedId}/mockdeletetoget")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteWithWorkCompletedMockDeleteToGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
        ActionResult<ActionDeleteWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteWithWorkCompleted().execute(effectivePerson, id, workCompletedId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "下载指定标识的附件.", operationId = OPERATIONID_PREFIX + "download", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "下载指定标识的附件.", action = ActionDownload.class)
    @GET
    @Path("download/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void download(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownload().execute(effectivePerson, id, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "下载指定标识的附件,设定使用流输出.", operationId = OPERATIONID_PREFIX + "downloadStream", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownloadStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "下载指定标识的附件,设定使用流输出.", action = ActionDownloadStream.class)
    @GET
    @Path("download/{id}/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadStream().execute(effectivePerson, id, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据工作标识和附件标识下载附件.", operationId = OPERATIONID_PREFIX + "downloadWithWork", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownloadWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识和附件标识下载附件.", action = ActionDownloadWithWork.class)
    @GET
    @Path("download/{id}/work/{workId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWork().execute(effectivePerson, id, workId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据工作标识和附件标识以指定的名称下载附件,设定使用流输出.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkStream", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识和附件标识以指定的名称下载附件,设定使用流输出.", action = ActionDownloadWithWorkStream.class)
    @GET
    @Path("download/{id}/work/{workId}/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据工作标识和附件标识以指定的名称下载附件.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkWithExtension", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识和附件标识以指定的名称下载附件.", action = ActionDownloadWithWork.class)
    @GET
    @Path("download/{id}/work/{workId}/{fileName}.{extension}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkWithExtension(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWork().execute(effectivePerson, id, workId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据工作标识和附件标识以指定的名称下载附件,设定使用流输出.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkStreamWithExtension", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识和附件标识以指定的名称下载附件,设定使用流输出.", action = ActionDownloadWithWorkStream.class)
    @GET
    @Path("download/{id}/work/{workId}/stream/{fileName}.{extension}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkStream().execute(effectivePerson, id, workId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据已完成工作标识和附件标识下载附件.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据已完成工作标识和附件标识下载附件.", action = ActionDownloadWithWorkCompleted.class)
    @GET
    @Path("download/{id}/workcompleted/{workCompletedId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据已完成工作标识和附件标识以指定的名称下载附件.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkCompletedWithExtension", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据已完成工作标识和附件标识以指定的名称下载附件.", action = ActionDownloadWithWorkCompleted.class)
    @GET
    @Path("download/{id}/workcompleted/{workCompletedId}/{fileName}.{extension}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkCompletedWithExtension(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkCompleted().execute(effectivePerson, id, workCompletedId, fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据已完成工作标识和附件标识下载附件,设定使用流输出.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkCompletedStream", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkCompletedStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据已完成工作标识和附件标识下载附件,设定使用流输出.", action = ActionDownloadWithWorkCompletedStream.class)
    @GET
    @Path("download/{id}/workcompleted/{workCompletedId}/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkCompletedStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId,
                    fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "根据已完成工作标识和附件标识以指定的名称下载附件,设定使用流输出.", operationId = OPERATIONID_PREFIX
            + "downloadWithWorkCompletedStreamWithExtension", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDownloadWithWorkCompletedStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据已完成工作标识和附件标识以指定的名称下载附件,设定使用流输出.", action = ActionDownloadWithWorkCompletedStream.class)
    @GET
    @Path("download/{id}/workcompleted/{workCompletedId}/stream/{fileName}.{extension}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWithWorkCompletedStreamWithExtension(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName) {
        ActionResult<ActionDownloadWithWorkCompletedStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWithWorkCompletedStream().execute(effectivePerson, id, workCompletedId,
                    fileName);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据工作标识上传附件.", operationId = OPERATIONID_PREFIX + "upload", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUploadWithWork.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "上传附件.", action = ActionUploadWithWork.class)
    @POST
    @Path("upload/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void upload(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUploadWithWork.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUploadWithWork().execute(effectivePerson, workId, site, fileName, bytes, disposition,
                    extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据已完成工作标识上传附件.", operationId = OPERATIONID_PREFIX + "uploadWithWorkCompleted", responses = {
            @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUploadWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "上传附件.", action = ActionUploadWithWorkCompleted.class)
    @POST
    @Path("upload/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void uploadWithWorkCompleted(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
            @JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUploadWithWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUploadWithWorkCompleted().execute(effectivePerson, workCompletedId, site, fileName,
                    bytes, disposition, extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));

    }

    @Deprecated
    @Operation(summary = "根据工作标识上传附件,回调jsonp.", operationId = OPERATIONID_PREFIX
            + "uploadWithWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUploadWithWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识上传附件,回调jsonp.", action = ActionUploadCallback.class)
    @POST
    @Path("upload/work/{workId}/callback/{callback}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.TEXT_HTML_UTF_8)
    public void uploadCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("回调函数名") @PathParam("callback") String callback,
            @JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUploadCallback.Wo<ActionUploadCallback.WoObject>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadCallback().execute(effectivePerson, workId, callback, site, fileName, bytes,
                    disposition);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据工作标识更新附件.", operationId = OPERATIONID_PREFIX + "update", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdate.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识更新附件内容.", action = ActionUpdate.class)
    @PUT
    @Path("update/{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void update(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
            @FormDataParam(FILE_FIELD) byte[] bytes,
            @JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据工作标识更新附件(mock put to post).", operationId = OPERATIONID_PREFIX
            + "updateMockPutToPost", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdate.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识更新附件内容(mock put to post).", action = ActionUpdate.class)
    @POST
    @Path("update/{id}/work/{workId}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void updateMockPutToPost(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
            @FormDataParam(FILE_FIELD) byte[] bytes,
            @JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据附件标识和工作标识更新附件信息.", operationId = OPERATIONID_PREFIX + "updateContent", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateContent.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据附件标识和工作标识更新附件信息.", action = ActionUpdateContent.class)
    @PUT
    @Path("update/content/{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateContent(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionUpdateContent.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateContent().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据附件标识和工作标识更新附件信息(mock put to post).", operationId = OPERATIONID_PREFIX
            + "updateContentMockPutToPost", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUpdateContent.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据附件标识和工作标识更新附件信息(mock put to post).", action = ActionUpdateContent.class)
    @POST
    @Path("update/content/{id}/work/{workId}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateContentMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionUpdateContent.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateContent().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据附件标识和工作标识更新附件,回调jsonp.", operationId = OPERATIONID_PREFIX + "updateCallback", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUpdateContent.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据附件标识和工作标识更新附件,回调jsonp.", action = ActionUpdateCallback.class)
    @POST
    @Path("update/{id}/work/{workId}/callback/{callback}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.TEXT_HTML_UTF_8)
    public void updateCallback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("回调函数名") @PathParam("callback") String callback,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @FormDataParam(FILE_FIELD) final byte[] bytes,
            @JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdateCallback.Wo<ActionUpdateCallback.WoObject>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateCallback().execute(effectivePerson, id, workId, callback, fileName, bytes,
                    disposition);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据附件标识和工作标识更新附件,与update方法同,为了兼容ntko对于附件上传只能设置post方法.", operationId = OPERATIONID_PREFIX
            + "updateCallback", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUpdateContent.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据附件标识和工作标识更新附件,与update方法同,为了兼容ntko对于附件上传只能设置post方法.", action = ActionUpdate.class)
    @POST
    @Path("update/{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void updatePost(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
            @FormDataParam(FILE_FIELD) byte[] bytes,
            @JaxrsParameterDescribe("附件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            if (StringUtils.isEmpty(extraParam)) {
                extraParam = this.request2Json(request);
            }
            if (bytes == null) {
                Map<String, List<FormDataBodyPart>> map = form.getFields();
                for (String key : map.keySet()) {
                    FormDataBodyPart part = map.get(key).get(0);
                    if ("application".equals(part.getMediaType().getType())) {
                        bytes = part.getValueAs(byte[].class);
                        break;
                    }
                }
            }
            result = new ActionUpdate().execute(effectivePerson, id, workId, fileName, bytes, disposition, extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "由工作标识指定的工作拷贝附件.", operationId = OPERATIONID_PREFIX + "copyToWork", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionCopyToWork.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "由工作标识指定的工作拷贝附件.", action = ActionCopyToWork.class)
    @POST
    @Path("copy/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void copyToWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<List<ActionCopyToWork.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCopyToWork().execute(effectivePerson, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "由已完成工作标识指定的工作拷贝附件.", operationId = OPERATIONID_PREFIX + "copyToWorkCompleted", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionCopyToWorkCompleted.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "由已完成工作标识指定的工作拷贝附件.", action = ActionCopyToWorkCompleted.class)
    @POST
    @Path("copy/workcompleted/{workCompletedId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void copyToWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
            JsonElement jsonElement) {
        ActionResult<List<ActionCopyToWorkCompleted.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCopyToWorkCompleted().execute(effectivePerson, workCompletedId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "由工作标识指定的工作软拷贝附件(不拷贝真实存储附件，共用附件，此接口拷贝的附件删除时只删除记录不删附件).", operationId = OPERATIONID_PREFIX
            + "copyToWorkSoft", responses = { @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionCopyToWorkSoft.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "由工作标识指定的工作软拷贝附件(不拷贝真实存储附件，共用附件，此接口拷贝的附件删除时只删除记录不删附件).", action = ActionCopyToWorkSoft.class)
    @POST
    @Path("copy/work/{workId}/soft")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void copyToWorkSoft(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<List<ActionCopyToWorkSoft.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCopyToWorkSoft().execute(effectivePerson, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "由已完成工作标识指定的工作软拷贝附件(不拷贝真实存储附件，共用附件，此接口拷贝的附件删除时只删除记录不删附件).", operationId = OPERATIONID_PREFIX
            + "copyToWorkCompletedSoft", responses = { @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionCopyToWorkCompletedSoft.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "由已完成工作标识指定的工作软拷贝附件(不拷贝真实存储附件，共用附件，此接口拷贝的附件删除时只删除记录不删附件).", action = ActionCopyToWorkCompletedSoft.class)
    @POST
    @Path("copy/workcompleted/{workCompletedId}/soft")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void copyToWorkCompletedSoft(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId,
            JsonElement jsonElement) {
        ActionResult<List<ActionCopyToWorkCompletedSoft.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCopyToWorkCompletedSoft().execute(effectivePerson, workCompletedId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "更新指定附件标识的附件位置信息.", operationId = OPERATIONID_PREFIX + "changeSite", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionChangeSite.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "更新指定附件标识的附件位置信息.", action = ActionChangeSite.class)
    @GET
    @Path("{id}/work/{workId}/change/site/{site}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void changeSite(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("位置") @PathParam("site") String site) {
        ActionResult<ActionChangeSite.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionChangeSite().execute(effectivePerson, id, workId, site);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "更新指定附件标识的附件信息.", operationId = OPERATIONID_PREFIX + "edit", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionEdit.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "更新指定附件标识的附件信息.", action = ActionEdit.class)
    @PUT
    @Path("edit/{id}/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void edit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionEdit.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEdit().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Deprecated
    @Operation(summary = "更新指定附件标识的附件信息(mock put to post).", operationId = OPERATIONID_PREFIX + "edit", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionEdit.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "M更新指定附件标识的附件信息(mock put to post).", action = ActionEdit.class)
    @POST
    @Path("edit/{id}/work/{workId}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionEdit.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEdit().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "更新指定附件标识的附件排序号.", operationId = OPERATIONID_PREFIX + "changeOrderNumber", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionChangeOrderNumber.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "更新指定附件标识的附件排序号.", action = ActionChangeOrderNumber.class)
    @GET
    @Path("{id}/work/{workId}/change/ordernumber/{orderNumber}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void changeOrderNumber(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("排序号") @PathParam("orderNumber") Integer orderNumber) {
        ActionResult<ActionChangeOrderNumber.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionChangeOrderNumber().execute(effectivePerson, id, workId, orderNumber);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "更新指定附件标识的附件的文本识别内容.", operationId = OPERATIONID_PREFIX + "exitText", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionEditText.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "更新指定附件标识的附件的文本识别内容.", action = ActionEditText.class)
    @PUT
    @Path("edit/{id}/work/{workId}/text")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void exitText(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionEditText.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEditText().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "更新指定附件标识的附件的文本识别内容(mock put to post).", operationId = OPERATIONID_PREFIX
            + "exitTextMockPutToPost", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionEditText.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "更新指定附件标识的附件的文本识别内容(mock put to post).", action = ActionEditText.class)
    @POST
    @Path("edit/{id}/work/{workId}/text/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void exitTextMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionEditText.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionEditText().execute(effectivePerson, id, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "获取指定附件标识的附件的文本识别内容.", operationId = OPERATIONID_PREFIX + "getText", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionGetText.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "获取指定附件标识的附件的文本识别内容.", action = ActionGetText.class)
    @GET
    @Path("{id}/work/{workId}/text")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getText(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
        ActionResult<ActionGetText.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetText().execute(effectivePerson, id, workId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "获取指定工作标识的工作,将html版式公文转换成成Word文件并添加在附件中.", operationId = OPERATIONID_PREFIX
            + "docToWord", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDocToWord.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "获取指定工作标识的工作,将html版式公文转换成成Word文件并添加在附件中.", action = ActionDocToWord.class)
    @POST
    @Path("doc/to/word/work/{workId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void docToWord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId, JsonElement jsonElement) {
        ActionResult<ActionDocToWord.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDocToWord().execute(effectivePerson, workId, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "获取指定工作标识或已完成工作标识,将html版式公文转换成成Word文件并添加在附件中.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionDocToWordWorkOrWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "获取指定工作标识或已完成工作标识,将html版式公文转换成成Word文件并添加在附件中.", action = ActionDocToWordWorkOrWorkCompleted.class)
    @POST
    @Path("doc/to/word/workorworkcompleted/{workOrWorkCompleted}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void docToWordWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
            JsonElement jsonElement) {
        ActionResult<ActionDocToWordWorkOrWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDocToWordWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted,
                    jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "获取指定附件标识返回pdf格式预览文件信息,支持doc,docx.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionPreviewPdf.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "获取指定附件标识返回pdf格式预览文件信息,支持doc,docx.", action = ActionPreviewPdf.class)
    @GET
    @Path("{id}/preview/pdf")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void previewPdf(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionPreviewPdf.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPreviewPdf().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "获取指定附件标识返回pdf格式预览文件,支持doc,docx.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionPreviewPdfResult.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "获取指定附件标识返回pdf格式预览文件,支持doc,docx.", action = ActionPreviewPdfResult.class)
    @GET
    @Path("preview/pdf/{flag}/result")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void previewPdfResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("flag") String flag) {
        ActionResult<ActionPreviewPdfResult.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPreviewPdfResult().execute(effectivePerson, flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "获取指定附件标识返回图片格式预览文件信息,支持doc,docx.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionPreviewImage.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "获取指定附件标识返回图片格式预览文件信息,支持doc,docx.", action = ActionPreviewImage.class)
    @GET
    @Path("{id}/preview/image/page/{page}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void previewImage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("页数") @PathParam("page") Integer page) {
        ActionResult<ActionPreviewImage.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPreviewImage().execute(effectivePerson, id, page);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "获取指定附件标识返回图片格式预览文件,支持doc,docx.", operationId = OPERATIONID_PREFIX
            + "docToWordWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionPreviewImageResult.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "获取指定附件标识返回图片格式预览文件,支持doc,docx.", action = ActionPreviewImageResult.class)
    @GET
    @Path("preview/image/{flag}/result")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void previewImageResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("flag") String flag) {
        ActionResult<ActionPreviewImageResult.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPreviewImageResult().execute(effectivePerson, flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据工作标识或已完成工作标识批量下载附件并压缩,设定使用stream输出.", operationId = OPERATIONID_PREFIX
            + "batchDownloadWithWorkOrWorkCompletedStream", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionBatchDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识或已完成工作标识批量下载附件并压缩,设定使用stream输出.", action = ActionBatchDownload.class)
    @GET
    @Path("batch/download/work/{workId}/site/{site}/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void batchDownloadWithWorkOrWorkCompletedStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("*Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("*附件框分类,多值~隔开,(0)表示全部") @PathParam("site") String site,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName,
            @JaxrsParameterDescribe("通过uploadWorkInfo上传返回的工单信息存储id，多值逗号隔开") @QueryParam("flag") String flag) {
        ActionResult<ActionBatchDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionBatchDownload().execute(effectivePerson, workId, site,
                    fileName, flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据工作标识或已完成工作标识批量下载附件并压缩.", operationId = OPERATIONID_PREFIX
            + "batchDownloadWithWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionBatchDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作标识或已完成工作标识批量下载附件并压缩.", action = ActionBatchDownload.class)
    @GET
    @Path("batch/download/work/{workId}/site/{site}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void batchDownloadWithWorkOrWorkCompleted(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("*Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("*附件框分类,多值~隔开,(0)表示全部") @PathParam("site") String site,
            @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName,
            @JaxrsParameterDescribe("通过uploadWorkInfo上传返回的工单信息存储id，多值逗号隔开") @QueryParam("flag") String flag) {
        ActionResult<ActionBatchDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionBatchDownload().execute(effectivePerson, workId, site, fileName,
                    flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "根据工作的job批量下载附件并压缩.", operationId = OPERATIONID_PREFIX
            + "batchDownload", responses = { @ApiResponse(content = {
            @Content(schema = @Schema(implementation = ActionBatchDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "根据工作的job批量下载附件并压缩.", action = ActionBatchDownload.class)
    @GET
    @Path("batch/download/job/{job}/site/{site}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void batchDownload(@Suspended final AsyncResponse asyncResponse,
                     @Context HttpServletRequest request,
                     @JaxrsParameterDescribe("*工作的job，多值~隔开") @PathParam("job") String job,
                     @JaxrsParameterDescribe("*附件框分类,多值~隔开,(0)表示全部") @PathParam("site") String site,
                     @JaxrsParameterDescribe("下载附件名称") @QueryParam("fileName") String fileName,
                     @JaxrsParameterDescribe("通过uploadWorkInfo上传返回的工单信息存储id，多值逗号隔开") @QueryParam("flag") String flag) {
        ActionResult<ActionBatchDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionBatchDownload().execute(effectivePerson, job, site, fileName, flag);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "上传工单的表单,审批记录等html信息到打包下载附件.", operationId = OPERATIONID_PREFIX
            + "uploadWorkInfo", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUploadWorkInfo.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "上传工单的表单、审批记录等html信息到缓存.", action = ActionUploadWorkInfo.class)
    @PUT
    @Path("upload/work/{workId}/save/as/{flag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void uploadWorkInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("另存为格式：(0)表示不转换|pdf表示转为pdf|word表示转为word") @PathParam("flag") String flag,
            JsonElement jsonElement) {
        ActionResult<ActionUploadWorkInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadWorkInfo().execute(effectivePerson, workId, flag, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "上传工单的表单,审批记录等html信息到打包下载附件(mock put to post).", operationId = OPERATIONID_PREFIX
            + "uploadWorkInfoMockPutToPost", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionUploadWorkInfo.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "上传工单的表单,审批记录等html信息到打包下载附件(mock put to post).", action = ActionUploadWorkInfo.class)
    @POST
    @Path("upload/work/{workId}/save/as/{flag}/mockputtopost")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void uploadWorkInfoMockPutToPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("另存为格式：(0)表示不转换|pdf表示转为pdf|word表示转为word") @PathParam("flag") String flag,
            JsonElement jsonElement) {
        ActionResult<ActionUploadWorkInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUploadWorkInfo().execute(effectivePerson, workId, flag, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "下载工单的表单,审批记录等html信息.", operationId = OPERATIONID_PREFIX + "downloadWorkInfo", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownloadWorkInfo.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "下载工单的表单,审批记录等html信息.", action = ActionDownloadWorkInfo.class)
    @GET
    @Path("download/work/{workId}/att/{flag}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadWorkInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("*Work或WorkCompleted的工作标识") @PathParam("workId") String workId,
            @JaxrsParameterDescribe("*通过uploadWorkInfo上传返回的附件id") @PathParam("flag") String flag,
            @JaxrsParameterDescribe("是否直接下载(true|false)") @QueryParam("stream") Boolean stream) {
        ActionResult<ActionDownloadWorkInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadWorkInfo().execute(effectivePerson, workId, flag, BooleanUtils.isTrue(stream));
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "管理员批量上传附件.", operationId = OPERATIONID_PREFIX + "manageBatchUpload", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageBatchUpload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "管理员批量上传附件.", action = ActionManageBatchUpload.class)
    @POST
    @Path("batch/upload/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void manageBatchUpload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作标识列表，多值逗号隔开") @FormDataParam("workIds") String workIds,
            @JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("上传到指定用户") @FormDataParam("person") String person,
            @JaxrsParameterDescribe("附件排序号") @FormDataParam("orderNumber") Integer orderNumber,
            @JaxrsParameterDescribe("是否根据主工作软拷贝附件到其他工作，所有文档共享主文档的存储附件") @FormDataParam("isSoftUpload") Boolean isSoftUpload,
            @JaxrsParameterDescribe("主工作标志(isSoftUpload为true时必填)") @FormDataParam("mainWork") String mainWork,
            @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionManageBatchUpload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageBatchUpload().execute(effectivePerson, workIds, site, fileName, bytes, disposition,
                    extraParam, person, orderNumber, isSoftUpload, mainWork);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "指定附件标识,管理员下载附件.", operationId = OPERATIONID_PREFIX + "manageDownload", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageDownload.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "指定附件标识,管理员下载附件.", action = ActionManageDownload.class)
    @GET
    @Path("download/{id}/manage")
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageDownload(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionManageDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageDownload().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "指定附件标识,管理员下载附件,设定使用stream输出.", operationId = OPERATIONID_PREFIX
            + "manageDownloadStream", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionManageDownloadStream.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "指定附件标识,管理员下载附件,设定使用stream输出.", action = ActionManageDownloadStream.class)
    @GET
    @Path("download/{id}/manage/stream")
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageDownloadStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionManageDownloadStream.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageDownloadStream().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "html转pdf工具类,转换后通过downloadTransfer接口下载.", operationId = OPERATIONID_PREFIX
            + "htmlToPdf", responses = {
                    @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionHtmlToPdf.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "html转pdf工具类,转换后通过downloadTransfer接口下载.", action = ActionHtmlToPdf.class)
    @POST
    @Path("html/to/pdf")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void htmlToPdf(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionHtmlToPdf.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHtmlToPdf().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "html转pdf工具类,下载转换后的附件.", operationId = OPERATIONID_PREFIX + "downloadTransfer", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionDownloadTransfer.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "html转pdf工具类,下载转换后的附件.", action = ActionDownloadTransfer.class)
    @GET
    @Path("download/transfer/flag/{flag}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void downloadTransfer(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("*转换后附件id") @PathParam("flag") String flag,
            @JaxrsParameterDescribe("是否直接下载(true|false)") @QueryParam("stream") Boolean stream) {
        ActionResult<ActionDownloadTransfer.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDownloadTransfer().execute(effectivePerson, flag, BooleanUtils.isTrue(stream));
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "管理员批量替换附件.", operationId = OPERATIONID_PREFIX + "manageBatchUpdate", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageBatchUpdate.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "管理员批量替换附件.", action = ActionManageBatchUpdate.class)
    @POST
    @Path("batch/update/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void manageBatchUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("附件Id列表，多值逗号隔开") @FormDataParam("ids") String ids,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("天印扩展字段") @FormDataParam("extraParam") String extraParam,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<ActionManageBatchUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageBatchUpdate().execute(effectivePerson, ids, fileName, bytes, disposition,
                    extraParam);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "管理员批量删除附件.", operationId = OPERATIONID_PREFIX + "manageBatchDelete", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionManageBatchDelete.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "管理员批量删除附件.", action = ActionManageBatchDelete.class)
    @POST
    @Path("batch/delete/manage")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void manageBatchDelete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionManageBatchDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionManageBatchDelete().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "上传附件.", operationId = OPERATIONID_PREFIX + "ActionUploadWithUrl", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionUploadWithUrl.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "上传附件", action = ActionUploadWithUrl.class)
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
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "html转图片工具类，通过微软playwright工具以截图方式生成图片，"
            + "转换后如果工作不为空通过downloadWithWork接口下载，为空downloadTransfer接口下载.", operationId = OPERATIONID_PREFIX
                    + "htmlToImage", responses = { @ApiResponse(content = {
                            @Content(schema = @Schema(implementation = ActionHtmlToImage.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "html转图片工具类，通过微软playwright工具以截图方式生成图片，"
            + "转换后如果工作不为空通过downloadWithWork接口下载，为空downloadTransfer接口下载.", action = ActionHtmlToImage.class)
    @POST
    @Path("html/to/image")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void htmlToImage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionHtmlToImage.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHtmlToImage().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "V2根据工作标识或已完成工作标识上传附件,如果同名附件存在则替换.", operationId = OPERATIONID_PREFIX
            + "v2UploadWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = V2UploadWorkOrWorkCompleted.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "V2根据工作标识或已完成工作标识上传附件,如果同名附件存在则替换.", action = V2UploadWorkOrWorkCompleted.class)
    @POST
    @Path("v2/upload/workorworkcompleted/{workOrWorkCompleted}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void v2UploadWorkOrWorkCompleted(FormDataMultiPart form, @Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
            @JaxrsParameterDescribe("位置") @FormDataParam("site") String site,
            @JaxrsParameterDescribe("附件名称") @FormDataParam(FILENAME_FIELD) String fileName,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<V2UploadWorkOrWorkCompleted.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new V2UploadWorkOrWorkCompleted().execute(effectivePerson, workOrWorkCompleted, site, fileName,
                    bytes, disposition);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "V2根据工作标识或已完成工作标识上传附件,上传文件内容经过base64编码后的文本,如果同名附件存在则替换.", operationId = OPERATIONID_PREFIX
            + "v2UploadWorkOrWorkCompleted", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = V2UploadWorkOrWorkCompletedBase64.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "V2根据工作标识或已完成工作标识上传附件,上传文件内容经过base64编码后的文本,如果同名附件存在则替换.", action = V2UploadWorkOrWorkCompletedBase64.class)
    @POST
    @Path("v2/upload/workorworkcompleted/{workOrWorkCompleted}/base64")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void v2UploadWorkOrWorkCompletedBase64(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("工作或已完成工作标识") @PathParam("workOrWorkCompleted") String workOrWorkCompleted,
            JsonElement jsonElement) {
        ActionResult<V2UploadWorkOrWorkCompletedBase64.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new V2UploadWorkOrWorkCompletedBase64().execute(effectivePerson, workOrWorkCompleted, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "获取指定附件的在线编辑信息.", operationId = OPERATIONID_PREFIX + "getOnlineInfo", responses = {
            @ApiResponse(content = { @Content(schema = @Schema(implementation = ActionOnlineInfo.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "获取指定附件的在线编辑信息.", action = ActionOnlineInfo.class)
    @GET
    @Path("{id}/online/info")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getOnlineInfo(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                        @JaxrsParameterDescribe("附件标识") @PathParam("id") String id) {
        ActionResult<ActionOnlineInfo.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionOnlineInfo().execute(effectivePerson, id);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
