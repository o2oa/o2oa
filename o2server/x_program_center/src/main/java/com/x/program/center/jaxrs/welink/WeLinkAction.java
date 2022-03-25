package com.x.program.center.jaxrs.welink;

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
 * Created by fancyLou on 2020-07-24.
 * Copyright © 2020 O2. All rights reserved.
 */
@Path("welink")
@JaxrsDescribe("WeLink接口")
public class WeLinkAction extends StandardJaxrsAction {


    private static Logger logger = LoggerFactory.getLogger(WeLinkAction.class);

    @JaxrsMethodDescribe(value = "发送一个拉入同步请求.", action = ActionSyncOrgnaizationCallback.class)
    @POST
    @Path("request/pull/sync")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void requestPullSync(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                JsonElement jsonElement) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionSyncOrgnaizationCallback.Wo> result = new ActionResult<>();
        try {
            result = new ActionSyncOrgnaizationCallback().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
    }

    @JaxrsMethodDescribe(value = "立即同步.", action = ActionPullSync.class)
    @GET
    @Path("pull/sync")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void pullSync(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionPullSync.Wo> result = new ActionResult<>();
        try {
            result = new ActionPullSync().execute(effectivePerson);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


}
