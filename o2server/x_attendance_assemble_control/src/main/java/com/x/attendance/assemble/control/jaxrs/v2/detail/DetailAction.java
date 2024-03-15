package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWo;
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
 * Created by fancyLou on 2023/2/27.
 * Copyright © 2023 O2. All rights reserved.
 */

@Path("v2/detail")
@JaxrsDescribe("考勤数据管理")
public class DetailAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(DetailAction.class);

    @JaxrsMethodDescribe(value = "重新生成考勤信息，人员: 人员DN，日期: yyyy-MM-dd.", action = ActionRebuildDetailWithPersonDate.class)
    @GET
    @Path("rebuild/person/{person}/date/{date}/")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void rebuild(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("人员") @PathParam("person") String person,
            @JaxrsParameterDescribe("日期：yyyy-MM-dd") @PathParam("date") String date) {
        ActionResult<ActionRebuildDetailWithPersonDate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionRebuildDetailWithPersonDate().execute(effectivePerson, person, date);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "分页查询考勤信息列表.", action = ActionListByPage.class)
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
            result = new ActionListByPage().execute(page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "统计查询.", action = ActionStatisticWithFilter.class)
    @POST
    @Path("statistic/filter")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void statistic(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<List<StatisticWo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionStatisticWithFilter().execute(jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "统计导出.", action = ActionStatisticExportExcel.class)
    @POST
    @Path("statistic/export/filter")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void statisticExport(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<ActionStatisticExportExcel.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionStatisticExportExcel().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据统计id查询相关的打卡记录列表.", action = ActionRecordListByDetailId.class)
    @GET
    @Path("statistic/{detailId}/list/record")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void recordList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("detail 统计 id") @PathParam("detailId") String detailId) {
        ActionResult<List<ActionRecordListByDetailId.WoRecord>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionRecordListByDetailId().execute(effectivePerson, detailId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
