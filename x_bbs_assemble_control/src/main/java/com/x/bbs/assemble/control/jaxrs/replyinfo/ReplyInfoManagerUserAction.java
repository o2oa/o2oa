package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
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
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSReplyInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;



@Path("user/reply")
public class ReplyInfoManagerUserAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( ReplyInfoManagerUserAction.class );
	private BBSReplyInfoService replyInfoService = new BBSReplyInfoService();
	private BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	private BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	private UserManagerService userManagerService = new UserManagerService();
	private BeanCopyTools<WrapInReplyInfo, BBSReplyInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInReplyInfo.class, BBSReplyInfo.class, null, WrapInReplyInfo.Excludes );
	private BeanCopyTools< BBSReplyInfo, WrapOutReplyInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSReplyInfo.class, WrapOutReplyInfo.class, null, WrapOutReplyInfo.Excludes);

	
	@HttpMethodDescribe( value = "创建新的回贴信息或者更新回贴信息.", request = JsonElement.class, response = WrapOutId.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInReplyInfo wrapIn = null;
		BBSSubjectInfo subjectInfo = null;
		BBSReplyInfo replyInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSForumInfo forumInfo = null;
		EffectivePerson currentPerson = this.effectivePerson( request );
		Boolean hasPermission = false;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInReplyInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		if( check ){
			wrapIn.setHostIp( request.getRemoteHost() );
			if( wrapIn.getSubjectId() == null ){
				check = false;
				Exception exception = new ReplySubjectIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getContent() == null ){
				check = false;
				Exception exception = new ReplyContentEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		//查询关联的主题信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoService.get( wrapIn.getSubjectId() );
				if( subjectInfo == null ){
					check = false;
					Exception exception = new SubjectNotExistsException( wrapIn.getSubjectId() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectQueryByIdException( e, wrapIn.getSubjectId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		//判断主题是否允许用户回复，已锁定的主题不允许用户进行回复
		if (check) {
			if( "已锁定".equals( subjectInfo.getSubjectStatus() )){
				check = false;
				Exception exception = new SubjectLockedException( wrapIn.getSubjectId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		//判断主题所在的版块是否允许用户回复，或者用户是否有权限进行主题回复
		if (check) {
			try {
				sectionInfo = sectionInfoServiceAdv.get( subjectInfo.getSectionId() );
				if( sectionInfo == null ){
					check = false;
					Exception exception = new SectionNotExistsException( subjectInfo.getSectionId() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionQueryByIdException( e, subjectInfo.getSectionId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if( "根据权限".equals( sectionInfo.getReplyPublishAble() ) ){
				//那么要开始判断用户是否有对版内的主题权限进行回复
				try{
					hasPermission = userManagerService.hasPermission( currentPerson.getName(), "SECTION_REPLY_PUBLISH_" + subjectInfo.getSectionId() );
					if( !hasPermission ){
						check = false;
						Exception exception = new SectionPermissionsException( sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH" );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				}catch( Exception e ){
					check = false;
					Exception exception = new SectionPermissionsCheckException( e, currentPerson.getName(), sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH" );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( subjectInfo != null && !subjectInfo.getMainSectionId().equals( subjectInfo.getSectionId())){
			//再查询用户是否有主版块的回复权限
			if (check) {
				try {
					sectionInfo = sectionInfoServiceAdv.get( subjectInfo.getMainSectionId() );
					if( sectionInfo == null ){
						check = false;
						Exception exception = new SectionNotExistsException( subjectInfo.getMainSectionId() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new SectionQueryByIdException( e, subjectInfo.getMainSectionId() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
			
			if (check) {
				if( "根据权限".equals( sectionInfo.getReplyPublishAble() ) ){
					//那么要开始判断用户是否有对版内的主题权限进行回复
					try{
						hasPermission = userManagerService.hasPermission( currentPerson.getName(), "SECTION_REPLY_PUBLISH_" + subjectInfo.getMainSectionId() );
						if( !hasPermission ){
							check = false;
							Exception exception = new SectionPermissionsException( sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH" );
							result.error( exception );
							logger.error( exception, currentPerson, request, null);
						}
					}catch( Exception e ){
						check = false;
						Exception exception = new SectionPermissionsCheckException( e, currentPerson.getName(), sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH" );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				}
			}
		}
		//判断主题所在的论坛是否允许用户回复，或者用户是否有权限进行主题回复
		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get( subjectInfo.getForumId() );
				if( forumInfo == null ){
					check = false;
					Exception exception = new ForumInfoNotExistsException( subjectInfo.getForumId() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoQueryByIdException( e, subjectInfo.getForumId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if( "根据权限".equals( forumInfo.getReplyPublishAble() ) ){
				//那么要开始判断用户是否有对版内的主题权限进行回复
				try{
					hasPermission = userManagerService.hasPermission( currentPerson.getName(), "FORUM_REPLY_PUBLISH_" + subjectInfo.getForumId() );
					if( !hasPermission ){
						check = false;
						Exception exception = new ForumPermissionException( subjectInfo.getForumName(), "FORUM_REPLY_PUBLISH" );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				}catch( Exception e ){
					check = false;
					Exception exception = new ForumPermissionsCheckException( e, currentPerson.getName(), subjectInfo.getForumName(), "FORUM_REPLY_PUBLISH" );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if (check) {
			wrapIn.setForumId( subjectInfo.getForumId() );
			wrapIn.setForumName( subjectInfo.getForumName() );
			wrapIn.setMainSectionId( subjectInfo.getMainSectionId() );
			wrapIn.setMainSectionName( subjectInfo.getMainSectionName() );
			wrapIn.setSectionId( subjectInfo.getSectionId() );
			wrapIn.setSectionName( subjectInfo.getSectionName() );
			wrapIn.setCreatorName( currentPerson.getName() );
		}
		
		if( check ){
			if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
				if( subjectInfo.getTitle() != null && !subjectInfo.getTitle().isEmpty() ){
					wrapIn.setTitle( subjectInfo.getTitle() );
				}else{
					wrapIn.setTitle( "无标题" );
				}
			}
		}
		if( check ){
			try {
				replyInfo = wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyWrapInException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				replyInfo.setMachineName( wrapIn.getReplyMachineName() );
				replyInfo.setSystemType( wrapIn.getReplySystemName() );
				replyInfo = replyInfoService.save( replyInfo );
				result.setData( new WrapOutId(replyInfo.getId()));
				operationRecordService.replyOperation( currentPerson.getName(), replyInfo, "CREATE", hostIp, hostName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplySaveException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
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
		BBSReplyInfo replyInfo = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ReplyIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		//判断主题信息是否存在
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
			if( replyInfo == null ){
				check = false;
				Exception exception = new ReplyNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		try {
			replyInfoService.delete( id );
			result.setData( new WrapOutId(id) );
			operationRecordService.replyOperation( currentPerson.getName(), replyInfo, "DELETE", hostIp, hostName );
		} catch (Exception e) {
			check = false;
			Exception exception = new ReplyDeleteException( e, id );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
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
		List<WrapOutReplyInfo> wraps = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson(request);
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
				total = replyInfoService.countReplyByUserName( currentPerson.getName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyCountByPersonException( e, currentPerson.getName());
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( total > 0 ){
				try{
					replyInfoList = replyInfoService.listReplyByUserNameForPage( currentPerson.getName(), page * count );
				} catch (Exception e) {
					check = false;
					Exception exception = new ReplyCountByPersonException( e, currentPerson.getName());
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
}