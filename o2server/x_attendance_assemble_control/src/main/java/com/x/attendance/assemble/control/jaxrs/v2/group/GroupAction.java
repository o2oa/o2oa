package com.x.attendance.assemble.control.jaxrs.v2.group;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
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
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
@Path("v2/group")
@JaxrsDescribe("考勤组管理")
public class GroupAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(GroupAction.class);



    @JaxrsMethodDescribe(value = "获取考勤组对象.", action = ActionGet.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("考勤组ID") @PathParam("id") String id) {
        ActionResult<ActionGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGet().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "刷新考勤组.", action = ActionRefreshParticipate.class)
    @GET
    @Path("{id}/refresh/participate")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void refreshParticipate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                    @JaxrsParameterDescribe("考勤组ID") @PathParam("id") String id) {
        ActionResult<ActionRefreshParticipate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionRefreshParticipate().execute(effectivePerson, id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "分页查询考勤组列表.", action = ActionListByPage.class)
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


    @JaxrsMethodDescribe(value = "创建或更新考勤组信息.", action = ActionCreateUpdate.class)
    @POST
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void createOrUpdate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                            JsonElement jsonElement) {
        ActionResult<ActionCreateUpdate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCreateUpdate().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "删除考勤组.", action = ActionDelete.class)
    @GET
    @Path("{id}/delete")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("考勤组ID") @PathParam("id") String id) {
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

    @JaxrsMethodDescribe(value = "当前考勤组重新生成某一天的考勤数据.", action = ActionRebuildDetailWithGroupDate.class)
    @GET
    @Path("rebuild/detail/group/{groupId}/date/{date}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void rebuildDetail(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("考勤组ID") @PathParam("groupId") String groupId, @JaxrsParameterDescribe("日期: yyyy-MM-dd") @PathParam("date") String date) {
        ActionResult<ActionRebuildDetailWithGroupDate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionRebuildDetailWithGroupDate().execute(effectivePerson, groupId, date);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "获取人员某一天的考勤组和班次对象.", action = ActionGetWithShiftByPersonDate.class)
    @GET
    @Path("person/{person}/date/{date}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getGroupWithShiftByPersonDate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                       @JaxrsParameterDescribe("人员DN") @PathParam("person") String person, @JaxrsParameterDescribe("日期: yyyy-MM-dd") @PathParam("date") String date) {
        ActionResult<WoGroupShift> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGetWithShiftByPersonDate().execute(person, date);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
