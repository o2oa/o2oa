package com.x.bbs.assemble.control.jaxrs.configsetting;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingProcessException;

@Path( "setting" )
public class BBSConfigSettingAnonymousAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( BBSConfigSettingAnonymousAction.class );
	
	@HttpMethodDescribe(value = "获取BBS系统名称配置的BBSConfigSetting对象.", response = WrapOutBBSConfigSetting.class)
	@GET
	@Path( "bbsName" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getBBSName(@Context HttpServletRequest request ) {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ExcuteGetBBSName().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ConfigSettingProcessException( e, "系统在更新配置信息时发生异常！" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
