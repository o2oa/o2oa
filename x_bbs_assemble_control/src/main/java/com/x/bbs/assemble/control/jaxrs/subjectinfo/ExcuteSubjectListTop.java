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
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SectionNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectFilterException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectWrapOutException;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteSubjectListTop extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectListTop.class );
	
	protected ActionResult<List<WrapOutSubjectInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String sectionId ) throws Exception {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		List<WrapOutSubjectInfo> wraps = new ArrayList<>();
		List<BBSSubjectInfo> subjectInfoList = null;
		BBSSectionInfo sectionInfo = null;
		List<String> viewSectionIds = null;
		Boolean check = true;
		
		if (check) {
			if ( sectionId == null || sectionId.isEmpty() ) {
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}
		if (check) {// 查询版块信息是否存在
			try {
				sectionInfo = sectionInfoServiceAdv.get( sectionId );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + sectionId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (sectionInfo == null) {
				check = false;
				Exception exception = new SectionNotExistsException( sectionId );
				result.error( exception );
			}
		}
		if( check ){
			viewSectionIds = getViewableSectionIds( request, result, effectivePerson );
		}
		if (check) {
			try {
				subjectInfoList = subjectInfoServiceAdv.listAllTopSubject( sectionInfo, null, viewSectionIds );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectFilterException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if ( subjectInfoList != null && !subjectInfoList.isEmpty() ) {
				try {
					wraps = WrapTools.subjectInfo_wrapout_copier.copy(subjectInfoList);
					SortTools.desc(wraps, true, "latestReplyTime");
					result.setData(wraps);
					result.setCount(Long.parseLong(wraps.size() + ""));
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