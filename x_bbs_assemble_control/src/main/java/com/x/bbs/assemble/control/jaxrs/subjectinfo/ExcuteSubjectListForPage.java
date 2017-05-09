package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SectionNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectFilterException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectWrapOutException;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteSubjectListForPage extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectListForPage.class );
	
	protected ActionResult<List<WrapOutSubjectInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilter wrapIn, Integer page, Integer count ) throws Exception {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		List<WrapOutSubjectInfo> wraps_nonTop = new ArrayList<>();
		List<WrapOutSubjectInfo> wraps_top = new ArrayList<>();
		List<WrapOutSubjectInfo> wraps_out = new ArrayList<WrapOutSubjectInfo>();
		BBSSectionInfo sectionInfo = null;
		List<BBSSubjectInfo> subjectInfoList = null;
		List<BBSSubjectInfo> subjectInfoList_top = null;
		List<String> viewSectionIds = new ArrayList<String>();
		Integer selectTotal = 0;
		Long total = 0L;
		Integer topTotal = 0;
		Boolean check = true;

		if( wrapIn.getSectionId() != null && !wrapIn.getSectionId().isEmpty() ){
			if (check) {
				try {
					sectionInfo = sectionInfoServiceAdv.get( wrapIn.getSectionId() );
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + wrapIn.getSectionId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if (check) {
				if ( sectionInfo == null ) {
					check = false;
					Exception exception = new SectionNotExistsException( wrapIn.getSectionId() );
					result.error( exception );
				}
			}
		}
		
		if( check ){
			viewSectionIds = getViewableSectionIds( request, result, effectivePerson );
		}
		if( check ){
			if( page == null ){
				page = 1;
			}
		}
		if( check ){
			if( count == null ){
				count = 20;
			}
		}
		//查询的最大条目数
		selectTotal = page * count;
		
		Boolean selectTopInSection = null;//默认是将版块内所有的置顶和非置顶贴全部查出
		//查询出所有的置顶贴
		if ( check && wrapIn != null && wrapIn.getWithTopSubject() != null && wrapIn.getWithTopSubject() ) {
			selectTopInSection = false; //如果已经查询过置顶贴,那么查询版块列表时就不用查询置顶贴了.
			try {
				subjectInfoList_top = subjectInfoServiceAdv.listAllTopSubject( sectionInfo, wrapIn.getCreatorName(), viewSectionIds );
				if( subjectInfoList_top != null ){
					topTotal = subjectInfoList_top.size();
					try {
						wraps_top = WrapTools.subjectInfo_wrapout_copier.copy( subjectInfoList_top );
						SortTools.desc( wraps_top, "latestReplyTime" );
					} catch (Exception e) {
						Exception exception = new SubjectWrapOutException( e );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectFilterException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( wrapIn.getWithTopSubject() != null && !wrapIn.getWithTopSubject() ){
			selectTopInSection = false; //不查询置顶贴
		}
		
		if( check ){
			if( wraps_top != null ){
				if( selectTotal < wraps_top.size() ){
					selectTotal = 0;
				}else{
					selectTotal = selectTotal - wraps_top.size();
				}
			}
		}
		if( check ){
			if( selectTotal > 0 ){
				try{
					total = subjectInfoServiceAdv.countSubjectInSectionForPage( wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getCreatorName(), wrapIn.getNeedPicture(), selectTopInSection, viewSectionIds );
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectFilterException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( selectTotal > 0 && total > 0 ){
				try{
					subjectInfoList = subjectInfoServiceAdv.listSubjectInSectionForPage( wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getCreatorName(), wrapIn.getNeedPicture(), selectTopInSection, selectTotal, viewSectionIds );
					if( subjectInfoList != null ){
						try {
							wraps_nonTop = WrapTools.subjectInfo_wrapout_copier.copy( subjectInfoList );
							SortTools.desc( wraps_nonTop, "latestReplyTime" );
						} catch (Exception e) {
							Exception exception = new SubjectWrapOutException( e );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectFilterException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
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
			int i = 0;
			for( ; wraps_top != null && i< wraps_top.size(); i++ ){
				if( i >= startIndex && i < endIndex ){
					wraps_out.add( wraps_top.get( i ) );
				}
			}
			for( int j=0; wraps_nonTop != null && j< wraps_nonTop.size(); j++ ){
				if( i+j >= startIndex && i+j < endIndex ){
					wraps_out.add( wraps_nonTop.get( j ) );
				}
			}
			if( wraps_out != null && !wraps_out.isEmpty() ){
				try {
					result.setData( wraps_out );
					result.setCount( total + topTotal );
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectWrapOutException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

}