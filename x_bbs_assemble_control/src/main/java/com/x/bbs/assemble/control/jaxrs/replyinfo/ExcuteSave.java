package com.x.bbs.assemble.control.jaxrs.replyinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ForumInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ForumInsufficientPermissionException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ForumPermissionsCheckException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyContentEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplySubjectIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.SectionInsufficientPermissionsException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.SectionNotExistsException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.SectionPermissionsCheckException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.SubjectLockedException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.SubjectNotExistsException;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteSave extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInReplyInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
		BBSReplyInfo replyInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSForumInfo forumInfo = null;
		Boolean hasPermission = false;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		
		if( check ){
			wrapIn.setHostIp( request.getRemoteHost() );
			if( wrapIn.getSubjectId() == null ){
				check = false;
				Exception exception = new ReplySubjectIdEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			if( wrapIn.getContent() == null ){
				check = false;
				Exception exception = new ReplyContentEmptyException();
				result.error( exception );
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
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyInfoProcessException( e, "根据指定ID查询主题信息时发生异常.ID:" + wrapIn.getSubjectId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		//判断主题是否允许用户回复，已锁定的主题不允许用户进行回复
		if (check) {
			if( "已锁定".equals( subjectInfo.getSubjectStatus() )){
				check = false;
				Exception exception = new SubjectLockedException( wrapIn.getSubjectId() );
				result.error( exception );
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
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + subjectInfo.getSectionId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if( "根据权限".equals( sectionInfo.getReplyPublishAble() ) ){
				//那么要开始判断用户是否有对版内的主题权限进行回复
				try{
					hasPermission = userManagerService.hasPermission( effectivePerson.getName(), "SECTION_REPLY_PUBLISH_" + subjectInfo.getSectionId() );
					if( !hasPermission ){
						check = false;
						Exception exception = new SectionInsufficientPermissionsException( sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH" );
						result.error( exception );
					}
				}catch( Exception e ){
					check = false;
					Exception exception = new SectionPermissionsCheckException( e, effectivePerson.getName(), sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
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
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ReplyInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + subjectInfo.getMainSectionId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				if( "根据权限".equals( sectionInfo.getReplyPublishAble() ) ){
					//那么要开始判断用户是否有对版内的主题权限进行回复
					try{
						hasPermission = userManagerService.hasPermission( effectivePerson.getName(), "SECTION_REPLY_PUBLISH_" + subjectInfo.getMainSectionId() );
						if( !hasPermission ){
							check = false;
							Exception exception = new SectionInsufficientPermissionsException( sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH" );
							result.error( exception );
							//logger.error( e, effectivePerson, request, null);
						}
					}catch( Exception e ){
						check = false;
						Exception exception = new SectionPermissionsCheckException( e, effectivePerson.getName(), sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
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
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyInfoProcessException( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + subjectInfo.getForumId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if( "根据权限".equals( forumInfo.getReplyPublishAble() ) ){
				//那么要开始判断用户是否有对版内的主题权限进行回复
				try{
					hasPermission = userManagerService.hasPermission( effectivePerson.getName(), "FORUM_REPLY_PUBLISH_" + subjectInfo.getForumId() );
					if( !hasPermission ){
						check = false;
						Exception exception = new ForumInsufficientPermissionException( subjectInfo.getForumName(), "FORUM_REPLY_PUBLISH" );
						result.error( exception );
					}
				}catch( Exception e ){
					check = false;
					Exception exception = new ForumPermissionsCheckException( e, effectivePerson.getName(), subjectInfo.getForumName(), "FORUM_REPLY_PUBLISH" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
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
			wrapIn.setCreatorName( effectivePerson.getName() );
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
				replyInfo = WrapTools.replyInfo_wrapin_copier.copy( wrapIn );
				if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					replyInfo.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyInfoProcessException( e, "将用户传入的信息转换为一个回复信息对象时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				replyInfo.setMachineName( wrapIn.getReplyMachineName() );
				replyInfo.setSystemType( wrapIn.getReplySystemName() );
				replyInfo = replyInfoService.save( replyInfo );
				result.setData( new WrapOutId(replyInfo.getId()));
				operationRecordService.replyOperation( effectivePerson.getName(), replyInfo, "CREATE", hostIp, hostName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyInfoProcessException( e, "系统在保存回复信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}