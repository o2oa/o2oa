package com.x.okr.assemble.control.jaxrs.okrroleinfo;
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
import com.x.okr.assemble.control.service.OkrRoleInfoService;
import com.x.okr.entity.OkrRoleInfo;


@Path( "okrroleinfo" )
public class OkrRoleInfoAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrRoleInfoAction.class );
	private BeanCopyTools<OkrRoleInfo, WrapOutOkrRoleInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrRoleInfo.class, WrapOutOkrRoleInfo.class, null, WrapOutOkrRoleInfo.Excludes);
	private OkrRoleInfoService okrRoleInfoService = new OkrRoleInfoService();

	@HttpMethodDescribe(value = "新建或者更新OkrRoleInfo对象.", request = WrapInOkrRoleInfo.class, response = WrapOutOkrRoleInfo.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrRoleInfo wrapIn) {
		ActionResult<WrapOutOkrRoleInfo> result = new ActionResult<>();
		OkrRoleInfo okrRoleInfo = null;
		if( wrapIn != null ){
			try {
				okrRoleInfo = okrRoleInfoService.save( wrapIn );
				if( okrRoleInfo != null ){
					result.setUserMessage( okrRoleInfo.getId() );
				}else{
					result.error( new Exception( "系统在保存系统配置信息时发生异常!" ) );
					result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				logger.error( "OkrRoleInfoService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存系统配置!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存系统配置!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrRoleInfo数据对象.", response = WrapOutOkrRoleInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrRoleInfo> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrRoleInfoService.delete( id );
			result.setUserMessage( id );
		}catch(Exception e){
			logger.error( "system delete okrRoleInfoService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除配置数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrRoleInfo对象.", response = WrapOutOkrRoleInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrRoleInfo> result = new ActionResult<>();
		WrapOutOkrRoleInfo wrap = null;
		OkrRoleInfo okrRoleInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrRoleInfo = okrRoleInfoService.get( id );
			if( okrRoleInfo != null ){
				wrap = wrapout_copier.copy( okrRoleInfo );
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
	
	@HttpMethodDescribe(value = "获取OkrRoleInfo列表.", response = WrapOutOkrRoleInfo.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutOkrRoleInfo>> result = new ActionResult<List<WrapOutOkrRoleInfo>>();
		List<WrapOutOkrRoleInfo> wraps = null;
		List<OkrRoleInfo> okrRoleInfoList = null;
		try {
			okrRoleInfoList = okrRoleInfoService.listAll();
			if( okrRoleInfoList != null ){
				wraps = wrapout_copier.copy( okrRoleInfoList );
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
