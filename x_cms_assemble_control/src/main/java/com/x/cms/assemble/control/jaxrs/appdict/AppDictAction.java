package com.x.cms.assemble.control.jaxrs.appdict;

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

import com.google.gson.JsonElement;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.cms.assemble.control.jaxrs.appdict.exception.AppDictDeleteException;
import com.x.cms.assemble.control.jaxrs.appdict.exception.AppDictListByAppIdException;
import com.x.cms.assemble.control.jaxrs.appdict.exception.AppDictQueryByIdException;
import com.x.cms.assemble.control.jaxrs.appdict.exception.AppDictSaveException;
import com.x.cms.assemble.control.jaxrs.appdict.exception.AppDictUpdateException;
import com.x.cms.assemble.control.jaxrs.appdict.exception.WrapInConvertException;

@Path("appdict")
public class AppDictAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( AppDictAction.class );
	
	@HttpMethodDescribe(value = "创建数据字典以及数据.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInAppDict wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAppDict.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if(check){
			try {
				result = new ExcuteSave().execute( effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new AppDictSaveException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
			
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "删除指定的数据字典以及数据字典数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( effectivePerson, appDictId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictDeleteException( e, appDictId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取Application的数据字典列表.", response = WrapOutAppDict.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithAppId(@Context HttpServletRequest request, @PathParam("appId") String appId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppDict>> result = new ActionResult<>();
		try {
			result = new ExcuteListByAppId().execute( effectivePerson, appId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictListByAppIdException( e, appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取单个数据字典以及数据字典数据.", response = WrapOutAppDict.class)
	@GET
	@Path("{appDictId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutAppDict> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( effectivePerson, appDictId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppDictQueryByIdException( e, appDictId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	

	@HttpMethodDescribe(value = "更新数据字典以及数据.", request = JsonElement.class, response = WrapOutId.class)
	@PUT
	@Path("{appDictId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInAppDict wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAppDict.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				result = new ExcuteUpdate().execute( effectivePerson, wrapIn, appDictId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new AppDictUpdateException( e, appDictId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	

}