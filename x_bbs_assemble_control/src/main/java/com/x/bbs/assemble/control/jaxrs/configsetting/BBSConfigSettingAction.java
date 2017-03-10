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

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.bbs.assemble.control.service.BBSConfigSettingService;
import com.x.bbs.assemble.control.service.UserManagerService;
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
	private UserManagerService userManagerService = new UserManagerService();
	
	@HttpMethodDescribe(value = "更新BBSConfigSetting对象, 配置信息不允许新建和删除操作.", request = JsonElement.class, response = WrapOutBBSConfigSetting.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		WrapInBBSConfigSetting wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInBBSConfigSetting.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			try {
				if( !userManagerService.isHasRole( effectivePerson.getName(), "BBSSystemAdmin") ){
					check = false;
					Exception exception = new InsufficientPermissionsException( effectivePerson.getName(), "BBSSystemAdmin" );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e1) {
				check = false;
				Exception exception = new InsufficientPermissionsException( effectivePerson.getName(), "BBSSystemAdmin" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
				check = false;
				Exception exception = new ConfigSettingCodeEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( wrapIn.getConfigValue() == null || wrapIn.getConfigValue().isEmpty() ){
				check = false;
				Exception exception = new ConfigSettingValueEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				configSetting = configSettingService.getWithConfigCode( wrapIn.getConfigCode() );
				if( configSetting == null ){
					check = false;
					Exception exception = new ConfigSettingNotExistsException( wrapIn.getConfigCode() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ConfigSettingQueryByCodeException( e, wrapIn.getConfigCode() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				configSetting.setConfigValue( wrapIn.getConfigValue() );
				configSetting = configSettingService.update( configSetting );
				ApplicationCache.notify( BBSConfigSetting.class );
			} catch (Exception e) {
				Exception exception = new ConfigSettingUpdateException( e, wrapIn.getId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( configSetting != null ){
				try {
					wrap = wrapout_copier.copy( configSetting );
					result.setData( wrap );
				} catch (Exception e) {
					Exception exception = new ConfigSettingWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ConfigSettingIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}else{
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
						Exception exception = new ConfigSettingNotExistsException( id );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				} catch (Throwable th) {
					Exception exception = new ConfigSettingQueryByIdException( th, id );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
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
				Exception exception = new ConfigSettingListAllException( th );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@Path( "code" )
	@HttpMethodDescribe( value = "根据CODE获取BBSConfigSetting对象.", request = JsonElement.class, response = WrapOutBBSConfigSetting.class)
	@PUT
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response getByCode( @Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<WrapOutBBSConfigSetting>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		WrapInBBSConfigSetting wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInBBSConfigSetting.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		if( check ){
			if( wrapIn.getConfigCode() == null || wrapIn.getConfigCode().isEmpty() ){
				Exception exception = new ConfigSettingCodeEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}else{
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
							Exception exception = new ConfigSettingNotExistsException( wrapIn.getConfigCode() );
							result.error( exception );
							logger.error( exception, effectivePerson, request, null);
						}
					} catch (Throwable th) {
						Exception exception = new ConfigSettingQueryByCodeException( th, wrapIn.getConfigCode() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
					}
				}
			}	
		}
			
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
