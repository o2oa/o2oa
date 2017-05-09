package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyInfoProcessException;



@Path("user/reply")
public class ReplyInfoManagerUserAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( ReplyInfoManagerUserAction.class );
	
	
	@HttpMethodDescribe( value = "创建新的回贴信息或者更新回贴信息.", request = JsonElement.class, response = WrapOutId.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		EffectivePerson currentPerson = this.effectivePerson( request );
		WrapInReplyInfo wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInReplyInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ReplyInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ReplyInfoProcessException( e, "创建新的回贴信息或者更新回贴信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 用户只有自己的回复可以删除
	 * 管理员和版主可以删除其他回复内容
	 * @param request
	 * @param id
	 * @return
	 */
	@HttpMethodDescribe(value = "根据ID删除指定的回贴信息.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ReplyIdEmptyException();
				result.error( exception );
			}
		}			
		if(check){
			try {
				result = new ExcuteDelete().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ReplyInfoProcessException( e, "根据ID删除指定的回贴信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe( value = "列示我发表的回贴,下一页.", response = WrapOutReplyInfo.class, request = JsonElement.class )
	@PUT
	@Path( "my/list/page/{page}/count/{count}" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listMyReplyForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutReplyInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ReplyInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if(check){
			try {
				result = new ExcuteListMyReplyForPages().execute( request, effectivePerson, wrapIn, page , count );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ReplyInfoProcessException( e, "列示我发表的回贴下一页时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}