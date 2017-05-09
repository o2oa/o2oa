package com.x.bbs.assemble.control.jaxrs.sectioninfo;

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
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ForumIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionIdEmptyException;

@Path("section")
public class SectionInfoAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( SectionInfoAction.class );
	
	
	@HttpMethodDescribe(value = "根据论坛ID获取所有版块的信息列表.", response = WrapOutSectionInfo.class)
	@GET
	@Path("viewforum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response viewWithForum( @Context HttpServletRequest request, @PathParam("forumId") String forumId ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson( request );
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				Exception exception = new ForumIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteViewWithForum().execute( request, effectivePerson, forumId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据主版块ID查询所有的子版块信息列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据主版块ID查询所有的子版块信息列表.", response = WrapOutSectionInfo.class)
	@GET
	@Path("viewsub/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubSectionByMainSectionId( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}		
		if(check){
			try {
				result = new ExcuteListSubSectionByMainSectionId().execute( request, effectivePerson, sectionId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据主版块ID查询所有的子版块信息列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "根据指定ID获取版块信息.", response = WrapOutSectionInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSectionInfo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteGet().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据指定ID获取版块信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}