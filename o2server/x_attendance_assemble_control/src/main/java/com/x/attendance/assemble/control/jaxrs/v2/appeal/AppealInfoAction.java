package com.x.attendance.assemble.control.jaxrs.v2.appeal;

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
 * Created by fancyLou on 2023/3/3.
 * Copyright © 2023 O2. All rights reserved.
 */
@Path("v2/appeal")
@JaxrsDescribe("申诉管理")
public class AppealInfoAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(AppealInfoAction.class);



    @JaxrsMethodDescribe(value = "分页查询当前用户的申诉数据列表.", action = ActionListByPage.class)
    @POST
    @Path("list/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listByPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                             @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
                             @JaxrsParameterDescribe("数量") @PathParam("size") Integer size) {
        ActionResult<List<ActionListByPage.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListByPage().execute(effectivePerson, page, size);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "管理员查询申诉数据列表.", action = ActionListByPageByAdmin.class)
    @POST
    @Path("list/manager/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void managerListByPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                             @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
                             @JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
        ActionResult<List<ActionListByPageByAdmin.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListByPageByAdmin().execute(effectivePerson, page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }



    @JaxrsMethodDescribe(value = "管理员处理异常数据为正常.", action = ActionUpdateStatusByAdmin.class)
    @GET
    @Path("{id}/manager/status")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void managerSetNormal(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("申诉数据ID") @PathParam("id") String id) {
        ActionResult<ActionUpdateStatusByAdmin.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateStatusByAdmin().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "申诉数据获取.", action = ActionGet.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("申诉数据ID") @PathParam("id") String id) {
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



    @JaxrsMethodDescribe(value = "检查是否能够申诉.", action = ActionCheckCanStartAppeal.class)
    @GET
    @Path("{id}/start/check")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void startCheck(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("申诉数据ID") @PathParam("id") String id) {
        ActionResult<ActionCheckCanStartAppeal.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCheckCanStartAppeal().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }



    @JaxrsMethodDescribe(value = "启动流程后修改状态.", action = ActionUpdateForStart.class)
    @POST
    @Path("{id}/start/process")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void startProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("申诉数据ID") @PathParam("id") String id,
                             JsonElement jsonElement) {
        ActionResult<ActionUpdateForStart.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateForStart().execute(effectivePerson, id, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "还原数据状态，清除流程关联.", action = ActionUpdateForResetStatus.class)
    @GET
    @Path("{id}/reset/status")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void resetStatus(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("申诉数据ID") @PathParam("id") String id) {
        ActionResult<ActionUpdateForResetStatus.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateForResetStatus().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "流程结束后回填数据.", action = ActionUpdateForEnd.class)
    @POST
    @Path("{id}/end/process")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void endProcess(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                           @JaxrsParameterDescribe("申诉数据ID") @PathParam("id") String id,
                               JsonElement jsonElement) {
        ActionResult<ActionUpdateForEnd.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionUpdateForEnd().execute(id, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
