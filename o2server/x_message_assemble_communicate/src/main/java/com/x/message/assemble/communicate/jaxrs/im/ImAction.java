package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("im")
@JaxrsDescribe("IM消息")
public class ImAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(ImAction.class);



    /************* im conversation ************/

    @JaxrsMethodDescribe(value = "创建会话.", action = ActionConversationCreate.class)
    @POST
    @Path("conversation")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void create(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       JsonElement jsonElement) {
        ActionResult<ActionConversationCreate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionConversationCreate().execute( effectivePerson, jsonElement );
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    //conversation/{id}  GET 如果没有扩展就创建扩展
    @JaxrsMethodDescribe(value = "会话对象.", action = ActionGetConversation.class)
    @GET
    @Path("conversation/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void conversation(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                   @JaxrsParameterDescribe("会话id") @PathParam("id") String id) {
        ActionResult<ActionGetConversation.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetConversation().execute( effectivePerson, id );
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    //conversation/{id}/read  阅读消息 PUT
    @JaxrsMethodDescribe(value = "会话阅读消息.", action = ActionConversationRead.class)
    @PUT
    @Path("conversation/{id}/read")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void conversationRead(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                 @JaxrsParameterDescribe("会话id") @PathParam("id") String id) {
        ActionResult<WoId> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionConversationRead().execute( effectivePerson, id );
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    //conversation/{id}/top  置顶会话 PUT
    @JaxrsMethodDescribe(value = "会话置顶.", action = ActionConversationSetTop.class)
    @PUT
    @Path("conversation/{id}/top/set")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void conversationSetTop(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                   @JaxrsParameterDescribe("会话id") @PathParam("id") String id) {
        ActionResult<WoId> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionConversationSetTop().execute( effectivePerson, id );
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    //conversation/{id}/top/cancel  置顶会话 PUT
    @JaxrsMethodDescribe(value = "会话取消置顶.", action = ActionConversationCancelTop.class)
    @PUT
    @Path("conversation/{id}/top/cancel")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void conversationCancelTop(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                   @JaxrsParameterDescribe("会话id") @PathParam("id") String id) {
        ActionResult<WoId> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionConversationCancelTop().execute( effectivePerson, id );
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "我的会话列表.", action = ActionMyConversationList.class)
    @GET
    @Path("conversation/list/my")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void myConversationList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<List<ActionMyConversationList.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionMyConversationList().execute( effectivePerson );
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }



    /************* im message ************/

    @JaxrsMethodDescribe(value = "创建消息，发送消息到某一个会话中.", action = ActionMsgCreate.class)
    @POST
    @Path("msg")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void msgCreate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       JsonElement jsonElement) {
        ActionResult<ActionMsgCreate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionMsgCreate().execute( effectivePerson, jsonElement );
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }



    @JaxrsMethodDescribe(value = "分页查询某个会话的消息列表.", action = ActionMsgListWithConversationByPage.class)
    @POST
    @Path("msg/list/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void msgListByPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                   @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
                                   @JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
        ActionResult<List<ActionMsgListWithConversationByPage.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionMsgListWithConversationByPage().execute(effectivePerson, page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


}
