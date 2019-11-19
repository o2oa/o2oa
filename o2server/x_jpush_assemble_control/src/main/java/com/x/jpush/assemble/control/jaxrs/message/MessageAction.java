package com.x.jpush.assemble.control.jaxrs.message;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("message")
@JaxrsDescribe("极光推送消息服务模块")
public class MessageAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger( MessageAction.class );


    @JaxrsMethodDescribe( value = "测试发送消息", action = ActionSendMessage.class )
    @POST
    @Path("test/send")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessageTest(@Context HttpServletRequest request, @JaxrsParameterDescribe("发送的消息内容") JsonElement jsonElement) {

        ActionResult<ActionSendMessageTest.Wo> result = new ActionResult<>();
        try{
            result = new ActionSendMessageTest().execute(request, this.effectivePerson(request), jsonElement);
        }catch (Exception e) {
            result = new ActionResult<>();
            Exception exception = new ExceptionSendMessage( e, "绑定设备时发生异常！" );
            result.error( exception );
            logger.error( e, this.effectivePerson(request), request, null);
        }
        return ResponseFactory.getDefaultActionResultResponse(result);
    }

    @JaxrsMethodDescribe( value = "发送消息", action = ActionSendMessage.class )
    @POST
    @Path("send")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(@Context HttpServletRequest request, @JaxrsParameterDescribe("发送的消息内容") JsonElement jsonElement) {
        ActionResult<ActionSendMessage.Wo> result = new ActionResult<>();
        try{
            result = new ActionSendMessage().execute(request, this.effectivePerson(request), jsonElement);
        }catch (Exception e) {
            result = new ActionResult<>();
            Exception exception = new ExceptionSendMessage( e, "绑定设备时发生异常！" );
            result.error( exception );
            logger.error( e, this.effectivePerson(request), request, null);
        }
        return ResponseFactory.getDefaultActionResultResponse(result);
    }





}
