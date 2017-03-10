package com.x.okr.assemble.control.jaxrs.okrworkreportdetailinfo;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.entity.OkrWorkReportDetailInfo;


@Path( "okrworkreportdetailinfo" )
public class OkrWorkReportDetailInfoAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkReportDetailInfoAction.class );
	private BeanCopyTools<OkrWorkReportDetailInfo, WrapOutOkrWorkReportDetailInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportDetailInfo.class, WrapOutOkrWorkReportDetailInfo.class, null, WrapOutOkrWorkReportDetailInfo.Excludes);
	private OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkReportDetailInfo对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInOkrWorkReportDetailInfo wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkReportDetailInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			try {
				okrWorkReportDetailInfo = okrWorkReportDetailInfoService.save( wrapIn );
				result.setData( new WrapOutId( okrWorkReportDetailInfo.getId() ) );
			} catch (Exception e) {
				Exception exception = new ReportDetailSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);	
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkReportDetailInfo数据对象.", response = WrapOutId.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if( id == null || id.isEmpty() ){
			Exception exception = new ReportDetailIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try{
				okrWorkReportDetailInfoService.delete( id );
				result.setData( new WrapOutId( id ));
			}catch(Exception e){
				Exception exception = new ReportDetailDeleteException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkReportDetailInfo对象.", response = WrapOutOkrWorkReportDetailInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkReportDetailInfo> result = new ActionResult<>();
		WrapOutOkrWorkReportDetailInfo wrap = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if( id == null || id.isEmpty() ){
			Exception exception = new ReportDetailIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try {
				okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( id );
				if( okrWorkReportDetailInfo != null ){
					wrap = wrapout_copier.copy( okrWorkReportDetailInfo );
					result.setData(wrap);
				}else{
					Exception exception = new ReportDetailNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Throwable th) {
				Exception exception = new ReportDetailQueryByIdException( th, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
