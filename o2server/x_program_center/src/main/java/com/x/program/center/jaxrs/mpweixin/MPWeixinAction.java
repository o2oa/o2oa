package com.x.program.center.jaxrs.mpweixin;

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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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


    // /menu/* 需要管理员权限

    @JaxrsMethodDescribe(value = "微信菜单列表查看.", action = ActionListAllMenu.class)
    @GET
    @Path("menu/list/weixin")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void menuWeixinList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionListAllMenu.Wo> result = new ActionResult<>();
        try {
            result = new ActionListAllMenu().execute();
        } catch (Exception e) {
            logger.error(e);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "给微信公众号创建菜单，【注意这个接口会把公众号菜单全部替换掉！】.", action = ActionCreateMenu.class)
    @GET
    @Path("menu/create/to/weixin")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void menuCreate2Weixin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionCreateMenu.Wo> result = new ActionResult<>();
        try {
            result = new ActionCreateMenu().execute();
        } catch (Exception e) {
            logger.error(e);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "创建一个菜单项.", action = ActionAddMenu.class)
    @POST
    @Path("menu/add")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void menuAdd(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                          JsonElement jsonElement) {
        ActionResult<ActionAddMenu.Wo> result = new ActionResult<>();
        try {
            result = new ActionAddMenu().execute(jsonElement);
        } catch (Exception e) {
            logger.error(e);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "更新一个菜单项.", action = ActionUpdateMenu.class)
    @POST
    @Path("menu/update/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void menuUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @PathParam("id") String id,
                        JsonElement jsonElement) {
        ActionResult<ActionUpdateMenu.Wo> result = new ActionResult<>();
        try {
            result = new ActionUpdateMenu().execute(id, jsonElement);
        } catch (Exception e) {
            logger.error(e);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }




    @JaxrsMethodDescribe(value = "删除一个菜单项.", action = ActionDeleteMenu.class)
    @DELETE
    @Path("menu/delete/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void menuDelete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                        @PathParam("id") String id) {
        ActionResult<ActionDeleteMenu.Wo> result = new ActionResult<>();
        try {
            result = new ActionDeleteMenu().execute(id);
        } catch (Exception e) {
            logger.error(e);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
