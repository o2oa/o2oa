package com.x.bbs.assemble.control.jaxrs.subjectinfo;

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
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.WrapInConvertException;

@Path("subject")
public class SubjectInfoAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( SubjectInfoAction.class );
	
	
	
	@HttpMethodDescribe(value = "列示根据过滤条件的推荐主题列表.", response = WrapOutSubjectInfo.class, request = JsonElement.class)
	@PUT
	@Path("recommended/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listRecommendedSubjectForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if(check){
			try {
				result = new ExcuteSubjectListRecommendedForPages().execute( request, effectivePerson, wrapIn, page, count );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "列示根据过滤条件的推荐主题列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取所有推荐到BBS首页的主题列表.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("recommended/index/{count}")
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listRecommendedSubjectForBBSIndex( @Context HttpServletRequest request, @PathParam("count") Integer count ) {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Boolean check = true;		
		if( check ){
			if( count == null || count <= 0 ){
				count = 10;
			}
		}
		if(check){
			try {
				result = new ExcuteSubjectListRecommendedForBBSIndex().execute( request, effectivePerson, count );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "获取所有推荐到BBS首页的主题列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	//所有的置顶贴应该全部取出
	@HttpMethodDescribe(value = "获取所有可以取到的置顶贴列表.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("top/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listTopSubject( @Context HttpServletRequest request, @PathParam("sectionId") String sectionId) {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson( request );
		
		if (check) {
			if ( sectionId == null || sectionId.isEmpty() ) {
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteSubjectListTop().execute( request, effectivePerson, sectionId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "获取所有可以取到的置顶贴列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的精华主题列表.", response = WrapOutSubjectInfo.class, request = JsonElement.class)
	@PUT
	@Path("creamed/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCreamedSubjectForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){ if( page == null ){ page = 1; } }
		if( check ){ if( count == null ){ count = 20; } }
		if(check){
			try {
				result = new ExcuteSubjectListCreamedForPages().execute( request, effectivePerson, wrapIn, page, count );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "列示根据过滤条件的精华主题列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的SubjectInfo,下一页.", response = WrapOutSubjectInfo.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSubjectForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Boolean check = true;
		WrapInFilter wrapIn = null;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteSubjectListForPage().execute( request, effectivePerson, wrapIn, page, count );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "列示根据过滤条件的推荐主题列表时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	

	@HttpMethodDescribe(value = "列示根据过滤条件的SubjectInfo,下一页.", response = WrapOutSubjectInfo.class, request = JsonElement.class)
	@PUT
	@Path("search/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response searchSubjectForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteSubjectSearchForPage().execute( request, effectivePerson, wrapIn, page, count );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "列示根据过滤条件的SubjectInfo时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据指定ID查看主题具体信息，需要记录查询次数和热度的.", response = WrapOutNearSubjectInfo.class)
	@GET
	@Path("view/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response viewSubject( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutNearSubjectInfo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SubjectIdEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteSubjectView().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据指定ID查看主题具体信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}