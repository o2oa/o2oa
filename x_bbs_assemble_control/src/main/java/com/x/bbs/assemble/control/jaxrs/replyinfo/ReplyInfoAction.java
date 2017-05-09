package com.x.bbs.assemble.control.jaxrs.replyinfo;

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
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.CountEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.PageEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyInfoProcessException;



@Path("reply")
public class ReplyInfoAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( ReplyInfoAction.class );

	@HttpMethodDescribe( value = "列示根据过滤条件的ReplyInfo,下一页.", response = WrapOutReplyInfo.class, request = JsonElement.class )
	@PUT
	@Path( "filter/list/page/{page}/count/{count}" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listWithSubjectForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutReplyInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ReplyInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			if( page == null ){
				check = false;
				Exception exception = new PageEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			if( count == null ){
				check = false;
				Exception exception = new CountEmptyException();
				result.error( exception );
			}
		}
		if(check){
			try {
				result = new ExcuteListWithSubjectForPage().execute( request, effectivePerson, wrapIn, page , count );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ReplyInfoProcessException( e, "列示根据过滤条件的ReplyInfo下一页时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据指定ID获取回贴信息.", response = WrapOutReplyInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutReplyInfo> result = new ActionResult<>();
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
				result = new ExcuteGet().execute( request, effectivePerson, id );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ReplyInfoProcessException( e, "根据指定ID获取回贴信息时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}