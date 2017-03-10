package com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog;
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
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;
import com.x.okr.entity.OkrWorkReportProcessLog;


@Path( "okrworkreportprocesslog" )
public class OkrWorkReportProcessLogAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkReportProcessLogAction.class );
	private BeanCopyTools<OkrWorkReportProcessLog, WrapOutOkrWorkReportProcessLog> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportProcessLog.class, WrapOutOkrWorkReportProcessLog.class, null, WrapOutOkrWorkReportProcessLog.Excludes);
	private OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkReportProcessLog对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInOkrWorkReportProcessLog wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkReportProcessLog.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			try {
				okrWorkReportProcessLog = okrWorkReportProcessLogService.save( wrapIn );
				result.setData( new WrapOutId( okrWorkReportProcessLog.getId() ) );
			} catch (Exception e) {
				Exception exception = new ReportProcessLogSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkReportProcessLog数据对象.", response = WrapOutId.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		
		if( id == null || id.isEmpty() ){
			Exception exception = new ReportProcessLogIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try{
				okrWorkReportProcessLogService.delete( id );
				result.setData( new WrapOutId( id ) );
			}catch(Exception e){
				Exception exception = new ReportProcessLogDeleteException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkReportProcessLog对象.", response = WrapOutOkrWorkReportProcessLog.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkReportProcessLog> result = new ActionResult<>();
		WrapOutOkrWorkReportProcessLog wrap = null;
		OkrWorkReportProcessLog okrWorkReportProcessLog = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		
		if( id == null || id.isEmpty() ){
			Exception exception = new ReportProcessLogIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try {
				okrWorkReportProcessLog = okrWorkReportProcessLogService.get( id );
				if( okrWorkReportProcessLog != null ){
					wrap = wrapout_copier.copy( okrWorkReportProcessLog );
					result.setData(wrap);
				}else{
					Exception exception = new ReportProcessLogNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Throwable th) {
				Exception exception = new ReportProcessLogQueryByIdException( th, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
