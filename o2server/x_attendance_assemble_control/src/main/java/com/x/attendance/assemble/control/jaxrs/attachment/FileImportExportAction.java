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
@JaxrsDescribe("附件操作")
public class FileImportExportAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(FileImportExportAction.class);

	@JaxrsMethodDescribe(value = "上传需要导入的数据文件XLS", action = StandardJaxrsAction.class)
	@POST
	@Path("upload")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void upload(@Suspended final AsyncResponse asyncResponse, 
			@Context HttpServletRequest request, 
			@FormDataParam(FILE_FIELD) final byte[] bytes,
			@FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition
	) {
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
}
