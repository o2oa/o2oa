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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.bbs.assemble.control.service.BBSReplyInfoService;
import com.x.bbs.entity.BBSReplyInfo;



@Path("reply")
public class ReplyInfoAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( ReplyInfoAction.class );
	private BBSReplyInfoService replyInfoService = new BBSReplyInfoService();
	private BeanCopyTools< BBSReplyInfo, WrapOutReplyInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSReplyInfo.class, WrapOutReplyInfo.class, null, WrapOutReplyInfo.Excludes);

	@HttpMethodDescribe( value = "列示根据过滤条件的ReplyInfo,下一页.", response = WrapOutReplyInfo.class, request = WrapInFilter.class )
	@PUT
	@Path( "filter/list/page/{page}/count/{count}" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listWithSubjectForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutReplyInfo>> result = new ActionResult<>();
		List<WrapOutReplyInfo> wraps = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		Boolean check = true;
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法查询回贴信息！" ) );
				result.setUserMessage( "传入的参数为空，无法查询回贴信息！" );
			}
		}
		if( check ){
			if( page == null ){
				check = false;
				result.error( new Exception("传入的参数page为空，无法继续查询回贴列表！" ) );
				result.setUserMessage( "传入的参数page为空，无法继续查询回贴列表！" );
			}
		}
		if( check ){
			if( count == null ){
				check = false;
				result.error( new Exception("传入的参数count为空，无法继续查询回贴列表！" ) );
				result.setUserMessage( "传入的参数count为空，无法继续查询回贴列表！" );
			}
		}
		if( check ){
			try{
				total = replyInfoService.countWithSubjectForPage( wrapIn.getSubjectId() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "根据ID信息查询版块信息时发生异常！" );
				logger.error( "system query reply info with section info got an exceptin.", e );
			}
		}
		if( check ){
			if( total > 0 ){
				try{
					replyInfoList = replyInfoService.listWithSubjectForPage( wrapIn.getSubjectId(), page * count );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "根据ID信息查询版块信息时发生异常！" );
					logger.error( "system query reply info with section info got an exceptin.", e );
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
					result.error( new Exception("将主题转换为输出格式发生异常！" ) );
					result.setUserMessage( "将主题转换为输出格式发生异常！" );
					logger.error( "system copy subject info list to wrapout got an exceptin.", e );
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
		WrapOutReplyInfo wrap = null;
		BBSReplyInfo replyInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数ID为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数ID为空，无法继续进行查询" );
			}
		}
		if( check ){
			try {
				replyInfo = replyInfoService.get( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据Id查询回贴信息时发生异常" );
				logger.error( "system query reply with id got an exception!", e );
			}
		}
		if( check ){
			if( replyInfo != null ){
				try {
					wrap = wrapout_copier.copy( replyInfo );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在将回贴信息列表转换为输出格式时发生异常" );
					logger.error( "system copy reply to wrap got an exception!", e );
				}
			}else{
				result.error( new Exception("回贴信息不存在！") );
				result.setUserMessage( "回贴信息不存在！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}