package com.x.okr.assemble.control.jaxrs.okrpersonpermission;
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
import com.x.okr.assemble.control.service.OkrPersonPermissionService;
import com.x.okr.entity.OkrPersonPermission;


@Path( "okrpersonpermission" )
public class OkrPersonPermissionAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrPersonPermissionAction.class );
	private BeanCopyTools<OkrPersonPermission, WrapOutOkrPersonPermission> wrapout_copier = BeanCopyToolsBuilder.create( OkrPersonPermission.class, WrapOutOkrPersonPermission.class, null, WrapOutOkrPersonPermission.Excludes);
	private OkrPersonPermissionService okrPersonPermissionService = new OkrPersonPermissionService();

	@HttpMethodDescribe(value = "新建或者更新OkrPersonPermission对象.", request = WrapInOkrPersonPermission.class, response = WrapOutOkrPersonPermission.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrPersonPermission wrapIn) {
		ActionResult<WrapOutOkrPersonPermission> result = new ActionResult<>();
		OkrPersonPermission okrPersonPermission = null;
		if( wrapIn != null ){
			try {
				okrPersonPermission = okrPersonPermissionService.save( wrapIn );
				if( okrPersonPermission != null ){
					result.setUserMessage( okrPersonPermission.getId() );
				}else{
					result.error( new Exception( "系统在保存系统配置信息时发生异常!" ) );
					result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				logger.error( "OkrPersonPermissionService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存系统配置!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存系统配置!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrPersonPermission数据对象.", response = WrapOutOkrPersonPermission.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrPersonPermission> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrPersonPermissionService.delete( id );
			result.setUserMessage( id );
		}catch(Exception e){
			logger.error( "system delete okrPersonPermissionService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除配置数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrPersonPermission对象.", response = WrapOutOkrPersonPermission.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrPersonPermission> result = new ActionResult<>();
		WrapOutOkrPersonPermission wrap = null;
		OkrPersonPermission okrPersonPermission = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrPersonPermission = okrPersonPermissionService.get( id );
			if( okrPersonPermission != null ){
				wrap = wrapout_copier.copy( okrPersonPermission );
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
	
	@HttpMethodDescribe(value = "获取OkrPersonPermission列表.", response = WrapOutOkrPersonPermission.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutOkrPersonPermission>> result = new ActionResult<List<WrapOutOkrPersonPermission>>();
		List<WrapOutOkrPersonPermission> wraps = null;
		List<OkrPersonPermission> okrPersonPermissionList = null;
		try {
			okrPersonPermissionList = okrPersonPermissionService.listAll();
			if( okrPersonPermissionList != null ){
				wraps = wrapout_copier.copy( okrPersonPermissionList );
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
