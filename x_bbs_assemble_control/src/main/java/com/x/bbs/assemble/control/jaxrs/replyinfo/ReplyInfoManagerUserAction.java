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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
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

	
	@HttpMethodDescribe( value = "创建新的回贴信息或者更新回贴信息.", request = WrapInReplyInfo.class, response = WrapOutId.class )
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, WrapInReplyInfo wrapIn ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
		BBSReplyInfo replyInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSForumInfo forumInfo = null;
		EffectivePerson currentPerson = this.effectivePerson( request );
		Boolean hasPermission = false;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法保存主题信息！" ) );
				result.setUserMessage( "传入的参数为空，无法保存主题信息！" );
			}
		}
		if( check ){
			wrapIn.setHostIp( request.getRemoteHost() );
			if( wrapIn.getSubjectId() == null ){
				check = false;
				result.error( new Exception("传入的主题ID为空，无法保存主题信息！" ) );
				result.setUserMessage( "传入的主题ID为空，无法保存主题信息！" );
			}
		}
		if( check ){
			if( wrapIn.getContent() == null ){
				check = false;
				result.error( new Exception("传入的回帖内容为空，无法保存回帖信息！" ) );
				result.setUserMessage( "传入的回帖内容为空，无法保存回帖信息！" );
			}
		}
		//查询关联的主题信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoService.get( wrapIn.getSubjectId() );
				if( subjectInfo == null ){
					check = false;
					result.error( new Exception("主题信息不存在，或者主题信息已经被删除！"));
					result.setUserMessage("主题信息不存在，或者主题信息已经被删除，无法保存回帖信息！");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("根据主题ID查询主题信息发生异常，无法保存回帖信息！");
				logger.error("system query subject info with id got an exceptin. id:" + wrapIn.getSubjectId(), e);
			}
		}
		//判断主题是否允许用户回复，已锁定的主题不允许用户进行回复
		if (check) {
			if( "已锁定".equals( subjectInfo.getSubjectStatus() )){
				check = false;
				result.error( new Exception("主题信息已锁定，不允许进行回复！"));
				result.setUserMessage("主题信息已锁定，不允许进行回复！");
			}
		}
		//判断主题所在的版块是否允许用户回复，或者用户是否有权限进行主题回复
		if (check) {
			try {
				sectionInfo = sectionInfoServiceAdv.get( subjectInfo.getMainSectionId() );
				if( sectionInfo == null ){
					check = false;
					result.error( new Exception("版块信息不存在，或者版块信息已经被删除！"));
					result.setUserMessage("版块信息不存在，或者版块信息已经被删除，无法保存回帖信息！");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("根据版块ID查询版块信息发生异常，无法保存回帖信息！");
				logger.error("system query section info with id got an exceptin. id:" + subjectInfo.getMainSectionId(), e);
			}
		}
		if (check) {
			if( "根据权限".equals( sectionInfo.getReplyPublishAble() ) ){
				//那么要开始判断用户是否有对版内的主题权限进行回复
				try{
					hasPermission = userManagerService.hasPermission( currentPerson.getName(), "SECTION_REPLY_PUBLISH_" + subjectInfo.getSectionId() );
					if( !hasPermission ){
						check = false;
						result.error( new Exception("用户没有版块["+ sectionInfo.getSectionName() +"]中主题的回复权限！"));
						result.setUserMessage("用户没有版块["+ sectionInfo.getSectionName() +"]中主题的回复权限，无法保存主题信息！");
					}
				}catch( Exception e ){
					check = false;
					result.error( new Exception("系统在判断用户是否有版块["+ sectionInfo.getSectionName() +"]中主题的回复权限时发生异常！"));
					result.setUserMessage("系统在判断用户是否有版块["+ sectionInfo.getSectionName() +"]中主题的回复权限时发生异常，无法保存主题信息！");
					logger.error( "system query user has able permission 'SECTION_REPLY_PUBLISH_" + subjectInfo.getSectionId()+"' got an exception.", e );
				}
			}
		}
		if( subjectInfo != null && subjectInfo.getMainSectionId().equals( subjectInfo.getSectionId())){
			//再查询用户是否有主版块的回复权限
			if (check) {
				try {
					sectionInfo = sectionInfoServiceAdv.get( subjectInfo.getMainSectionId() );
					if( sectionInfo == null ){
						check = false;
						result.error( new Exception("版块信息不存在，或者版块信息已经被删除！"));
						result.setUserMessage("版块信息不存在，或者版块信息已经被删除，无法保存回帖信息！");
					}
				} catch (Exception e) {
					check = false;
					result.error(e);
					result.setUserMessage("根据版块ID查询版块信息发生异常，无法保存回帖信息！");
					logger.error("system query section info with id got an exceptin. id:" + subjectInfo.getMainSectionId(), e);
				}
			}
			
			if (check) {
				if( "根据权限".equals( sectionInfo.getReplyPublishAble() ) ){
					//那么要开始判断用户是否有对版内的主题权限进行回复
					try{
						hasPermission = userManagerService.hasPermission( currentPerson.getName(), "SECTION_REPLY_PUBLISH_" + subjectInfo.getSectionId() );
						if( !hasPermission ){
							check = false;
							result.error( new Exception("用户没有版块["+ sectionInfo.getSectionName() +"]中主题的回复权限！"));
							result.setUserMessage("用户没有版块["+ sectionInfo.getSectionName() +"]中主题的回复权限，无法保存主题信息！");
						}
					}catch( Exception e ){
						check = false;
						result.error( new Exception("系统在判断用户是否有版块["+ sectionInfo.getSectionName() +"]中主题的回复权限时发生异常！"));
						result.setUserMessage("系统在判断用户是否有版块["+ sectionInfo.getSectionName() +"]中主题的回复权限时发生异常，无法保存主题信息！");
						logger.error( "system query user has able permission 'SECTION_REPLY_PUBLISH_" + subjectInfo.getSectionId()+"' got an exception.", e );
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
					result.error( new Exception("论坛信息不存在，或者论坛信息已经被删除！"));
					result.setUserMessage("论坛信息不存在，或者论坛信息已经被删除，无法保存回帖信息！");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("根据论坛ID查询论坛信息发生异常，无法保存回帖信息！");
				logger.error("system query forum info with id got an exceptin. id:" + subjectInfo.getForumId(), e);
			}
		}
		if (check) {
			if( "根据权限".equals( forumInfo.getReplyPublishAble() ) ){
				//那么要开始判断用户是否有对版内的主题权限进行回复
				try{
					hasPermission = userManagerService.hasPermission( currentPerson.getName(), "FORUM_REPLY_PUBLISH_" + subjectInfo.getForumId() );
					if( !hasPermission ){
						check = false;
						result.error( new Exception("用户没有论坛["+ subjectInfo.getForumName() +"]中主题的回复权限！"));
						result.setUserMessage("用户没有论坛["+ subjectInfo.getForumName() +"]中主题的回复权限，无法保存主题信息！");
					}
				}catch( Exception e ){
					check = false;
					result.error( new Exception("系统在判断用户是否有论坛["+ subjectInfo.getForumName() +"]中主题的回复权限时发生异常！"));
					result.setUserMessage("系统在判断用户是否有论坛["+ subjectInfo.getForumName() +"]中主题的回复权限时发生异常，无法保存主题信息！");
					logger.error( "system query user has able permission 'FORUM_REPLY_PUBLISH_" + subjectInfo.getForumId()+"' got an exception.", e );
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
			replyInfo = new BBSReplyInfo();
			try {
				replyInfo = wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "将传入的参数转换为回贴对象发生异常，无法保存主题信息！" );
				logger.error( "system copy wrap into reply info object got an exceptin. " , e );
			}
		}
		if( check ){
			try {
				replyInfo.setMachineName( wrapIn.getReplyMachineName() );
				replyInfo.setSystemType( wrapIn.getReplySystemName() );
				replyInfo = replyInfoService.save( replyInfo );
				result.setUserMessage( replyInfo.getId() );
				result.setData( new WrapOutId(replyInfo.getId()));
				operationRecordService.replyOperation( currentPerson.getName(), replyInfo, "CREATE", hostIp, hostName );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "保存主题信息到数据库时发生异常！" );
				logger.error( "system save bbsreply info object got an exceptin. " , e );
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
				result.error( new Exception("传入的参数为空，无法根据ID删除回贴！" ) );
				result.setUserMessage( "传入的参数为空，无法根据ID删除回贴！" );
			}
		}		
		//判断主题信息是否存在
		if( check ){
			try {
				replyInfo = replyInfoService.get( id );
			} catch (Exception e) {
				check = false;
				result.error( new Exception("根据ID获取回贴信息时发生异常！" ) );
				result.setUserMessage( "根据ID获取回贴信息时发生异常！" );
				logger.error( "system query reply info with id got an exceptin.", e );
			}
		}		
		if( check ){
			if( replyInfo == null ){
				check = false;
				result.error( new Exception("根据ID未能获取到任何回贴信息！" ) );
				result.setUserMessage( "根据ID未能获取到任何回贴信息！" );
			}
		}		
		try {
			replyInfoService.delete( id );
			result.setData( new WrapOutId(id) );
			operationRecordService.replyOperation( currentPerson.getName(), replyInfo, "DELETE", hostIp, hostName );
		} catch (Exception e) {
			check = false;
			result.error( new Exception("根据ID删除回贴信息时发生异常！" ) );
			result.setUserMessage( "根据ID删除回贴信息时发生异常！" );
			logger.error( "system delete reply info with id got an exceptin.", e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe( value = "列示我发表的回贴,下一页.", response = WrapOutReplyInfo.class, request = WrapInFilter.class )
	@PUT
	@Path( "my/list/page/{page}/count/{count}" )
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response listMyReplyForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutReplyInfo>> result = new ActionResult<>();
		List<WrapOutReplyInfo> wraps = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		Boolean check = true;
		
		EffectivePerson currentPerson = this.effectivePerson(request);
		
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
				total = replyInfoService.countReplyByUserName( currentPerson.getName() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "根据ID信息查询回复信息时发生异常！" );
				logger.error( "system query my reply info got an exceptin.", e );
			}
		}
		if( check ){
			if( total > 0 ){
				try{
					replyInfoList = replyInfoService.listReplyByUserNameForPage( currentPerson.getName(), page * count );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "根据ID信息查询回复信息时发生异常！" );
					logger.error( "system query my reply info got an exceptin.", e );
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
}