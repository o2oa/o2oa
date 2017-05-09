package com.x.bbs.assemble.control.jaxrs.foruminfo;

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
import com.x.bbs.assemble.control.jaxrs.configsetting.exception.ConfigSettingProcessException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoProcessException;



@Path("user/forum")
public class ForumInfoManagerUserAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( ForumInfoManagerUserAction.class );	

	/**
	 * 访问论坛信息，登录用户访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "获取所有ForumInfo的信息列表.", response = WrapOutForumInfo.class)
	@GET
	@Path("all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutForumInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		
		if(check){
			try {
				result = new ExcuteGetAll().execute( request, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ForumInfoProcessException( e, "获取所有ForumInfo的信息列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/**
	 * 保存论坛信息，登录用户访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "创建新的论坛信息或者更新论坛信息.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post( @Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInForumInfo wrapIn = null;
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson( request );
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInForumInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ForumInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ForumInfoProcessException( e, "创建新的论坛信息或者更新论坛信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 删除论坛信息，登录用户访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "根据ID删除指定的论坛信息，如果论坛里有版块或者贴子，则不允许删除.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ForumInfoIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteDelete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ForumInfoProcessException( e, "系统在删除论坛信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
}