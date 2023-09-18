package com.x.attendance.assemble.control.jaxrs.v2.record;

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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by fancyLou on 2023/4/17.
 * Copyright © 2023 O2. All rights reserved.
 */


@Path("v2/record")
@JaxrsDescribe("打卡记录")
public class RecordAction extends StandardJaxrsAction {

    private static Logger logger = LoggerFactory.getLogger(RecordAction.class);





    @JaxrsMethodDescribe(value = "分页查询班次列表.", action = ActionListByPage.class)
    @POST
    @Path("list/{page}/size/{size}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void listByPaging(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                             @JaxrsParameterDescribe("分页") @PathParam("page") Integer page,
                             @JaxrsParameterDescribe("数量") @PathParam("size") Integer size, JsonElement jsonElement) {
        ActionResult<List<ActionListByPage.Wo>> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionListByPage().execute(effectivePerson, page, size, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "获取打卡记录对象.", action = ActionGet.class)
    @GET
    @Path("{id}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                    @JaxrsParameterDescribe("打卡记录ID") @PathParam("id") String id) {
        ActionResult<ActionGet.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionGet().execute(id);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据人员和日期删除打卡记录.", action = ActionDeleteByDateAndPeople.class)
    @GET
    @Path("delete/people/{people}/date/{date}")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteByDate(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                    @JaxrsParameterDescribe("人员 DN") @PathParam("people") String people,
                       @JaxrsParameterDescribe("日期 yyyy-MM-dd") @PathParam("date") String date) {
        ActionResult<ActionDeleteByDateAndPeople.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionDeleteByDateAndPeople().execute( effectivePerson, people, date);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
 


    @JaxrsMethodDescribe(value = "获取导入打卡记录数据的模版.", action = ActionExcelTemplate.class)
    @GET
    @Path("template")
    @Consumes(MediaType.APPLICATION_JSON)
    public void template(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
        ActionResult<ActionExcelTemplate.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionExcelTemplate().execute(effectivePerson);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }


    @JaxrsMethodDescribe(value = "上传Excel导入打卡记录数据.", action = ActionImportExcel.class)
    @POST
    @Path("import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    public void input(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
                      @FormDataParam(FILE_FIELD) final byte[] bytes,
                      @JaxrsParameterDescribe("Excel文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult< ActionImportExcel.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionImportExcel().execute(effectivePerson, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }




    @JaxrsMethodDescribe(value = "导入某一天的打卡记录.", action = ActionPostDailyRecord.class)
    @POST
    @Path("import/daily")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.APPLICATION_JSON)
    public void inputDailyRecord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,  JsonElement jsonElement) {
        ActionResult<ActionPostDailyRecord.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionPostDailyRecord().execute(effectivePerson, jsonElement);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, jsonElement);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
