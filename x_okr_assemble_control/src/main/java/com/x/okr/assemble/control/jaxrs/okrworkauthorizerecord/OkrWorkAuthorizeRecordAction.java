package com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeRecordService;
import com.x.okr.entity.OkrWorkAuthorizeRecord;


@Path( "okrworkauthorizerecord" )
public class OkrWorkAuthorizeRecordAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkAuthorizeRecordAction.class );
	private BeanCopyTools<OkrWorkAuthorizeRecord, WrapOutOkrWorkAuthorizeRecord> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkAuthorizeRecord.class, WrapOutOkrWorkAuthorizeRecord.class, null, WrapOutOkrWorkAuthorizeRecord.Excludes);
	private OkrWorkAuthorizeRecordService okrWorkAuthorizeRecordService = new OkrWorkAuthorizeRecordService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkAuthorizeRecord对象.", request = WrapInOkrWorkAuthorizeRecord.class, response = WrapOutOkrWorkAuthorizeRecord.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrWorkAuthorizeRecord wrapIn) {
		ActionResult<WrapOutOkrWorkAuthorizeRecord> result = new ActionResult<>();
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to save OkrWorkAuthorizeRecord......" );
		if( wrapIn != null ){
			try {
				okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.save( wrapIn );
				if( okrWorkAuthorizeRecord != null ){
					result.setUserMessage( okrWorkAuthorizeRecord.getId() );
				}else{
					result.error( new Exception( "系统在保存信息时发生异常!" ) );
					result.setUserMessage( "系统在保存信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrWorkAuthorizeRecordService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkAuthorizeRecord数据对象.", response = WrapOutOkrWorkAuthorizeRecord.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkAuthorizeRecord> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to delete okrWorkAuthorizeRecord{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrWorkAuthorizeRecordService.delete( id );
			result.setUserMessage( "成功删除授权记录数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrWorkAuthorizeRecordService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除授权记录数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkAuthorizeRecord对象.", response = WrapOutOkrWorkAuthorizeRecord.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkAuthorizeRecord> result = new ActionResult<>();
		WrapOutOkrWorkAuthorizeRecord wrap = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrWorkAuthorizeRecord{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.get( id );
			if( okrWorkAuthorizeRecord != null ){
				wrap = wrapout_copier.copy( okrWorkAuthorizeRecord );
				result.setData(wrap);
			}else{
				logger.error( "system can not get any object by {'id':'"+id+"'}. " );
			}
		} catch (Throwable th) {
			logger.error( "system get by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
