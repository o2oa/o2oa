package com.x.attendance.assemble.control.jaxrs.dingding;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("dingding")
@JaxrsDescribe("钉钉打卡数据管理")
public class DingdingAttendanceAction extends StandardJaxrsAction {


    private static final Logger logger = LoggerFactory.getLogger(DingdingAttendanceAction.class);


    //删除所有打卡数据
    @JaxrsMethodDescribe(value = "删除所有打卡数据", action = ActionDeleteAllData.class)
    @DELETE
    @Path("all")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteAllData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<WrapBoolean> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteAllData().execute(effectivePerson);
        }catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    //
    @JaxrsMethodDescribe(value = "同步钉钉考勤结果", action = ActionSyncData.class)
    @GET
    @Path("sync/from/{dateFrom}/to/{dateTo}/start")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void syncData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                         @JaxrsParameterDescribe("开始时间: yyyy-MM-dd") @PathParam("dateFrom") String dateFrom,
                         @JaxrsParameterDescribe("结束时间: yyyy-MM-dd") @PathParam("dateTo") String dateTo) {
        ActionResult<WrapBoolean> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionSyncData().execute(effectivePerson, dateFrom, dateTo);
        }catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "查询钉钉同步记录信息", action = ActionListDingdingSyncRecord.class)
    @GET
    @Path("sync/list")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listDingdingSyncRecord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<List<ActionListDingdingSyncRecord.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListDingdingSyncRecord().execute();
        }catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "查询钉钉打卡结果", action = ActionListDDAttendanceDetail.class)
    @PUT
    @Path("attendance/list")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listDingdingAttendance(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<List<ActionListDDAttendanceDetail.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListDDAttendanceDetail().execute(jsonElement);
        }catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


}
