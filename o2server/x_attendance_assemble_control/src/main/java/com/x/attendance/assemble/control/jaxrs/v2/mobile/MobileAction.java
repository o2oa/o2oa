package com.x.attendance.assemble.control.jaxrs.v2.mobile;

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

/**
 * Created by fancyLou on 2023/2/22.
 * Copyright © 2023 O2. All rights reserved.
 */

@Path("v2/mobile")
@JaxrsDescribe("移动端API")
public class MobileAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(MobileAction.class);



    @JaxrsMethodDescribe(value = "打卡前获取打卡需要信息的请求.", action = ActionPreCheck.class)
    @GET
    @Path("check/pre")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void preCheckIn(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionPreCheck.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPreCheck().execute(effectivePerson);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "打卡，打卡之前需要先调用preCheckIn接口获取数据.", action = ActionCheckIn.class)
    @POST
    @Path("check")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void checkIn(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                            JsonElement jsonElement) {
        ActionResult<ActionCheckIn.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionCheckIn().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


}
