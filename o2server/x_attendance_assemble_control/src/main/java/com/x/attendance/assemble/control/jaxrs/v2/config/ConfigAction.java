package com.x.attendance.assemble.control.jaxrs.v2.config;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.group.ActionDelete;
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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */
@Path("v2/config")
@JaxrsDescribe("配置管理")
public class ConfigAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(ConfigAction.class);


    @JaxrsMethodDescribe(value = "保存配置信息.", action = ActionPost.class)
    @POST
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void post(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                               JsonElement jsonElement) {
        ActionResult<ActionPost.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPost().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "配置信息.", action = ActionGet.class)
    @GET
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGet().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "保存个人配置信息.", action = ActionPersonConfigPost.class)
    @POST
    @Path("person")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void postPersonConfig(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                     JsonElement jsonElement) {
        ActionResult<ActionPersonConfigPost.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPersonConfigPost().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "个人配置信息.", action = ActionPersonConfigGet.class)
    @GET
    @Path("person")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getPersonConfig(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionPersonConfigGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPersonConfigGet().execute(effectivePerson);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


}
