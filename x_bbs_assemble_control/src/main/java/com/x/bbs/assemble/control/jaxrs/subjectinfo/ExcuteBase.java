package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.PublicSectionFilterException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectInfoProcessException;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectVoteService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;

public class ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectListForPage.class );
	
	protected BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	protected BBSSubjectInfoServiceAdv subjectInfoServiceAdv = new BBSSubjectInfoServiceAdv();
	protected BBSSubjectVoteService subjectVoteService = new BBSSubjectVoteService();
	protected BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	
	protected boolean isImage(BBSSubjectAttachment fileInfo) {
		if (fileInfo == null || fileInfo.getExtension() == null || fileInfo.getExtension().isEmpty()) {
			return false;
		}
		if ("jpg".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("png".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("jpeg".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("tiff".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("gif".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("bmp".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取用户可访问的所有版块ID列表
	 * @param request
	 * @param result
	 * @param currentPerson
	 * @return
	 */
	protected List<String> getViewableSectionIds( HttpServletRequest request, ActionResult<List<WrapOutSubjectInfo>> result, EffectivePerson currentPerson ) {
		List<BBSSectionInfo> sectionInfoList = null;
		List<BBSSectionInfo> subSectionInfoList = null;
		List<BBSPermissionInfo> permissonList = null;
		List<String> publicForumIds = null;
		List<String> publicSectionIds = null;
		List<String> viewforumIds = new ArrayList<String>();
		List<String> viewSectionIds = new ArrayList<String>();
		Boolean check = true;
		
		if( check ){
			permissonList = userManagerService.getUserPermissionInfoList( currentPerson.getName() );
		}
		if( check ){
			if( permissonList != null ){
				for( BBSPermissionInfo permissionInfo : permissonList ){
					if( "FORUM_VIEW".equals( permissionInfo.getPermissionType() ) && !viewforumIds.contains( permissionInfo.getForumId() )){
						viewforumIds.add( permissionInfo.getForumId() );
					}
					if( "SECTION_VIEW".equals( permissionInfo.getPermissionType() ) && !viewSectionIds.contains( permissionInfo.getSectionId() )){
						viewSectionIds.add( permissionInfo.getSectionId() );
					}
				}
			}
		}
		if( check ){
			try {
				publicForumIds = forumInfoServiceAdv.listAllPublicForumIds();
				if( publicForumIds != null ){
					for( String _id : publicForumIds ){
						if( !viewforumIds.contains( _id )){
							viewforumIds.add( _id );
						}
					}
				}
			} catch (Exception e) {
				check = false;
				logger.warn( "system query all public forum got an exceptin.", e );
			}
		}
		if( check ){
			try {
				publicSectionIds = sectionInfoServiceAdv.viewSectionByForumIds( viewforumIds, true );
			} catch (Exception e) {
				check = false;
				logger.warn( "system query all public section with forumIds got an exceptin." );
				Exception exception = new PublicSectionFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				sectionInfoList = sectionInfoServiceAdv.list( publicSectionIds );
				if( sectionInfoList != null ){
					for( BBSSectionInfo _sectionInfo : sectionInfoList ){
						if( !viewSectionIds.contains( _sectionInfo.getId() )){
							viewSectionIds.add( _sectionInfo.getId() );
						}
						if( "主板块".equals( _sectionInfo.getSectionLevel() ) ){
							subSectionInfoList = sectionInfoServiceAdv.listSubSectionByMainSectionId( _sectionInfo.getId() );
							if( subSectionInfoList != null ){
								for( BBSSectionInfo _subSectionInfo : subSectionInfoList ){
									if( !viewSectionIds.contains( _subSectionInfo.getId() )){
										viewSectionIds.add( _subSectionInfo.getId() );
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectInfoProcessException( e, "根据指定ID列表查询版块信息时发生异常." );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		return viewSectionIds;
	}
}
