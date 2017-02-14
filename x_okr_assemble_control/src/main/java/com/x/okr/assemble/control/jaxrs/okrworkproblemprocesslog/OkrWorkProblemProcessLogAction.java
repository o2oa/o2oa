package com.x.okr.assemble.control.jaxrs.okrworkproblemprocesslog;
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
import com.x.okr.assemble.control.service.OkrWorkProblemProcessLogService;
import com.x.okr.entity.OkrWorkProblemProcessLog;


@Path( "okrworkproblemprocesslog" )
public class OkrWorkProblemProcessLogAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkProblemProcessLogAction.class );
	private BeanCopyTools<OkrWorkProblemProcessLog, WrapOutOkrWorkProblemProcessLog> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkProblemProcessLog.class, WrapOutOkrWorkProblemProcessLog.class, null, WrapOutOkrWorkProblemProcessLog.Excludes);
	private OkrWorkProblemProcessLogService okrWorkProblemProcessLogService = new OkrWorkProblemProcessLogService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkProblemProcessLog对象.", request = WrapInOkrWorkProblemProcessLog.class, response = WrapOutOkrWorkProblemProcessLog.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrWorkProblemProcessLog wrapIn) {
		ActionResult<WrapOutOkrWorkProblemProcessLog> result = new ActionResult<>();
		OkrWorkProblemProcessLog okrWorkProblemProcessLog = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to save OkrWorkProblemProcessLog......" );
		if( wrapIn != null ){
			try {
				okrWorkProblemProcessLog = okrWorkProblemProcessLogService.save( wrapIn );
				if( okrWorkProblemProcessLog != null ){
					result.setUserMessage( okrWorkProblemProcessLog.getId() );
				}else{
					result.error( new Exception( "系统在保存信息时发生异常!" ) );
					result.setUserMessage( "系统在保存信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrWorkProblemProcessLogService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkProblemProcessLog数据对象.", response = WrapOutOkrWorkProblemProcessLog.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkProblemProcessLog> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to delete okrWorkProblemProcessLog{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrWorkProblemProcessLogService.delete( id );
			result.setUserMessage( "成功删除问题请示处理记录数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrWorkProblemProcessLogService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除问题请示处理记录数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkProblemProcessLog对象.", response = WrapOutOkrWorkProblemProcessLog.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkProblemProcessLog> result = new ActionResult<>();
		WrapOutOkrWorkProblemProcessLog wrap = null;
		OkrWorkProblemProcessLog okrWorkProblemProcessLog = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrWorkProblemProcessLog{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrWorkProblemProcessLog = okrWorkProblemProcessLogService.get( id );
			if( okrWorkProblemProcessLog != null ){
				wrap = wrapout_copier.copy( okrWorkProblemProcessLog );
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
