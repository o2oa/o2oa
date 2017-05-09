package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SectionNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SectionSubjectTypeEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SectionSubjectTypeInvalidException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SectionTypeCategoryEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SectionTypeCategoryInvalidException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectPropertyEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectSaveException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectWrapInException;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteSubjectSave extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInSubjectInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSSectionInfo sectionInfo = null;
		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		
		if( check ){
			wrapIn.setHostIp( request.getRemoteHost() );
			if( wrapIn.getTitle() == null ){
				check = false;
				Exception exception = new SubjectPropertyEmptyException( "主题标题" );
				result.error( exception );
			}
		}
		
		if( check ){
			if( wrapIn.getType() == null ){
				check = false;
				Exception exception = new SubjectPropertyEmptyException( "主题类别" );
				result.error( exception );
			}
		}
		
		if( check ){
			if( wrapIn.getContent() == null ){
				check = false;
				Exception exception = new SubjectPropertyEmptyException( "主题内容" );
				result.error( exception );
			}
		}
		if( check ){
			if( wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty() ){
				check = false;
				Exception exception = new SubjectPropertyEmptyException( "所属版块ID" );
				result.error( exception );
			}
		}
		
		//查询版块信息是否存在
		if( check ){
			try {
				sectionInfo = sectionInfoServiceAdv.get( wrapIn.getSectionId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + wrapIn.getSectionId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				Exception exception = new SectionNotExistsException( wrapIn.getSectionId() );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getTypeCategory() == null || !wrapIn.getTypeCategory().isEmpty() ){
				wrapIn.setTypeCategory( "信息" );
			}else{
				if( sectionInfo.getTypeCategory() == null || sectionInfo.getTypeCategory().isEmpty() ){
					check = false;
					Exception exception = new SectionTypeCategoryEmptyException( wrapIn.getSectionId() );
					result.error( exception );
				}else{
					//判断TypeCategory是否合法
					String[] categories = sectionInfo.getTypeCategory().split("\\|");
					Boolean categoryValid = false;
					if( categories != null && categories.length > 0 ){
						for( String category : categories ){
							if( category.equals( wrapIn.getTypeCategory() )){
								categoryValid = true;
							}
						}
						if( !categoryValid ){
							check = false;
							Exception exception = new SectionTypeCategoryInvalidException( categories );
							result.error( exception );
						}
					}
				}
			}				
		}
		if( check ){
			if( wrapIn.getType() == null || wrapIn.getType().isEmpty() ){
				wrapIn.setType( "未知类别" );
			}else{
				if( sectionInfo.getSubjectType() == null || sectionInfo.getSubjectType().isEmpty() ){
					check = false;
					Exception exception = new SectionSubjectTypeEmptyException( wrapIn.getSectionId() );
					result.error( exception );
				}else{
					//判断Type是否合法
					String[] types = sectionInfo.getSubjectType().split("\\|");
					Boolean typeValid = false;
					if( types != null && types.length > 0 ){
						for( String type : types ){
							if( type.equals( wrapIn.getType() )){
								typeValid = true;
							}
						}
						if( !typeValid ){
							check = false;
							Exception exception = new SectionSubjectTypeInvalidException( types );
							result.error( exception );
						}
					}
				}
			}				
		}
		if( check ){
			try {
				subjectInfo = WrapTools.subjectInfo_wrapin_copier.copy( wrapIn );
				if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					subjectInfo.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectWrapInException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			subjectInfo.setForumId( sectionInfo.getForumId() );
			subjectInfo.setForumName( sectionInfo.getForumName() );
			subjectInfo.setMainSectionId(sectionInfo.getMainSectionId());
			subjectInfo.setMainSectionName(sectionInfo.getMainSectionName());
			subjectInfo.setSectionId(sectionInfo.getId());
			subjectInfo.setSectionName(sectionInfo.getSectionName());
			subjectInfo.setCreatorName( effectivePerson.getName() );
			subjectInfo.setLatestReplyTime( new Date() );
			subjectInfo.setTypeCategory( wrapIn.getTypeCategory() );
			subjectInfo.setType( wrapIn.getType() );
			subjectInfo.setTitle( subjectInfo.getTitle().trim() );
		}
		
		if( check ){
			subjectInfo.setMachineName( wrapIn.getSubjectMachineName() );
			subjectInfo.setSystemType( wrapIn.getSubjectSystemName() );
			try {
				subjectInfo = subjectInfoServiceAdv.save( subjectInfo, wrapIn.getContent() );
				result.setData( new WrapOutId(subjectInfo.getId()));
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectSaveException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}