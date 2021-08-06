package com.x.organization.assemble.authentication.jaxrs.welink;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

/**
 * Created by fancyLou on 2020-07-27.
 * Copyright © 2020 O2. All rights reserved.
 */

@Path("welink")
@JaxrsDescribe("WeLink单点登录")
public class WeLinkAction extends StandardJaxrsAction {


    private static Logger logger = LoggerFactory.getLogger(WeLinkAction.class);

    @JaxrsMethodDescribe(value = "WeLink单点登录.", action = ActionLogin.class)
    @GET
    @Path("code/{code}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getLogin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                         @Context HttpServletResponse response, @JaxrsParameterDescribe("登录code") @PathParam("code") String code) {
        ActionResult<ActionLogin.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLogin().execute(request, response, effectivePerson, code);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
