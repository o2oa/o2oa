package com.x.general.assemble.control.jaxrs.excel;

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

@Path("excel")
@JaxrsDescribe("生成excel")
public class ExcelAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ExcelAction.class);

	@JaxrsMethodDescribe(value = "将内容生成Excel", action = ActionExcelExport.class)
	@POST
	@Path("excelName/{excelName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void excelWithDataList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
											@JaxrsParameterDescribe("文件名") @PathParam("excelName") String excelName, JsonElement jsonElement) {
		ActionResult<ActionExcelExport.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExcelExport().execute(effectivePerson, excelName, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "将内容生成Excel，支持多个sheet页", action = ActionExcelSheetExport.class)
	@POST
	@Path("excelName/{excelName}/sheetList")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void excelSheetWithDataList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								  @JaxrsParameterDescribe("文件名") @PathParam("excelName") String excelName, JsonElement jsonElement) {
		ActionResult<ActionExcelSheetExport.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExcelSheetExport().execute(effectivePerson, excelName, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "导出Excel.", action = ActionExcelResult.class)
	@GET
	@Path("result/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void excelResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							@JaxrsParameterDescribe("对象标识") @PathParam("flag") String flag) {
		ActionResult<ActionExcelResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExcelResult().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "解析上传Excel中的内容.", action = ActionUploadExcel.class)
	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void excelToDataList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					  @JaxrsParameterDescribe("读取内容的Sheet页下标，默认0") @FormDataParam("sheetIndex") Integer sheetIndex,
					  @JaxrsParameterDescribe("读取内容的起始行下标，默认0") @FormDataParam("rowIndex") Integer rowIndex,
					  @FormDataParam(FILE_FIELD) final byte[] bytes,
					  @JaxrsParameterDescribe("Excel文件") @FormDataParam(FILE_FIELD) final FormDataContentDisposition disposition) {
		ActionResult< ActionUploadExcel.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadExcel().execute(effectivePerson, sheetIndex,rowIndex,bytes, disposition);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}


	@JaxrsMethodDescribe(value = "解析url中Excel的内容", action = ActionUploadExcelWithUrl.class)
	@POST
	@Path("upload/with/url")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void excelToDataListWithUrl(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
							  JsonElement jsonElement) {
		ActionResult<ActionUploadExcelWithUrl.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUploadExcelWithUrl().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}