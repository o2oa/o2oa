package com.x.bbs.assemble.control.jaxrs.foruminfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoProcessException;

@Path("forum")
public class ForumInfoAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( ForumInfoAction.class );
	
	/**
	 * 访问论坛信息，匿名用户可以访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "获取登录者可以访问到的所有ForumInfo的信息列表.", response = WrapOutForumInfo.class)
	@GET
	@Path("view/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response viewAllWithMyPermission( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutForumInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Boolean check = true;

		if(check){
			try {
				result = new ExcuteGetAllWithPermission().execute( request, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ForumInfoProcessException( e, "获取登录者可以访问到的所有ForumInfo的信息列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据指定ID获取论坛信息.", response = WrapOutForumInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutForumInfo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ForumInfoIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteGet().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ForumInfoProcessException( e, "根据指定ID获取论坛信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}