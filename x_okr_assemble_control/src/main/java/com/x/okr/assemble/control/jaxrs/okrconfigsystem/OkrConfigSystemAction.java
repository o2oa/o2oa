package com.x.okr.assemble.control.jaxrs.okrconfigsystem;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrConfigSystem;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;


@Path( "okrconfigsystem" )
public class OkrConfigSystemAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrConfigSystemAction.class );
	private BeanCopyTools<OkrConfigSystem, WrapOutOkrConfigSystem> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigSystem.class, WrapOutOkrConfigSystem.class, null, WrapOutOkrConfigSystem.Excludes);
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	
	private Ehcache cache = ApplicationCache.instance().getCache( OkrConfigSystem.class );
	private String catchNamePrefix = this.getClass().getName();
	
	@HttpMethodDescribe(value = "新建或者更新OkrConfigSystem对象.", request = WrapInOkrConfigSystem.class, response = WrapOutOkrConfigSystem.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrConfigSystem wrapIn) {
		ActionResult<WrapOutOkrConfigSystem> result = new ActionResult<>();
		OkrConfigSystem okrConfigSystem = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn != null && check ){
			try {
				okrConfigSystem = okrConfigSystemService.save( wrapIn );
				if( okrConfigSystem != null ){
					ApplicationCache.notify( OkrConfigSystem.class );
					okrWorkDynamicsService.configSystemDynamic(
							okrConfigSystem.getConfigName(), 
							okrConfigSystem.getConfigCode(), 
							"修改系统配置", 
							currentPerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"修改系统配置：" + okrConfigSystem.getConfigCode() + "值为" + okrConfigSystem.getConfigValue(), 
							"系统配置修改保存成功！"
					);
					result.setUserMessage( okrConfigSystem.getId() );
				}else{
					result.error( new Exception( "系统在保存系统配置信息时发生异常!" ) );
					result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				logger.error( "OkrConfigSystemService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存系统配置!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存系统配置!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrConfigSystem数据对象.", response = WrapOutOkrConfigSystem.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutOkrConfigSystem> result = new ActionResult<>();
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		if( check ){
			OkrConfigSystem okrConfigSystem = null;		
			try{
				okrConfigSystem = okrConfigSystemService.get( id );
				if( okrConfigSystem != null ){
					
					okrConfigSystemService.delete( id );
					
					ApplicationCache.notify( OkrConfigSystem.class );
					
					okrWorkDynamicsService.configSystemDynamic(
							okrConfigSystem.getConfigName(), 
							okrConfigSystem.getConfigCode(), 
							"删除系统配置", 
							currentPerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"删除系统配置：" + okrConfigSystem.getConfigCode(), 
							"系统配置删除成功！"
					);
				}			
				result.setUserMessage( id );
			}catch(Exception e){
				logger.error( "system delete okrConfigSystemService get an exception, {'id':'"+id+"'}", e );
				result.setUserMessage( "删除配置数据过程中发生异常。" );
				result.error( e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrConfigSystem对象.", response = WrapOutOkrConfigSystem.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrConfigSystem> result = new ActionResult<>();
		WrapOutOkrConfigSystem wrap = null;
		OkrConfigSystem okrConfigSystem = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		
		String cacheKey = catchNamePrefix + "." + id;
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>System get okrConfigSystem from cache. cacheKey:"+cacheKey );
			wrap = (WrapOutOkrConfigSystem) element.getObjectValue();
			result.setData( wrap );
		}else{
			try {
				okrConfigSystem = okrConfigSystemService.get( id );
				if( okrConfigSystem != null ){
					wrap = wrapout_copier.copy( okrConfigSystem );
					
					cache.put( new Element( cacheKey, wrap ) );
					
					result.setData(wrap);
				}else{
					logger.error( "system can not get any object by {'id':'"+id+"'}. " );
				}
			} catch (Throwable th) {
				logger.error( "system get by id got an exception" );
				th.printStackTrace();
				result.error(th);
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取OkrConfigSystem列表.", response = WrapOutOkrConfigSystem.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutOkrConfigSystem>> result = new ActionResult<List<WrapOutOkrConfigSystem>>();
		List<WrapOutOkrConfigSystem> wraps = null;
		List<OkrConfigSystem> okrConfigSystemList = null;
		
		String cacheKey = catchNamePrefix + ".all";
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wraps = (List<WrapOutOkrConfigSystem>) element.getObjectValue();
			result.setData( wraps );
		}else{
			try {
				okrConfigSystemList = okrConfigSystemService.listAll();
				if( okrConfigSystemList != null ){
					wraps = wrapout_copier.copy( okrConfigSystemList );
					SortTools.asc( wraps, true, "orderNumber");
					
					cache.put( new Element( cacheKey, wraps ) );
					
					result.setData( wraps );
				}
			} catch (Throwable th) {
				logger.error( "system get by id got an exception" );
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@Path( "code" )
	@HttpMethodDescribe( value = "根据CODE获取OkrConfigSystem对象.", request = WrapInOkrConfigSystem.class, response = WrapOutOkrConfigSystem.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response getByCode( @Context HttpServletRequest request, WrapInOkrConfigSystem wrapIn ) {
		ActionResult<WrapOutOkrConfigSystem> result = new ActionResult<WrapOutOkrConfigSystem>();
		WrapOutOkrConfigSystem wrap = null;
		OkrConfigSystem okrConfigSystem = null;
		if( wrapIn == null ){
			logger.error( "wrapIn is null, system can not get any object." );
		}
		if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
			logger.error( "config code is null, system can not get any object." );
		}
		
		String cacheKey = catchNamePrefix + "." + wrapIn.getConfigCode();
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>System get okrConfigSystem from cache. cacheKey:"+cacheKey );
			wrap = ( WrapOutOkrConfigSystem ) element.getObjectValue();
			result.setData( wrap );
		}else{
			try {
				okrConfigSystem = okrConfigSystemService.getWithConfigCode( wrapIn.getConfigCode() );
				if( okrConfigSystem != null ){
					wrap = wrapout_copier.copy( okrConfigSystem );
					
					cache.put( new Element( cacheKey, wrap ) );
					
					result.setData(wrap);
				}else{
					logger.error( "system can not get any object by {'configCode':'"+wrapIn.getConfigCode()+"'}. " );
				}
			} catch (Throwable th) {
				logger.error( "system get by id got an exception" );
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
