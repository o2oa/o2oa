package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectContentQueryByIdException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectQueryByIdException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectWrapOutException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.VoteOptionBinaryQueryByIdException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.VoteOptionListByIdException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.VoteResultQueryByIdException;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSVoteOption;

public class ExcuteSubjectGet extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectGet.class );
	
	protected ActionResult<WrapOutSubjectInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutSubjectInfo> result = new ActionResult<>();
		List<WrapOutSubjectAttachment> wrapSubjectAttachmentList = null;
		List<BBSSubjectAttachment> subjectAttachmentList = null;
		String subjectVoteResult = null;
		String optionBinaryContent = null;
		List<BBSVoteOption> voteOptionList = null;
		List<WrapOutBBSVoteOption> wrapOutSubjectVoteOptionList = null;
		WrapOutSubjectInfo wrap = null;
		BBSSubjectInfo subjectInfo = null;
		String subjectContent = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SubjectIdEmptyException();
				result.error( exception );
			}
		}
		
		//查询版块信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if ( subjectInfo == null ) {
				check = false;
				Exception exception = new SubjectNotExistsException( id );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}else{//查到了主题信息
				try {
					wrap = WrapTools.subjectInfo_wrapout_copier.copy( subjectInfo );
					//根据附件ID列表查询附件信息
					if( subjectInfo.getAttachmentList() != null && subjectInfo.getAttachmentList().size() > 0 ){
						subjectAttachmentList = subjectInfoServiceAdv.listAttachmentByIds( subjectInfo.getAttachmentList() );
						if( subjectAttachmentList != null && subjectAttachmentList.size() > 0 ){
							wrapSubjectAttachmentList = WrapTools.subjectAttachment_wrapout_copier.copy( subjectAttachmentList );
							wrap.setSubjectAttachmentList( wrapSubjectAttachmentList );
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectWrapOutException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}			
		}
		if (check) {
			if( wrap != null ){
				//填充主题的内容信息
				try {
					subjectContent = subjectInfoServiceAdv.getSubjectContent( id );
					if( subjectContent != null ){
						wrap.setContent( subjectContent );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectContentQueryByIdException( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if( wrap != null ){//获取该主题的投票选项
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
				wrap.setVoteOptionList( wrapOutSubjectVoteOptionList );
			}
		}
		if ( check ) {
			if( wrap != null ){
				//获取该主题的投票结果
				try {
					subjectVoteResult = subjectVoteService.getVoteResult( id );
					wrap.setVoteResult( subjectVoteResult );
				} catch (Exception e) {
					check = false;
					Exception exception = new VoteResultQueryByIdException( e, id );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		result.setData( wrap );
		return result;
	}

}