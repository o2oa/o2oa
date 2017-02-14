package com.x.okr.assemble.control.jaxrs.okrpermissioninfo;
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
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.service.OkrPermissionInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrPermissionInfo;


@Path( "okrpermissioninfo" )
public class OkrPermissionInfoAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrPermissionInfoAction.class );
	private BeanCopyTools<OkrPermissionInfo, WrapOutOkrPermissionInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrPermissionInfo.class, WrapOutOkrPermissionInfo.class, null, WrapOutOkrPermissionInfo.Excludes);
	private OkrPermissionInfoService okrPermissionInfoService = new OkrPermissionInfoService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	
	@HttpMethodDescribe(value = "新建或者更新OkrPermissionInfo对象.", request = WrapInOkrPermissionInfo.class, response = WrapOutOkrPermissionInfo.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrPermissionInfo wrapIn) {
		ActionResult<WrapOutOkrPermissionInfo> result = new ActionResult<>();
		OkrPermissionInfo okrPermissionInfo = null;
		if( wrapIn != null ){
			try {
				okrPermissionInfo = okrPermissionInfoService.save( wrapIn );
				if( okrPermissionInfo != null ){
					result.setUserMessage( okrPermissionInfo.getId() );
				}else{
					result.error( new Exception( "系统在保存系统配置信息时发生异常!" ) );
					result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				logger.error( "OkrPermissionInfoService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存系统配置!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存系统配置!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrPermissionInfo数据对象.", response = WrapOutOkrPermissionInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrPermissionInfo> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrPermissionInfoService.delete( id );
			result.setUserMessage( id );
		}catch(Exception e){
			logger.error( "system delete okrPermissionInfoService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除配置数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrPermissionInfo对象.", response = WrapOutOkrPermissionInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrPermissionInfo> result = new ActionResult<>();
		WrapOutOkrPermissionInfo wrap = null;
		OkrPermissionInfo okrPermissionInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrPermissionInfo = okrPermissionInfoService.get( id );
			if( okrPermissionInfo != null ){
				wrap = wrapout_copier.copy( okrPermissionInfo );
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
	
	@HttpMethodDescribe(value = "获取OkrPermissionInfo列表.", response = WrapOutOkrPermissionInfo.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutOkrPermissionInfo>> result = new ActionResult<List<WrapOutOkrPermissionInfo>>();
		List<WrapOutOkrPermissionInfo> wraps = null;
		List<OkrPermissionInfo> okrPermissionInfoList = null;
		try {
			okrPermissionInfoList = okrPermissionInfoService.listAll();
			if( okrPermissionInfoList != null ){
				wraps = wrapout_copier.copy( okrPermissionInfoList );
				result.setData( wraps );
			}
		} catch (Throwable th) {
			logger.error( "system get by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
}
