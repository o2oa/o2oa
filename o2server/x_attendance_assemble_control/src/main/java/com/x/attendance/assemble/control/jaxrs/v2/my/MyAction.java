package com.x.attendance.assemble.control.jaxrs.v2.my;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.StatisticWo;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * Created by fancyLou on 2023/3/9.
 * Copyright © 2023 O2. All rights reserved.
 */

@Path("v2/my")
@JaxrsDescribe("我的考勤API")
public class MyAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(MyAction.class);



    @JaxrsMethodDescribe(value = "考勤版本查询.", action = ActionVersion.class)
    @GET
    @Path("version")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void versionCheck(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionVersion.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionVersion().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "我的考勤权限.", action = ActionMyControl.class)
    @GET
    @Path("controls")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void controls(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionMyControl.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionMyControl().execute(effectivePerson);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据日期查询我的考勤数据.", action = ActionListDetailWithDate.class)
    @POST
    @Path("detail/list")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listDetailWithDate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<List<ActionListDetailWithDate.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListDetailWithDate().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据日期查询我的考勤统计.", action = ActionMyStatistic.class)
    @POST
    @Path("statistic")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void statistic(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<StatisticWo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionMyStatistic().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "是否是休息日.", action = ActionDateIsRestDay.class)
    @POST
    @Path("rest/date/check")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void checkIsMyRestDay(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<ActionDateIsRestDay.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDateIsRestDay().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
