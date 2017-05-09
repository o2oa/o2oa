package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectContentQueryByIdException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectFilterException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectViewException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectWrapOutException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.VoteOptionBinaryQueryByIdException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.VoteOptionListByIdException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.VoteResultQueryByIdException;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSVoteOption;

public class ExcuteSubjectView extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectView.class );
	
	protected ActionResult<WrapOutNearSubjectInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutNearSubjectInfo> result = new ActionResult<>();
		List<WrapOutSubjectAttachment> wrapSubjectAttachmentList = null;
		List<BBSSubjectAttachment> subjectAttachmentList = null;
		WrapOutNearSubjectInfo wrapOutNearSubjectInfo = new WrapOutNearSubjectInfo();
		WrapOutSubjectInfo lastSubject = null;
		WrapOutSubjectInfo currentSubject = null;
		WrapOutSubjectInfo nextSubject = null;
		BBSSubjectInfo subjectInfo = null;
		String subjectVoteResult = null;
		String optionBinaryContent = null;
		List<BBSVoteOption> voteOptionList = null;
		List<WrapOutBBSVoteOption> wrapOutSubjectVoteOptionList = null;
		String subjectContent = null;
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SubjectIdEmptyException();
				result.error( exception );
			}
		}
		if (check) {//查询版块信息是否存在
			try {
				subjectInfo = subjectInfoServiceAdv.view( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectViewException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if ( subjectInfo == null ) {
				check = false;
				Exception exception = new SubjectNotExistsException( id );
				result.error( exception );
			}else{//查到了主题信息
				try {
					currentSubject = WrapTools.subjectInfo_wrapout_copier.copy( subjectInfo );
					//根据附件ID列表查询附件信息
					if( currentSubject.getAttachmentList() != null && currentSubject.getAttachmentList().size() > 0 ){
						subjectAttachmentList = subjectInfoServiceAdv.listAttachmentByIds( currentSubject.getAttachmentList() );
						if( subjectAttachmentList != null && subjectAttachmentList.size() > 0 ){
							wrapSubjectAttachmentList = WrapTools.subjectAttachment_wrapout_copier.copy( subjectAttachmentList );
							currentSubject.setSubjectAttachmentList( wrapSubjectAttachmentList );
						}
					}
					wrapOutNearSubjectInfo.setCurrentSubject( currentSubject );
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectWrapOutException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}			
		}
		if (check) {
			if( wrapOutNearSubjectInfo.getCurrentSubject() != null ){
				currentSubject = wrapOutNearSubjectInfo.getCurrentSubject();
				//填充主题的内容信息
				try {
					subjectContent = subjectInfoServiceAdv.getSubjectContent( id );
					if( subjectContent != null ){
						currentSubject.setContent( subjectContent );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectContentQueryByIdException( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		//开始查询上一个主题的信息
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.getLastSubject( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectFilterException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if( subjectInfo != null ){
				lastSubject = new WrapOutSubjectInfo();
				lastSubject.setId( subjectInfo.getId() );
				lastSubject.setTitle( subjectInfo.getTitle() );
				wrapOutNearSubjectInfo.setLastSubject( lastSubject );
			}
		}
		//开始查询下一个主题的信息
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.getNextSubject( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectFilterException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if( subjectInfo != null ){
				nextSubject = new WrapOutSubjectInfo();
				nextSubject.setId( subjectInfo.getId() );
				nextSubject.setTitle( subjectInfo.getTitle() );
				wrapOutNearSubjectInfo.setNextSubject( nextSubject );
			}
		}
		if (check) {
			if( currentSubject != null ){//获取该主题的投票选项
				try {
					voteOptionList = subjectVoteService.listVoteOption( id );
				} catch (Exception e) {
					check = false;
					Exception exception = new VoteOptionListByIdException( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if( voteOptionList != null  && !voteOptionList.isEmpty() ){
				try {
					wrapOutSubjectVoteOptionList = WrapTools.voteOption_wrapout_copier.copy( voteOptionList );
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectWrapOutException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if( wrapOutSubjectVoteOptionList != null  && !wrapOutSubjectVoteOptionList.isEmpty() ){
				for( WrapOutBBSVoteOption option : wrapOutSubjectVoteOptionList ){
					//获取图片编码
					try {
						optionBinaryContent = subjectVoteService.getOptionBinaryContent( option.getId() );
						option.setOptionBinary( optionBinaryContent );
					} catch (Exception e) {
						check = false;
						Exception exception = new VoteOptionBinaryQueryByIdException( e, option.getId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		if (check) {
			if( wrapOutSubjectVoteOptionList != null  && !wrapOutSubjectVoteOptionList.isEmpty() ){
				currentSubject.setVoteOptionList( wrapOutSubjectVoteOptionList );
			}
		}
		if ( check ) {
			if( currentSubject != null ){
				//获取该主题的投票结果
				try {
					subjectVoteResult = subjectVoteService.getVoteResult( id );
					currentSubject.setVoteResult( subjectVoteResult );
				} catch (Exception e) {
					check = false;
					Exception exception = new VoteResultQueryByIdException( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		result.setData( wrapOutNearSubjectInfo );
		return result;
	}

}