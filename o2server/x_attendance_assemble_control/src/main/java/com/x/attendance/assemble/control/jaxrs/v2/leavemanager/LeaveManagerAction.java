package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

@Path("v2/leavemanager")
@JaxrsDescribe("假期管理")
public class LeaveManagerAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(LeaveManagerAction.class);

    @JaxrsMethodDescribe(value = "保存假期类型数据.", action = ActionLeaveTypePost.class)
    @POST
    @Path("type")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typePost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
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

    @JaxrsMethodDescribe(value = "假期类型对象.", action = ActionLeaveTypeGet.class)
    @GET
    @Path("type/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typeGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("类型ID") @PathParam("id") String id) {
        ActionResult<ActionLeaveTypeGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveTypeGet().execute(id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "删除假期类型对象.", action = ActionLeaveTypeDelete.class)
    @GET
    @Path("type/delete/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typeDelete(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("类型ID") @PathParam("id") String id) {
        ActionResult<ActionLeaveTypeDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveTypeDelete().execute(id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "假期类型列表.", action = ActionLeaveTypeList.class)
    @GET
    @Path("type/list/all")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typeListAll(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request) {
        ActionResult<List<ActionLeaveTypeList.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveTypeList().execute();
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "假期类型列表,是否有限额.", action = ActionLeaveTypeListWithQuotaType.class)
    @GET
    @Path("type/list/quota/{isLimited}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void typeListWithQuota(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("是否有限额") @PathParam("isLimited") Boolean isLimited) {
        ActionResult<List<ActionLeaveTypeListWithQuotaType.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveTypeListWithQuotaType().execute(isLimited);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "保存假期规则数据.", action = ActionLeavePolicyPost.class)
    @POST
    @Path("policy")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void policyPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
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

    @JaxrsMethodDescribe(value = "假期规则对象.", action = ActionLeavePolicyGet.class)
    @GET
    @Path("policy/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void policyGet(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("规则ID") @PathParam("id") String id) {
        ActionResult<ActionLeavePolicyGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeavePolicyGet().execute(id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "假期规则列表.", action = ActionLeavePolicyList.class)
    @GET
    @Path("policy/list/type/{typeId}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void policyListWithTypeId(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("假期类型ID") @PathParam("typeId") String typeId) {
        ActionResult<List<ActionLeavePolicyList.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeavePolicyList().execute(typeId);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "删除假期规则对象.", action = ActionLeavePolicyDelete.class)
    @GET
    @Path("policy/delete/{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void policyDelete(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("规则ID") @PathParam("id") String id) {
        ActionResult<ActionLeavePolicyDelete.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeavePolicyDelete().execute(id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "查询假期余额账户.", action = ActionLeaveAccountSearch.class)
    @POST
    @Path("account/search")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void accountSearch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<ActionLeaveAccountSearch.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveAccountSearch().execute(jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "查询假期流水.", action = ActionLeaveTransactionSearch.class)
    @POST
    @Path("transaction/search")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void transactionSearch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<ActionLeaveTransactionSearch.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveTransactionSearch().execute(jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "查询假期发放批次.", action = ActionLeaveLedgerSearch.class)
    @POST
    @Path("ledger/search")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void ledgerSearch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<ActionLeaveLedgerSearch.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveLedgerSearch().execute(jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "新增假期发放批次.", action = ActionLeaveLedgerPost.class)
    @POST
    @Path("ledger")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void ledgerPost(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<ActionLeaveLedgerPost.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveLedgerPost().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据年份查询中国节假日数据.", action = ActionHolidayListWithYear.class)
    @GET
    @Path("holiday/year/{year}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void holidayListWithYear(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("年份") @PathParam("year") Integer year) {
        ActionResult<ActionHolidayListWithYear.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHolidayListWithYear().execute(year);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据日期判断中国节假日数据.", action = ActionHolidayGetWithDate.class)
    @GET
    @Path("holiday/date/{date}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void holidayGetWithDate(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("日期，格式 yyyy-MM-dd") @PathParam("date") String date) {
        ActionResult<ActionHolidayGetWithDate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionHolidayGetWithDate().execute(date);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "查询假期申请.", action = ActionLeaveRequestSearch.class)
    @POST
    @Path("request/search/page/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void requestSearch(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
            @JaxrsParameterDescribe("数量") @PathParam("size") Integer size,
            JsonElement jsonElement) {
        ActionResult<List<ActionLeaveRequestSearch.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveRequestSearch().execute(page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "假期申请.", action = ActionLeaveRequestApply.class)
    @POST
    @Path("request/apply")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void requestApply(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request, JsonElement jsonElement) {
        ActionResult<ActionLeaveRequestApply.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionLeaveRequestApply().execute(jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
