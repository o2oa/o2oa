package com.x.attendance.assemble.control.jaxrs.dingdingstatistic;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
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

/**
 * Created by fancyLou on 2020-04-05.
 * Copyright © 2020 O2. All rights reserved.
 */
@Path("dingdingstatistic")
@JaxrsDescribe("钉钉打卡数据统计管理")
public class DingdingAttendanceStatisticAction extends StandardJaxrsAction {
    private static final Logger logger = LoggerFactory.getLogger(DingdingAttendanceStatisticAction.class);


    @JaxrsMethodDescribe(value = "测试", action = ActionTest.class)
    @GET
    @Path("demo")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void syncData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<WrapBoolean> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionTest().execute();
        }catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
