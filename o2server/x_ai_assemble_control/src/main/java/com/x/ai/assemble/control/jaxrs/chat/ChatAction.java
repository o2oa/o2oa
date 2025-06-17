package com.x.ai.assemble.control.jaxrs.chat;

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
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 * @author sword
 */
@Path("chat")
@JaxrsDescribe("AI智能服务")
public class ChatAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatAction.class);

    @JaxrsMethodDescribe(value = "连接ai", action = ActionChat.class)
    @POST
    @Path("completion")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    @Consumes(MediaType.APPLICATION_JSON)
    public void completion(@Context HttpServletRequest request,
            @Context Sse sse,
            @Context SseEventSink eventSink,
            JsonElement jsonElement) {
        final EffectivePerson effectivePerson = this.effectivePerson(request);
        final ActionChat actionChat = new ActionChat();
        new Thread(() -> actionChat.execute(effectivePerson, sse, eventSink, jsonElement)).start();
    }

    @JaxrsMethodDescribe(value = "分页查找线索.", action = ActionListPaging.class)
    @GET
    @Path("list/paging/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPaging(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size) {
        ActionResult<List<ActionListPaging.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListPaging().execute(effectivePerson, page, size);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据线索分页查找对话.", action = ActionListCompletionPaging.class)
    @GET
    @Path("list/completion/{clueId}/paging/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listCompletionPaging(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("线索ID") @PathParam("clueId") String clueId,
            @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("每页数量") @PathParam("size") Integer size) {
        ActionResult<List<ActionListCompletionPaging.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListCompletionPaging().execute(effectivePerson, clueId, page, size);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "删除线索.", action = ActionDelete.class)
    @GET
    @Path("delete/{clueId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("线索标识") @PathParam("clueId") String clueId) {
        ActionResult<ActionDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDelete().execute(effectivePerson, clueId);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "写入对话扩展数据", action = ActionWriteCompletion.class)
    @POST
    @Path("write/completion/extra")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void writeCompletionExtra(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<ActionWriteCompletion.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionWriteCompletion().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            LOGGER.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));

    }

}
