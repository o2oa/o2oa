package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import java.io.InputStream;

/**
 * 微信公众号接口
 * Created by fancyLou on 3/8/21.
 * Copyright © 2021 O2. All rights reserved.
 */

@Path("mpweixin")
@JaxrsDescribe("微信公众号接口")
public class MPWeixinAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(MPWeixinAction.class);


    @JaxrsMethodDescribe(value = "给微信公众号后台检测服务器配置.", action = ActionCheckMPWeixin.class)
    @GET
    @Path("check")
    public void check(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @QueryParam("signature") String signature,
                      @QueryParam("timestamp") Long timestamp, @QueryParam("nonce") String nonce,
                      @QueryParam("echostr") String echostr) {
        ActionResult<ActionCheckMPWeixin.Wo> result = new ActionResult<>();
        try {
            result = new ActionCheckMPWeixin().execute(signature, timestamp, nonce, echostr);
        } catch (Exception e) {
            logger.error(e);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "微信公众号接收消息.", action = ActionReceiveMsg.class)
    @POST
    @Path("check")
    public void receiveMsg(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @QueryParam("signature") String signature,
                           @QueryParam("timestamp") Long timestamp, @QueryParam("nonce") String nonce,
                           @QueryParam("echostr") String echostr, InputStream inputStream) {
        ActionResult<ActionReceiveMsg.Wo> result = new ActionResult<>();
        try {
            result = new ActionReceiveMsg().execute(signature, timestamp, nonce, echostr, inputStream);
        }catch (Exception e) {
            logger.error(e);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
