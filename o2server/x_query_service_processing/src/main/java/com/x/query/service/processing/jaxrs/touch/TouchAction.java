package com.x.query.service.processing.jaxrs.touch;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("touch")
@Tag(name = "TouchAction", description = "触发任务.")
@JaxrsDescribe("触发任务.")
public class TouchAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TouchAction.class);

    private static final String OPERATIONID_PREFIX = "TouchAction::";

    @Operation(summary = "执行在流转工作高频索引,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "highFreqWorkTouch", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionHighFreqWorkTouch.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "执行在流转工作高频索引,(0)表示在所有节点上执行.", action = ActionHighFreqWorkTouch.class)
    @GET
    @Path("high/freq/work/node/{node}/touch")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void highFreqWorkTouch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionHighFreqWorkTouch.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHighFreqWorkTouch().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "重置在流转工作高频索引定时任务状态,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "highFreqWorkReset", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionHighFreqWorkReset.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "重置在流转工作高频索引定时任务状态,(0)表示在所有节点上执行.", action = ActionHighFreqWorkReset.class)
    @GET
    @Path("high/freq/work/node/{node}/reset")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void highFreqWorkReset(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionHighFreqWorkReset.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHighFreqWorkReset().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "执行在流转工作低频索引,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "lowFreqIndexWorkTouch", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionLowFreqWorkTouch.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "执行在流转工作低频索引,(0)表示在所有节点上执行.", action = ActionLowFreqWorkTouch.class)
    @GET
    @Path("low/freq/work/node/{node}/touch")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void lowFreqWorkTouch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionLowFreqWorkTouch.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLowFreqWorkTouch().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "重置在流转工作低频索引定时任务状态,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "lowFreqWorkReset", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionLowFreqWorkReset.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "重置在流转工作低频索引定时任务状态,(0)表示在所有节点上执行.", action = ActionLowFreqWorkReset.class)
    @GET
    @Path("low/freq/work/node/{node}/reset")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void lowFreqWorkReset(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionLowFreqWorkReset.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLowFreqWorkReset().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "执行已完成工作高频索引,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "highFreqWorkCompletedTouch", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionHighFreqWorkCompletedTouch.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "执行已完成工作高频索引,(0)表示在所有节点上执行.", action = ActionHighFreqWorkCompletedTouch.class)
    @GET
    @Path("high/freq/workcompleted/node/{node}/touch")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void highFreqWorkCompletedTouch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionHighFreqWorkCompletedTouch.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHighFreqWorkCompletedTouch().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "重置已完成工作高频索引定时任务状态,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "highFreqWorkCompletedReset", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionHighFreqWorkCompletedReset.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "重置已完成工作高频索引定时任务状态,(0)表示在所有节点上执行.", action = ActionHighFreqWorkCompletedReset.class)
    @GET
    @Path("high/freq/workcompleted/node/{node}/reset")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void highFreqWorkCompletedReset(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionHighFreqWorkCompletedReset.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHighFreqWorkCompletedReset().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "执行已完成工作低频索引,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "lowFreqIndexWorkCompletedTouch", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionLowFreqWorkCompletedTouch.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "执行已完成工作低频索引,(0)表示在所有节点上执行.", action = ActionLowFreqWorkCompletedTouch.class)
    @GET
    @Path("low/freq/workcompleted/node/{node}/touch")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void lowFreqWorkCompletedTouch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionLowFreqWorkCompletedTouch.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLowFreqWorkCompletedTouch().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "重置已完成工作低频索引定时任务状态,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "lowFreqWorkCompletedReset", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionLowFreqWorkCompletedReset.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "重置已完成工作低频索引定时任务状态,(0)表示在所有节点上执行.", action = ActionLowFreqWorkCompletedReset.class)
    @GET
    @Path("low/freq/workcompleted/node/{node}/reset")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void lowFreqWorkCompletedReset(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionLowFreqWorkCompletedReset.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLowFreqWorkCompletedReset().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "执行文档高频索引,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX + "highFreqDocumentTouch", responses = {
            @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionHighFreqDocumentTouch.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "执行文档高频索引,(0)表示在所有节点上执行.", action = ActionHighFreqDocumentTouch.class)
    @GET
    @Path("high/freq/document/node/{node}/touch")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void highFreqDocumentTouch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionHighFreqDocumentTouch.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHighFreqDocumentTouch().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "重置文档高频索引定时任务状态,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "highFreqDocumentReset", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionHighFreqDocumentReset.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "重置文档高频索引定时任务状态,(0)表示在所有节点上执行.", action = ActionHighFreqDocumentReset.class)
    @GET
    @Path("high/freq/document/node/{node}/reset")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void highFreqDocumentReset(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionHighFreqDocumentReset.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHighFreqDocumentReset().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "执行文档低频索引,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX + "lowFreqDocumentTouch", responses = {
            @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionLowFreqDocumentTouch.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "执行文档低频索引,(0)表示在所有节点上执行.", action = ActionLowFreqDocumentTouch.class)
    @GET
    @Path("low/freq/document/node/{node}/touch")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void lowFreqDocumentTouch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionLowFreqDocumentTouch.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLowFreqDocumentTouch().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "重置文档低频索引定时任务状态,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX
            + "lowFreqDocumentReset", responses = { @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionLowFreqDocumentReset.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "重置文档低频索引定时任务状态,(0)表示在所有节点上执行.", action = ActionLowFreqDocumentReset.class)
    @GET
    @Path("low/freq/document/node/{node}/reset")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void lowFreqDocumentReset(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionLowFreqDocumentReset.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLowFreqDocumentReset().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "执行优化索引,(0)表示在所有节点上执行.", operationId = OPERATIONID_PREFIX + "optimizeIndex", responses = {
            @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionOptimizeIndex.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "执行优化索引,(0)表示在所有节点上执行.", action = ActionOptimizeIndex.class)
    @GET
    @Path("optimize/index/{node}/touch")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void optimizeIndex(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, @JaxrsParameterDescribe("节点") @PathParam("node") String node) {
        ActionResult<ActionOptimizeIndex.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionOptimizeIndex().execute(effectivePerson, node);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}