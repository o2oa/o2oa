package com.x.attendance.assemble.control.jaxrs.v2.group.schedule;

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
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
@Path("v2/groupschedule")
@JaxrsDescribe("考勤组排班管理")
public class GroupScheduleAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(GroupScheduleAction.class);


    @JaxrsMethodDescribe(value = "排班配置数据，方便排班处理.", action = ActionScheduleConfigGet.class)
    @GET
    @Path("config/group/{groupId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void configByGroupId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("考勤组ID") @PathParam("groupId") String groupId) {
        ActionResult<ActionScheduleConfigGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionScheduleConfigGet().execute(groupId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "获取月份排班数据.", action = ActionScheduleList.class)
    @GET
    @Path("list/group/{groupId}/month/{month}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listMonth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("考勤组ID") @PathParam("groupId") String groupId,
                       @JaxrsParameterDescribe("月份：yyyy-MM") @PathParam("month") String month) {
        ActionResult<List<BaseAction.ScheduleWo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionScheduleList().execute(groupId, month);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
    
    @JaxrsMethodDescribe(value = "排班数据查询，后端接口.", action = ActionScheduleListFilter.class)
    @POST
    @Path("list/filter")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void  listFilter(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                            JsonElement jsonElement) {
        ActionResult<List<BaseAction.ScheduleWo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionScheduleListFilter().execute(jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "月份排班.", action = ActionSchedulePost.class)
    @POST
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void postMonth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                            JsonElement jsonElement) {
        ActionResult<ActionSchedulePost.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionSchedulePost().execute(jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
