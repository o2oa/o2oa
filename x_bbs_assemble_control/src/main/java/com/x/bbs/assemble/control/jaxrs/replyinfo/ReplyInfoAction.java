package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
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
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.service.BBSReplyInfoService;
import com.x.bbs.entity.BBSReplyInfo;



@Path("reply")
public class ReplyInfoAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( ReplyInfoAction.class );
	private BBSReplyInfoService replyInfoService = new BBSReplyInfoService();
	private BeanCopyTools< BBSReplyInfo, WrapOutReplyInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSReplyInfo.class, WrapOutReplyInfo.class, null, WrapOutReplyInfo.Excludes);

	@HttpMethodDescribe( value = "列示根据过滤条件的ReplyInfo,下一页.", response = WrapOutReplyInfo.class, request = JsonElement.class )
	@PUT
	@Path( "filter/list/page/{page}/count/{count}" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listWithSubjectForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, JsonElement jsonElement ) {
		ActionResult<List<WrapOutReplyInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutReplyInfo> wraps = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		if( check ){
			if( page == null ){
				check = false;
				Exception exception = new PageEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( count == null ){
				check = false;
				Exception exception = new CountEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try{
				total = replyInfoService.countWithSubjectForPage( wrapIn.getSubjectId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyCountBySubjectException( e, wrapIn.getSubjectId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( total > 0 ){
				try{
					replyInfoList = replyInfoService.listWithSubjectForPage( wrapIn.getSubjectId(), page * count );
				} catch (Exception e) {
					check = false;
					Exception exception = new ReplyListBySubjectException( e, wrapIn.getSubjectId() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			if( page <= 0 ){
				page = 1;
			}
			if( count <= 0 ){
				count = 20;
			}
			int startIndex = ( page - 1 ) * count;
			int endIndex = page * count;
			for( int i=0; replyInfoList != null && i< replyInfoList.size(); i++ ){
				if( i < replyInfoList.size() && i >= startIndex && i < endIndex ){
					replyInfoList_out.add( replyInfoList.get( i ) );
				}
			}
			if( replyInfoList_out != null && !replyInfoList_out.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( replyInfoList_out );
					result.setData( wraps );
					result.setCount( total );
				} catch (Exception e) {
					check = false;
					Exception exception = new ReplyWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutReplyInfo wrap = null;
		BBSReplyInfo replyInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ReplyIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				replyInfo = replyInfoService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( replyInfo != null ){
				try {
					wrap = wrapout_copier.copy( replyInfo );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					Exception exception = new ReplyWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}else{
				Exception exception = new ReplyNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}