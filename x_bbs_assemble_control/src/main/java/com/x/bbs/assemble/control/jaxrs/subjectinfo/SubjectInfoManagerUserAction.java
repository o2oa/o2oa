package com.x.bbs.assemble.control.jaxrs.subjectinfo;

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
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.WrapInConvertException;



@Path("user/subject")
public class SubjectInfoManagerUserAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( SubjectInfoManagerUserAction.class );
	
	
	
	@HttpMethodDescribe(value = "根据指定ID获取主题具体信息.", response = WrapOutSubjectInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
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
				result = new ExcuteSubjectGet().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "根据指定ID获取主题具体信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "设置为精华主题.", response = WrapOutId.class)
	@GET
	@Path("setCream/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setCream( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectSetCream().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "设置精华主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消精华主题.", response = WrapOutId.class)
	@GET
	@Path("nonCream/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonCream( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectNonCream().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "锁定主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "设置为原创主题.", response = WrapOutId.class)
	@GET
	@Path("setOriginal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOriginal( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectSetOriginal().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "设置为原创主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消原创主题.", response = WrapOutId.class)
	@GET
	@Path("nonOriginal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonOriginal( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectNonOriginal().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "取消原创主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "锁定主题: 状态修改为'启用', 属性stopReply = false.", response = WrapOutId.class)
	@GET
	@Path("unlock/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unlock( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectUnLock().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "锁定主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "锁定主题: 状态修改为'已锁定', 属性stopReply = true.", response = WrapOutId.class)
	@GET
	@Path("lock/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response lock( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectLock().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "锁定主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消完成主题: 属性isCompleted = false.", response = WrapOutId.class)
	@GET
	@Path("uncomplete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unComplete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectUnComplete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "取消完成主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "完成主题: 属性isCompleted = true.", response = WrapOutId.class)
	@GET
	@Path("complete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response complete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectCompleted().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "完成主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消问题贴采纳回复.", response = WrapOutId.class)
	@GET
	@Path("unacceptreply/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response unAcceptReply( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectUnAcceptReply().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "取消问题贴采纳回复时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}	
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "问题贴采纳回复.", response = WrapOutId.class)
	@GET
	@Path("acceptreply/{id}/{replyId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptReply( @Context HttpServletRequest request, @PathParam("id") String id, @PathParam("replyId") String replyId ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectAcceptReply().execute( request, effectivePerson, id, replyId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "问题贴采纳回复时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "版块置顶.", response = WrapOutId.class)
	@GET
	@Path("topToSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response topToSection( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectTopToMainSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "版块置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}	
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消版块置顶.", response = WrapOutId.class)
	@GET
	@Path("nonTopToSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonTopToSection( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectNonTopToSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "取消版块置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "主版块置顶.", response = WrapOutId.class)
	@GET
	@Path("topToMainSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response topToMainSection( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectTopToMainSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "主版块置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消主版块置顶.", response = WrapOutId.class)
	@GET
	@Path("nonTopToMainSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonTopToMainSection( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectNonTopToMainSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "取消主版块置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "论坛置顶.", response = WrapOutId.class)
	@GET
	@Path("topToForum/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response topToForum( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectTopToForum().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "全局置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消论坛置顶.", response = WrapOutId.class)
	@GET
	@Path("nonTopToForum/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonTopToForum( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectNonTopToForum().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "取消论坛置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "全局置顶.", response = WrapOutId.class)
	@GET
	@Path("topToBBS/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response topToBBS( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectTopToBBS().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "全局置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "推荐到BBS首页.", response = WrapOutId.class)
	@GET
	@Path("setRecommendToBBSIndex/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setRecommendToBBSIndex( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectSetRecommendToBBSIndex().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "推荐到BBS首页时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消推荐到BBS首页.", response = WrapOutId.class)
	@GET
	@Path("nonRecommendToBBSIndex/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonRecommendToBBSIndex( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectNonRecommendToBBSIndex().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "取消推荐到BBS首页时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "取消全局置顶.", response = WrapOutId.class)
	@GET
	@Path("nonTopToBBS/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response nonTopToBBS( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectNonTopToBBS().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "取消全局置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
    
	@HttpMethodDescribe(value = "创建新的主题信息或者更新主题信息.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInSubjectInfo wrapIn = null;
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInSubjectInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteSubjectSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "创建新的主题信息或者更新主题信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除指定的主题信息.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
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
				result = new ExcuteSubjectDelete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "完成主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示我发布的主题.", response = WrapOutSubjectInfo.class, request = JsonElement.class)
	@PUT
	@Path("my/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMySubjectForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, JsonElement jsonElement ) {
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
				result = new ExcuteSubjectListMyForPage().execute( request, effectivePerson, wrapIn, page, count );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new RoleInfoProcessException( e, "列示我发布的主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
}