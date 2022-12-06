package com.x.program.center.jaxrs.warnlog;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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

@JaxrsDescribe("警告")
@Path("warnlog")
public class WarnLogAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(WarnLogAction.class);

    private static ReentrantLock lock = new ReentrantLock();

    @JaxrsMethodDescribe(value = "获取警告.", action = ActionGet.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("id") String id) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<ActionGet.Wo> result = new ActionResult<>();
        try {
            result = new ActionGet().execute(effectivePerson, id);
        } catch (Exception e) {
            e.printStackTrace();
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "记录Warn错误.", action = ActionCreate.class)
    @POST
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void recordWarnLog(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            JsonElement jsonElement) {
        ActionResult<ActionCreate.Wo> result = new ActionResult<>();
        try {
            result = new ActionCreate().execute(jsonElement);
        } catch (Exception e) {
            e.printStackTrace();
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示WarnLog,下一页.", action = ActionListPrev.class)
    @GET
    @Path("list/{id}/next/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listNext(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("返回数量") @PathParam("count") Integer count) {
        ActionResult<List<ActionListNext.Wo>> result = new ActionResult<>();
        try {
            result = new ActionListNext().execute(id, count);
        } catch (Exception e) {
            e.printStackTrace();
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示WarnLog,上一页.", action = ActionListPrev.class)
    @GET
    @Path("list/{id}/prev/{count}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPrev(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("返回数量") @PathParam("count") Integer count) {
        ActionResult<List<ActionListPrev.Wo>> result = new ActionResult<>();
        try {
            result = new ActionListPrev().execute(id, count);
        } catch (Exception e) {
            e.printStackTrace();
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示WarnLog,指定日期,下一页.", action = ActionListNextWithDate.class)
    @GET
    @Path("list/{id}/next/{count}/date/{date}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listNextWithDate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("返回数量") @PathParam("count") Integer count,
            @JaxrsParameterDescribe("指定日期") @PathParam("date") String date) {
        ActionResult<List<ActionListNextWithDate.Wo>> result = new ActionResult<>();
        try {
            result = new ActionListNextWithDate().execute(id, count, date);
        } catch (Exception e) {
            logger.error(e);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "列示WarnLog,指定日期,上一页.", action = ActionListPrevWithDate.class)
    @GET
    @Path("list/{id}/prev/{count}/date/{date}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listPrevWithDate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("返回数量") @PathParam("count") Integer count,
            @JaxrsParameterDescribe("指定日期") @PathParam("date") String date) {
        ActionResult<List<ActionListPrevWithDate.Wo>> result = new ActionResult<>();
        try {
            result = new ActionListPrevWithDate().execute(id, count, date);
        } catch (Exception e) {
            logger.error(e);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "获取系统日志.", action = ActionGetSystemLog.class)
    @GET
    @Path("view/system/log/tag/{tag}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void getSystemLog(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("日志标识") @PathParam("tag") String tag) {
        EffectivePerson effectivePerson = this.effectivePerson(request);
        ActionResult<List<ActionGetSystemLog.Wo>> result = new ActionResult<>();
        if (lock.tryLock()) {
            try {
                result = new ActionGetSystemLog().execute(effectivePerson, tag);
            } catch (Exception e) {
                e.printStackTrace();
                result.error(e);
            } finally {
                lock.unlock();
            }
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

}
