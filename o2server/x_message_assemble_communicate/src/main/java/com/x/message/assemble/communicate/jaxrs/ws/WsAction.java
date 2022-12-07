package com.x.message.assemble.communicate.jaxrs.ws;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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

@Tag(name = "WsAction", description = "webSocket接口.")
@Path("ws")
@JaxrsDescribe("webSocket接口.")
public class WsAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsAction.class);
    private static final String OPERATIONID_PREFIX = "WsAction::";

    @Operation(summary = "发送webSocket消息.", operationId = OPERATIONID_PREFIX + "create", responses = {
            @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionCreate.Wo.class)) }) }, requestBody = @RequestBody(content = {
                            @Content(schema = @Schema(implementation = ActionCreate.Wi.class)) }))
    @JaxrsMethodDescribe(value = "发送webSocket消息.", action = ActionCreate.class)
    @POST
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

    @Operation(summary = "在线用户数量.", operationId = OPERATIONID_PREFIX + "countPerson", responses = {
            @ApiResponse(content = {
                    @Content(schema = @Schema(implementation = ActionCountPerson.Wo.class)) }) })
    @JaxrsMethodDescribe(value = "在线用户数量.", action = ActionCountPerson.class)
    @GET
    @Path("count/person")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void countPerson(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request) {
        ActionResult<ActionCountPerson.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCountPerson().execute(effectivePerson);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "列示当前节点用户.", operationId = OPERATIONID_PREFIX + "listPersonCurrentNode", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPersonCurrentNode.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "列示当前节点用户.", action = ActionListPersonCurrentNode.class)
    @GET
    @Path("list/person/current/node")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPersonCurrentNode(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request) {
        ActionResult<List<ActionListPersonCurrentNode.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPersonCurrentNode().execute(effectivePerson);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @Operation(summary = "列示用户.", operationId = OPERATIONID_PREFIX + "listPerson", responses = {
            @ApiResponse(content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ActionListPerson.Wo.class))) }) })
    @JaxrsMethodDescribe(value = "列示用户.", action = ActionListPerson.class)
    @GET
    @Path("list/person")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPerson(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request) {
        ActionResult<List<ActionListPerson.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPerson().execute(effectivePerson);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}