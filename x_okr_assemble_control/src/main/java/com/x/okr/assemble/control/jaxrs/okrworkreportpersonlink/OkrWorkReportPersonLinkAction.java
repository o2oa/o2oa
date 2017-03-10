package com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink;
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
import com.x.okr.assemble.control.service.OkrWorkReportPersonLinkService;
import com.x.okr.entity.OkrWorkReportPersonLink;


@Path( "okrworkreportpersonlink" )
public class OkrWorkReportPersonLinkAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkReportPersonLinkAction.class );
	private BeanCopyTools<OkrWorkReportPersonLink, WrapOutOkrWorkReportPersonLink> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportPersonLink.class, WrapOutOkrWorkReportPersonLink.class, null, WrapOutOkrWorkReportPersonLink.Excludes);
	private OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkReportPersonLink对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkReportPersonLink okrWorkReportPersonLink = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInOkrWorkReportPersonLink wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkReportPersonLink.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			try {
				okrWorkReportPersonLink = okrWorkReportPersonLinkService.save( wrapIn );
				result.setData( new WrapOutId( okrWorkReportPersonLink.getId() ) );
			} catch (Exception e) {
				Exception exception = new ReportPersonLinkSaveException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkReportPersonLink数据对象.", response = WrapOutId.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if( id == null || id.isEmpty() ){
			Exception exception = new ReportPersonLinkIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try{
				okrWorkReportPersonLinkService.delete( id );
				result.setData( new WrapOutId( id ) );
			}catch(Exception e){
				Exception exception = new ReportPersonLinkDeleteException( e, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkReportPersonLink对象.", response = WrapOutOkrWorkReportPersonLink.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkReportPersonLink> result = new ActionResult<>();
		WrapOutOkrWorkReportPersonLink wrap = null;
		OkrWorkReportPersonLink okrWorkReportPersonLink = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		
		if( id == null || id.isEmpty() ){
			Exception exception = new ReportPersonLinkIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
			try {
				okrWorkReportPersonLink = okrWorkReportPersonLinkService.get( id );
				if( okrWorkReportPersonLink != null ){
					wrap = wrapout_copier.copy( okrWorkReportPersonLink );
					result.setData(wrap);
				}else{
					Exception exception = new ReportPersonLinkNotExistsException( id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Throwable th) {
				Exception exception = new ReportPersonLinkQueryByIdException( th, id );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
