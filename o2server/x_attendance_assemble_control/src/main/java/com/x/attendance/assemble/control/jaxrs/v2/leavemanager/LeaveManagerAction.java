package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

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
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("v2/leavemanager")
@JaxrsDescribe("假期管理")
public class LeaveManagerAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(LeaveManagerAction.class);

    @JaxrsMethodDescribe(value = "保存假期类型数据.", action = ActionLeaveTypePost.class)
    @POST
    @Path("type")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typePost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionLeaveTypePost.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveTypePost().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "保存假期规则数据.", action = ActionLeavePolicyPost.class)
    @POST
    @Path("policy")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void policyPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionLeavePolicyPost.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeavePolicyPost().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "假期规则列表.", action = ActionLeavePolicyList.class)
    @GET
    @Path("policy/list")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<List<ActionLeavePolicyList.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeavePolicyList().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
