package com.x.attendance.assemble.control.jaxrs.v2.shift;

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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by fancyLou on 2023/1/31.
 * Copyright © 2023 O2. All rights reserved.
 */

@Path("v2/shift")
@JaxrsDescribe("班次管理")
public class ShiftAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(ShiftAction.class);




    @JaxrsMethodDescribe(value = "分页查询班次列表.", action = ActionListByPage.class)
    @POST
    @Path("list/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listByPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
                                @JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
        ActionResult<List<ActionListByPage.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListByPage().execute(effectivePerson, page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "创建班次信息.", action = ActionPost.class)
    @POST
    @Path("create")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void shiftCreate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
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

    @JaxrsMethodDescribe(value = "修改班次信息.", action = ActionUpdate.class)
    @POST
    @Path("update")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void shiftUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                          JsonElement jsonElement) {
        ActionResult<ActionUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdate().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "删除班次.", action = ActionDelete.class)
    @GET
    @Path("delete/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                     @JaxrsParameterDescribe("班次ID") @PathParam("id") String id) {
        ActionResult<ActionDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDelete().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "获取班次.", action = ActionGet.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                     @JaxrsParameterDescribe("班次ID") @PathParam("id") String id) {
        ActionResult<ActionGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGet().execute(id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
