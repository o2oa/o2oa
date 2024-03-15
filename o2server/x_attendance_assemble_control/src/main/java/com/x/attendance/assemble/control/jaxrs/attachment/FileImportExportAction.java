package com.x.attendance.assemble.control.jaxrs.attachment;

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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("file")
@JaxrsDescribe("附件操作（已弃用）")
public class FileImportExportAction extends StandardJaxrsAction {
    private static Logger logger = LoggerFactory.getLogger(FileImportExportAction.class);

    @JaxrsMethodDescribe(value = "上传需要导入的数据文件XLS", action = StandardJaxrsAction.class)
    @POST
    @Path("upload")
    @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void upload(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("文件内容") @FormDataParam(FILE_FIELD) final byte[] bytes,
            @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
        ActionResult<WrapOutId> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionImportFileUpload().execute(request, effectivePerson, bytes, disposition);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "根据文件ID下载附件,设定是否使用stream输出", action = ActionImportFileDownload.class)
    @GET
    @Path("download/{id}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void fileDownloadStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("文件标识") @PathParam("id") String id,
            @JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
        ActionResult<ActionImportFileDownload.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionImportFileDownload().execute(request, effectivePerson, id, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "按指定月份导出非正常打卡数据,设定是否使用stream输出", action = ActionExportAbnormalDetail.class)
    @GET
    @Path("export/abnormaldetails/year/{year}/month/{month}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void abnormalDetailsExportStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("年份") @PathParam("year") String year,
            @JaxrsParameterDescribe("月份") @PathParam("month") String month,
            @JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
        ActionResult<ActionExportAbnormalDetail.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionExportAbnormalDetail().execute(request, effectivePerson, year, month, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "按时间区间导出请假数据,设定是否使用stream输出", action = ActionExportHolidayDetail.class)
    @GET
    @Path("export/selfholiday/{startdate}/{enddate}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void selfHolidayExportStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("开始时间") @PathParam("startdate") String startdate,
            @JaxrsParameterDescribe("结束时间") @PathParam("enddate") String enddate,
            @JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
        ActionResult<ActionExportHolidayDetail.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionExportHolidayDetail().execute(request, effectivePerson, startdate, enddate, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "导出符合过滤条件的打卡记录明细", action = ActionExportDetailWithFilter.class)
    @GET
    @Path("export/filter/{q_topUnitName}/{q_unitName}/{q_empName}/{cycleYear}/{cycleMonth}/{q_date}/{isAbsent}/{isLackOfTime}/{isLate}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void detailsExportStream(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
            @JaxrsParameterDescribe("公司,为空时输入0") @PathParam("q_topUnitName") String q_topUnitName,
            @JaxrsParameterDescribe("部门,为空时输入0") @PathParam("q_unitName") String q_unitName,
            @JaxrsParameterDescribe("员工,为空时输入0") @PathParam("q_empName") String q_empName,
            @JaxrsParameterDescribe("统计周期年份,为空时输入0") @PathParam("cycleYear") String cycleYear,
            @JaxrsParameterDescribe("统计周期月份,为空时输入0") @PathParam("cycleMonth") String cycleMonth,
            @JaxrsParameterDescribe("统计具体日期,为空时输入0") @PathParam("q_date") String q_date,
            @JaxrsParameterDescribe("是否缺勤,为空时输入0") @PathParam("isAbsent") String isAbsent,
            @JaxrsParameterDescribe("是否工时不足,为空时输入0") @PathParam("isLackOfTime") String isLackOfTime,
            @JaxrsParameterDescribe("是否迟到,为空时输入0") @PathParam("isLate") String isLate,
            @JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
        ActionResult<ActionExportDetailWithFilter.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionExportDetailWithFilter().execute(request, effectivePerson, q_topUnitName, q_unitName,
                    q_empName, cycleYear, cycleMonth, q_date, isAbsent, isLackOfTime, isLate, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "导出原始打卡记录", action = ActionExportDetailSource.class)
    @GET
    @Path("export/source/{cycleYear}/{cycleMonth}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void detailsSourceExportStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("统计周期年份,为空时输入0") @PathParam("cycleYear") String cycleYear,
            @JaxrsParameterDescribe("统计周期月份,为空时输入0") @PathParam("cycleMonth") String cycleMonth,
            @JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
        ActionResult<ActionExportDetailSource.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionExportDetailSource().execute(request, effectivePerson, cycleYear, cycleMonth, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "导出个人出勤率统计记录,设定是否使用stream输出", action = ActionExportPersonStatistic.class)
    @GET
    @Path("export/person/{name}/{year}/{month}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void personStatisticExportStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("统计员工姓名") @PathParam("name") String name,
            @JaxrsParameterDescribe("统计周期年份") @PathParam("year") String year,
            @JaxrsParameterDescribe("统计周期月份") @PathParam("month") String month,
            @JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
        ActionResult<ActionExportPersonStatistic.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionExportPersonStatistic().execute(request, effectivePerson, name, year, month, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "导出部门出勤率统计记录,设定是否使用stream输出", action = ActionExportUnitSubNestedStatistic.class)
    @GET
    @Path("export/unit/{name}/{year}/{month}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void unitStatisticExportStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("统计部门名称") @PathParam("name") String name,
            @JaxrsParameterDescribe("统计周期年份") @PathParam("year") String year,
            @JaxrsParameterDescribe("统计周期月份") @PathParam("month") String month,
            @JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
        ActionResult<ActionExportUnitSubNestedStatistic.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionExportUnitSubNestedStatistic().execute(request, effectivePerson, name, year, month,
                    stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }

    @JaxrsMethodDescribe(value = "导出公司出勤率统计记录,设定是否使用stream输出", action = ActionExportTopUnitStatistic.class)
    @GET
    @Path("export/topunit/{name}/{year}/{month}/stream/{stream}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void topunitStatisticExportStream(@Suspended final AsyncResponse asyncResponse,
            @Context HttpServletRequest request,
            @JaxrsParameterDescribe("统计公司名称") @PathParam("name") String name,
            @JaxrsParameterDescribe("统计周期年份") @PathParam("year") String year,
            @JaxrsParameterDescribe("统计周期月份") @PathParam("month") String month,
            @JaxrsParameterDescribe("用.APPLICATION_OCTET_STREAM头输出") @PathParam("stream") Boolean stream) {
        ActionResult<ActionExportTopUnitStatistic.Wo> result = new ActionResult<>();
        EffectivePerson effectivePerson = this.effectivePerson(request);
        try {
            result = new ActionExportTopUnitStatistic().execute(request, effectivePerson, name, year, month, stream);
        } catch (Exception e) {
            logger.error(e, effectivePerson, request, null);
            result.error(e);
        }
        asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
    }
}
