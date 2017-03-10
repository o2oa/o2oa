package com.x.bbs.assemble.control.jaxrs.configsetting;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.bbs.assemble.control.service.BBSConfigSettingService;
import com.x.bbs.entity.BBSConfigSetting;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;


@Path( "setting" )
public class BBSConfigSettingAnonymousAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( BBSConfigSettingAnonymousAction.class );
	private BeanCopyTools<BBSConfigSetting, WrapOutBBSConfigSetting> wrapout_copier = BeanCopyToolsBuilder.create( BBSConfigSetting.class, WrapOutBBSConfigSetting.class, null, WrapOutBBSConfigSetting.Excludes);
	private BBSConfigSettingService configSettingService = new BBSConfigSettingService();
	private Ehcache cache = ApplicationCache.instance().getCache( BBSConfigSetting.class );
	private String catchNamePrefix = this.getClass().getName();

	@HttpMethodDescribe(value = "获取BBS系统名称配置的BBSConfigSetting对象.", response = WrapOutBBSConfigSetting.class)
	@GET
	@Path( "bbsName" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getBBSName(@Context HttpServletRequest request ) {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutBBSConfigSetting wrap = null;
		BBSConfigSetting configSetting = null;
		
		String cacheKey = catchNamePrefix + "#code#BBS_LOGO_NAME";
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wrap = (WrapOutBBSConfigSetting) element.getObjectValue();
			result.setData( wrap );
		}else{
			try {
				configSetting = configSettingService.getWithConfigCode( "BBS_LOGO_NAME" );
				if( configSetting != null ){
					wrap = wrapout_copier.copy( configSetting );
					cache.put( new Element( cacheKey, wrap ) );
					result.setData(wrap);
				}else{
					Exception exception = new ConfigSettingNotExistsException( "BBS_LOGO_NAME" );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Throwable th) {
				Exception exception = new ConfigSettingQueryByCodeException( th, "BBS_LOGO_NAME" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
