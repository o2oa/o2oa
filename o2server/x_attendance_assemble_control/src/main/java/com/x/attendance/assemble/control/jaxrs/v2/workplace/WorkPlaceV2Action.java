package com.x.attendance.assemble.control.jaxrs.v2.workplace;

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
 * Created by fancyLou on 2023/1/31.
 * Copyright © 2023 O2. All rights reserved.
 */


@Path("v2/workplace")
@JaxrsDescribe("工作地点信息管理服务")
public class WorkPlaceV2Action extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(WorkPlaceV2Action.class);

    @JaxrsMethodDescribe(value = "新建或者更新工作地点信息", action = ActionSave.class)
    @POST
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void post(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                     JsonElement jsonElement) {
        ActionResult<ActionSave.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionSave().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }



    @JaxrsMethodDescribe(value = "删除工作地点.", action = ActionDelete.class)
    @DELETE
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                                     @JaxrsParameterDescribe("班次ID") @PathParam("id") String id) {
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



    @JaxrsMethodDescribe(value = "全部的工作地点列表.", action = ActionListAll.class)
    @GET
    @Path("list/all")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listAll(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<List<ActionListAll.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListAll().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "根据id列表", action = ActionListWithWorkPlaceObject.class)
    @POST
    @Path("list/ids")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listWithWorkPlaceObject(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                     JsonElement jsonElement) {
        ActionResult<List<ActionListWithWorkPlaceObject.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListWithWorkPlaceObject().execute(jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
