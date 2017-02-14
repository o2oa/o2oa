package com.x.bbs.assemble.control.jaxrs.configsetting;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.SortTools;
import com.x.bbs.assemble.control.service.BBSConfigSettingService;
import com.x.bbs.entity.BBSConfigSetting;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;


@Path( "user/setting" )
public class BBSConfigSettingAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( BBSConfigSettingAction.class );
	private BeanCopyTools<BBSConfigSetting, WrapOutBBSConfigSetting> wrapout_copier = BeanCopyToolsBuilder.create( BBSConfigSetting.class, WrapOutBBSConfigSetting.class, null, WrapOutBBSConfigSetting.Excludes);
	private BBSConfigSettingService configSettingService = new BBSConfigSettingService();
	private Ehcache cache = ApplicationCache.instance().getCache( BBSConfigSetting.class );
	private String catchNamePrefix = this.getClass().getName();
	
	@HttpMethodDescribe(value = "更新BBSConfigSetting对象, 配置信息不允许新建和删除操作.", request = WrapInBBSConfigSetting.class, response = WrapOutBBSConfigSetting.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request, WrapInBBSConfigSetting wrapIn) {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<>();
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		Boolean check = true;		
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception( "system can not get any parameter." ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存系统配置!" );
		}
		
		if( check ){
			if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
				check = false;
				result.error( new Exception( "config code can not null." ) );
				result.setUserMessage( "数据校验错误,[配置编码]为空，无法查询系统配置!" );
			}
		}
		
		if( check ){
			if( wrapIn.getConfigValue() == null || wrapIn.getConfigValue().isEmpty() ){
				check = false;
				result.error( new Exception( "config value can not null." ) );
				result.setUserMessage( "数据校验错误,[配置值]为空，无法查询系统配置!" );
			}
		}
		if( check ){
			try {
				configSetting = configSettingService.getWithConfigCode( wrapIn.getConfigCode() );
				if( configSetting == null ){
					check = false;
					result.error( new Exception( "config setting["+ wrapIn.getConfigCode() +"] not exists." ) );
					result.setUserMessage( "系统设置["+ wrapIn.getConfigCode() +"]不存在,无法继续更新配置信息!" );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统查询配置信息["+ wrapIn.getConfigCode() +"]时发生异常!" );
				logger.error( "system query config setting ["+ wrapIn.getConfigCode() +"] got an excetipn.", e );
			}
		}
		if( check ){
			try {
				configSetting.setConfigValue( wrapIn.getConfigValue() );
				configSetting = configSettingService.update( configSetting );
				ApplicationCache.notify( BBSConfigSetting.class );
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存系统配置信息时发生异常!" );
				logger.error( "BBSConfigSettingService save object got an exception", e );
			}
		}
		if( check ){
			if( configSetting != null ){
				try {
					wrap = wrapout_copier.copy( configSetting );
					result.setData( wrap );
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统转换对象为输出格式时发生异常!" );
					logger.error( "system copy object to wrap out got an exception", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取BBSConfigSetting对象.", response = WrapOutBBSConfigSetting.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<>();
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		
		String cacheKey = catchNamePrefix + "#id#" + id;
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wrap = (WrapOutBBSConfigSetting) element.getObjectValue();
			result.setData( wrap );
		}else{
			try {
				configSetting = configSettingService.get( id );
				if( configSetting != null ){
					wrap = wrapout_copier.copy( configSetting );
					
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
	
	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "获取BBSConfigSetting列表.", response = WrapOutBBSConfigSetting.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutBBSConfigSetting>> result = new ActionResult<List<WrapOutBBSConfigSetting>>();
		List<WrapOutBBSConfigSetting> wraps = null;
		List<BBSConfigSetting> configSettingList = null;
		String cacheKey = catchNamePrefix + "#all";
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wraps = ( List<WrapOutBBSConfigSetting> ) element.getObjectValue();
			result.setData( wraps );
		}else{
			try {
				configSettingList = configSettingService.listAll();
				if( configSettingList != null ){
					wraps = wrapout_copier.copy( configSettingList );
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
	@HttpMethodDescribe( value = "根据CODE获取BBSConfigSetting对象.", request = WrapInBBSConfigSetting.class, response = WrapOutBBSConfigSetting.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response getByCode( @Context HttpServletRequest request, WrapInBBSConfigSetting wrapIn ) {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<WrapOutBBSConfigSetting>();
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		if( wrapIn == null ){
			logger.error( "wrapIn is null, system can not get any object." );
		}
		if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
			logger.error( "config code is null, system can not get any object." );
		}
		
		String cacheKey = catchNamePrefix + "#code#" + wrapIn.getConfigCode();
		Element element = null;
		element = cache.get( cacheKey );
		if( element != null ){
			wrap = ( WrapOutBBSConfigSetting ) element.getObjectValue();
			result.setData( wrap );
		}else{
			try {
				configSetting = configSettingService.getWithConfigCode( wrapIn.getConfigCode() );
				if( configSetting != null ){
					wrap = wrapout_copier.copy( configSetting );
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
