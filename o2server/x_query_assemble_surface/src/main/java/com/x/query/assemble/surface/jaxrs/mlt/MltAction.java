package com.x.query.assemble.surface.jaxrs.mlt;

import java.util.List;

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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MltAction", description = "相似检索.")
@Path("mlt")
@JaxrsDescribe("相似检索.")
public class MltAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MltAction.class);
    private static final String OPERATIONID_PREFIX = "MltAction::";

    @Operation(summary = "相似查找.", operationId = OPERATIONID_PREFIX + "list", responses = { @ApiResponse(content = {
            @Content(array = @ArraySchema(schema = @Schema(implementation = ActionList.Wo.class))) }) }, requestBody = @RequestBody(content = {
                    @Content(schema = @Schema(implementation = ActionList.Wi.class)) }))
    @JaxrsMethodDescribe(value = "相似查找.", action = ActionList.class)
    @POST
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void list(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<List<ActionList.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionList().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}