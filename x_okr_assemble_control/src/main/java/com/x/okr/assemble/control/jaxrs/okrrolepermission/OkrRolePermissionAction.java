package com.x.okr.assemble.control.jaxrs.okrrolepermission;
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
import com.x.okr.assemble.control.service.OkrRolePermissionService;
import com.x.okr.entity.OkrRolePermission;


@Path( "okrrolepermission" )
public class OkrRolePermissionAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrRolePermissionAction.class );
	private BeanCopyTools<OkrRolePermission, WrapOutOkrRolePermission> wrapout_copier = BeanCopyToolsBuilder.create( OkrRolePermission.class, WrapOutOkrRolePermission.class, null, WrapOutOkrRolePermission.Excludes);
	private OkrRolePermissionService okrRolePermissionService = new OkrRolePermissionService();

	@HttpMethodDescribe(value = "新建或者更新OkrRolePermission对象.", request = WrapInOkrRolePermission.class, response = WrapOutOkrRolePermission.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrRolePermission wrapIn) {
		ActionResult<WrapOutOkrRolePermission> result = new ActionResult<>();
		OkrRolePermission okrRolePermission = null;
		if( wrapIn != null ){
			try {
				okrRolePermission = okrRolePermissionService.save( wrapIn );
				if( okrRolePermission != null ){
					result.setUserMessage( okrRolePermission.getId() );
				}else{
					result.error( new Exception( "系统在保存系统配置信息时发生异常!" ) );
					result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				logger.error( "OkrRolePermissionService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存系统配置!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存系统配置!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrRolePermission数据对象.", response = WrapOutOkrRolePermission.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrRolePermission> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrRolePermissionService.delete( id );
			result.setUserMessage( id );
		}catch(Exception e){
			logger.error( "system delete okrRolePermissionService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除配置数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrRolePermission对象.", response = WrapOutOkrRolePermission.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrRolePermission> result = new ActionResult<>();
		WrapOutOkrRolePermission wrap = null;
		OkrRolePermission okrRolePermission = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrRolePermission = okrRolePermissionService.get( id );
			if( okrRolePermission != null ){
				wrap = wrapout_copier.copy( okrRolePermission );
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
	
	@HttpMethodDescribe(value = "获取OkrRolePermission列表.", response = WrapOutOkrRolePermission.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutOkrRolePermission>> result = new ActionResult<List<WrapOutOkrRolePermission>>();
		List<WrapOutOkrRolePermission> wraps = null;
		List<OkrRolePermission> okrRolePermissionList = null;
		try {
			okrRolePermissionList = okrRolePermissionService.listAll();
			if( okrRolePermissionList != null ){
				wraps = wrapout_copier.copy( okrRolePermissionList );
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
