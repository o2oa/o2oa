package com.x.okr.assemble.control.jaxrs.okrconfigworklevel;
import java.util.List;

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
import com.x.okr.assemble.control.service.OkrConfigWorkLevelService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrConfigWorkLevel;


@Path( "okrconfigworklevel" )
public class OkrConfigWorkLevelAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrConfigWorkLevelAction.class );
	private BeanCopyTools<OkrConfigWorkLevel, WrapOutOkrConfigWorkLevel> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigWorkLevel.class, WrapOutOkrConfigWorkLevel.class, null, WrapOutOkrConfigWorkLevel.Excludes);
	private OkrConfigWorkLevelService okrConfigWorkLevelService = new OkrConfigWorkLevelService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	
	@HttpMethodDescribe(value = "新建或者更新OkrConfigWorkLevel对象.", request = WrapInOkrConfigWorkLevel.class, response = WrapOutOkrConfigWorkLevel.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrConfigWorkLevel wrapIn) {
		ActionResult<WrapOutOkrConfigWorkLevel> result = new ActionResult<>();
		OkrConfigWorkLevel okrConfigWorkLevel = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to save OkrConfigWorkLevel......" );
		if( wrapIn != null ){
			try {
				okrConfigWorkLevel = okrConfigWorkLevelService.save( wrapIn );
				if( okrConfigWorkLevel != null ){
					result.setUserMessage( okrConfigWorkLevel.getId() );
				}else{
					result.error( new Exception( "系统在保存信息时发生异常!" ) );
					result.setUserMessage( "系统在保存信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrConfigWorkLevelService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrConfigWorkLevel数据对象.", response = WrapOutOkrConfigWorkLevel.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrConfigWorkLevel> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to delete okrConfigWorkLevel{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrConfigWorkLevelService.delete( id );
			result.setUserMessage( "成功删除工作等级数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrConfigWorkLevelService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除工作等级数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrConfigWorkLevel对象.", response = WrapOutOkrConfigWorkLevel.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrConfigWorkLevel> result = new ActionResult<>();
		WrapOutOkrConfigWorkLevel wrap = null;
		OkrConfigWorkLevel okrConfigWorkLevel = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrConfigWorkLevel{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrConfigWorkLevel = okrConfigWorkLevelService.get( id );
			if( okrConfigWorkLevel != null ){
				wrap = wrapout_copier.copy( okrConfigWorkLevel );
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
	
	@HttpMethodDescribe(value = "获取OkrConfigWorkLevel对象.", response = WrapOutOkrConfigWorkLevel.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response all(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutOkrConfigWorkLevel>> result = new ActionResult<>();
		List<WrapOutOkrConfigWorkLevel> wraps = null;
		List<OkrConfigWorkLevel> okrConfigWorkLevelList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrConfigWorkLevel{'id':'"+id+"'}......" );
		try {
			okrConfigWorkLevelList = okrConfigWorkLevelService.listAll();
			if( okrConfigWorkLevelList != null ){
				wraps = wrapout_copier.copy( okrConfigWorkLevelList );
				result.setData(wraps);
			}
		} catch (Throwable th) {
			logger.error( "system get by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
