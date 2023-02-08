package com.x.query.service.processing.jaxrs.index;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "IndexAction", description = "检索接口.")
@Path("index")
@JaxrsDescribe("检索接口.")
public class IndexAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexAction.class);
    private static final String OPERATIONID_PREFIX = "IndexAction::";

    @Operation(summary = "目录中索引数量,category,type,key为空统计search目录数量.", operationId = OPERATIONID_PREFIX
            + "count", responses = {
                    @ApiResponse(content = {
                            @Content(schema = @Schema(implementation = ActionDirectoryDocumentCount.Wo.class)) }) }, requestBody = @RequestBody(content = {
                                    @Content(schema = @Schema(implementation = ActionDirectoryDocumentCount.Wi.class)) }))
    @JaxrsMethodDescribe(value = "目录中索引数量,category,type,key为空统计search目录数量.", action = ActionDirectoryDocumentCount.class)
    @POST
    @Path("directory/document/count")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void directoryDocumentCount(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionDirectoryDocumentCount.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDirectoryDocumentCount().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
