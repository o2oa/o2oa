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
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingProcessException;


@Path( "user/setting" )
public class BBSConfigSettingAction extends StandardJaxrsAction{	
	
	private Logger logger = LoggerFactory.getLogger( BBSConfigSettingAction.class );	
	
	@HttpMethodDescribe(value = "更新BBSConfigSetting对象, 配置信息不允许新建和删除操作.", request = JsonElement.class, response = WrapOutBBSConfigSetting.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBBSConfigSetting> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInBBSConfigSetting wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInBBSConfigSetting.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ConfigSettingInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteUpdate().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ConfigSettingProcessException( e, "系统在更新配置信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
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
		if( id == null || id.isEmpty() ){
			Exception exception = new ConfigSettingIdEmptyException();
			result.error( exception );
		}else{
			try {
				result = new ExcuteGet().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ConfigSettingProcessException( e, "系统在更新配置信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}			
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取BBSConfigSetting列表.", response = WrapOutBBSConfigSetting.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutBBSConfigSetting>> result = new ActionResult<List<WrapOutBBSConfigSetting>>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ExcuteGetAll().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ConfigSettingProcessException( e, "系统在更新配置信息时发生异常！" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
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
		WrapInBBSConfigSetting wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInBBSConfigSetting.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ConfigSettingInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				result = new ExcuteGetWithCode().execute( request, effectivePerson, wrapIn.getConfigCode() );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ConfigSettingProcessException( e, "系统在更新配置信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}			
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
