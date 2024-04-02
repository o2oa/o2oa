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
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectIdEmpty;

@Path("user/subject")
@JaxrsDescribe("主贴管理服务")
public class SubjectInfoManagerUserAction extends StandardJaxrsAction {

	private static  Logger logger = LoggerFactory.getLogger( SubjectInfoManagerUserAction.class );

	@JaxrsMethodDescribe( value = "根据指定ID获取主题具体信息.", action = ActionSubjectGet.class )
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectGet().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "根据指定ID获取主题具体信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "设置为精华主题.", action = ActionSubjectSetCream.class )
	@GET
	@Path("setCream/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setCream( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectSetCream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectSetCream().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "设置精华主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "取消精华主题.", action = ActionSubjectNonCream.class )
	@GET
	@Path("nonCream/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonCream( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonCream.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonCream().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "锁定主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "设置为原创主题.", action = ActionSubjectSetOriginal.class )
	@GET
	@Path("setOriginal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setOriginal( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectSetOriginal.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectSetOriginal().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "设置为原创主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "取消原创主题.", action = ActionSubjectNonOriginal.class )
	@GET
	@Path("nonOriginal/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonOriginal( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonOriginal.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonOriginal().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "取消原创主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "锁定主题: 状态修改为'启用', 属性stopReply = false.", action = ActionSubjectUnLock.class )
	@GET
	@Path("unlock/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void unlock( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectUnLock.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectUnLock().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "锁定主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "锁定主题: 状态修改为'已锁定', 属性stopReply = true", action = ActionSubjectLock.class )
	@GET
	@Path("lock/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void lock( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectLock.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectLock().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "锁定主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "取消完成主题: 属性isCompleted = false.", action = ActionSubjectUnComplete.class )
	@GET
	@Path("uncomplete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void unComplete( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectUnComplete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectUnComplete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "取消完成主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "完成主题: 属性isCompleted = true", action = ActionSubjectCompleted.class )
	@GET
	@Path("complete/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void complete( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectCompleted().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "完成主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "取消问题贴采纳回复", action = ActionSubjectUnAcceptReply.class )
	@GET
	@Path("unacceptreply/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void unAcceptReply( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectUnAcceptReply.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectUnAcceptReply().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "取消问题贴采纳回复时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "问题贴采纳回复", action = ActionSubjectAcceptReply.class )
	@GET
	@Path("acceptreply/{id}/{replyId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void acceptReply( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id,
			@JaxrsParameterDescribe("回帖信息ID") @PathParam("replyId") String replyId ) {
		ActionResult<ActionSubjectAcceptReply.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectAcceptReply().execute( request, effectivePerson, id, replyId );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "问题贴采纳回复时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "版块置顶", action = ActionSubjectTopToSection.class )
	@GET
	@Path("topToSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void topToSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectTopToSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectTopToSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "版块置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "取消版块置顶", action = ActionSubjectNonTopToSection.class )
	@GET
	@Path("nonTopToSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonTopToSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonTopToSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonTopToSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "取消版块置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "主版块置顶", action = ActionSubjectTopToMainSection.class )
	@GET
	@Path("topToMainSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void topToMainSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectTopToMainSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectTopToMainSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "主版块置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "取消主版块置顶", action = ActionSubjectNonTopToMainSection.class )
	@GET
	@Path("nonTopToMainSection/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonTopToMainSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonTopToMainSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonTopToMainSection().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "取消主版块置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "论坛置顶", action = ActionSubjectTopToForum.class )
	@GET
	@Path("topToForum/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void topToForum( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectTopToForum.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectTopToForum().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "全局置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "取消论坛置顶.", action = ActionSubjectNonTopToForum.class )
	@GET
	@Path("nonTopToForum/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonTopToForum( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonTopToForum.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonTopToForum().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "取消论坛置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "全局置顶.", action = ActionSubjectTopToBBS.class )
	@GET
	@Path("topToBBS/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void topToBBS( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectTopToBBS.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectTopToBBS().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "全局置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "推荐到BBS首页.", action = ActionSubjectSetRecommendToBBSIndex.class )
	@GET
	@Path("setRecommendToBBSIndex/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setRecommendToBBSIndex( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectSetRecommendToBBSIndex.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectSetRecommendToBBSIndex().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "推荐到BBS首页时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "取消推荐到BBS首页.", action = ActionSubjectNonRecommendToBBSIndex.class )
	@GET
	@Path("nonRecommendToBBSIndex/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonRecommendToBBSIndex( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonRecommendToBBSIndex.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonRecommendToBBSIndex().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "取消推荐到BBS首页时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "取消全局置顶.", action = ActionSubjectNonTopToBBS.class )
	@GET
	@Path("nonTopToBBS/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void nonTopToBBS( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id ) {
		ActionResult<ActionSubjectNonTopToBBS.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectNonTopToBBS().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "取消全局置顶时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "创建新的主题信息或者更新主题信息.", action = ActionSubjectSave.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<ActionSubjectSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Boolean check = true;

		if(check){
			try {
				result = new ActionSubjectSave().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				logger.error( e, effectivePerson, request, jsonElement);
				result.error( e );
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "根据ID删除指定的主题信息.", action = ActionSubjectDelete.class )
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id) {
		ActionResult<ActionSubjectDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ActionSubjectDelete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "完成主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "将指定ID的主贴转移到其他的版块中.", action = ActionSubjectChangeSection.class )
	@PUT
	@Path("change/section")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changeSection( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<ActionSubjectChangeSection.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if(check){
			try {
				result = new ActionSubjectChangeSection().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "将指定ID的主贴转移到其他的版块中时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "列示投票记录.", action = ActionVoteRecordListMyForPage.class )
	@PUT
	@Path("voterecord/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listVoteRecordForPage( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("显示页码") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页显示条目数量") @PathParam("count") Integer count,
			JsonElement jsonElement ) {
		ActionResult<List<ActionVoteRecordListMyForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionVoteRecordListMyForPage().execute( request, effectivePerson, page, count, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "列示主题投票记录时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "提交投票信息.", action = ActionSubjectSubmitVoteResult.class )
	@Path("vote/submit")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void voteSubmit(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<ActionSubjectSubmitVoteResult.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Boolean check = true;

		if(check){
			try {
				result = new ActionSubjectSubmitVoteResult().execute( request, effectivePerson, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "用户提交投票结果时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe( value = "列示我发布的主题.", action = ActionSubjectListMyForPage.class )
	@PUT
	@Path("my/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMySubjectForPage( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("显示页码") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页显示条目数量") @PathParam("count") Integer count,
			JsonElement jsonElement ) {
		ActionResult<List<ActionSubjectListMyForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if(check){
			try {
				result = new ActionSubjectListMyForPage().execute( request, effectivePerson, page, count, jsonElement );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess( e, "列示我发布的主题时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
