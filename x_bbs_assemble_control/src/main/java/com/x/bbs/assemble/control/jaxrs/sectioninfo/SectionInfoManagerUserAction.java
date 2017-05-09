package com.x.bbs.assemble.control.jaxrs.sectioninfo;

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
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ForumIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionInfoProcessException;

@Path("user/section")
public class SectionInfoManagerUserAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( SectionInfoManagerUserAction.class );
	
	@HttpMethodDescribe(value = "获取所有版块的信息列表.", response = WrapOutSectionInfo.class)
	@GET
	@Path("all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllSection( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if(check){
			try {
				result = new ExcuteAllSections().execute( request, effectivePerson );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "获取所有版块的信息列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据论坛ID获取所有主版块的信息列表(管理).", response = WrapOutSectionInfo.class)
	@GET
	@Path("forum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithForum( @Context HttpServletRequest request, @PathParam("forumId") String forumId ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				Exception exception = new ForumIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteListWithForum().execute( request, effectivePerson, forumId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据论坛ID获取所有主版块的信息列表(管理)时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据主版块ID查询所有的子版块信息列表(管理).", response = WrapOutSectionInfo.class)
	@GET
	@Path("sub/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllSubSectionByMainSectionId( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId ) {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;	
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}		
		if(check){
			try {
				result = new ExcuteAllListSubSectionByMainSectionId().execute( request, effectivePerson, sectionId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据主版块ID查询所有的子版块信息列表(管理)时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "创建新的版块信息或者更新版块信息.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInSectionInfo wrapIn = null;
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInSectionInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new SectionInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "创建新的版块信息或者更新版块信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "根据ID删除指定的版块信息，如果版块里有贴子，则不允许删除.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson( request );
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteDelete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据ID删除指定的版块信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除指定的版块信息，如果版块里有贴子，则全部删除.", response = WrapOutId.class)
	@DELETE
	@Path("force/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteForce(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson( request );
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}		
		if(check){
			try {
				result = new ExcuteDeleteForce().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据ID删除指定的版块信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}	
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}