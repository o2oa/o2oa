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
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectFilterException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectWrapOutException;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteSubjectListRecommendedForBBSIndex extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectListRecommendedForBBSIndex.class );
	
	protected ActionResult<List<WrapOutSubjectInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, Integer count ) throws Exception {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		List<WrapOutSubjectInfo> wraps = new ArrayList<>();
		List<BBSSubjectInfo> subjectInfoList = null;
		List<String> viewSectionIds = new ArrayList<String>();		
		Boolean check = true;		
		if( check ){
			if( count == null || count <= 0 ){
				count = 10;
			}
		}
		if( check ){
			viewSectionIds = getViewableSectionIds( request, result, effectivePerson );
		}
		if( check ){
			try {
				subjectInfoList = subjectInfoServiceAdv.listRecommendedSubjectForBBSIndex( viewSectionIds, count );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectFilterException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( subjectInfoList != null && !subjectInfoList.isEmpty() ){
				try {
					wraps = WrapTools.subjectInfo_wrapout_copier.copy( subjectInfoList );
					SortTools.desc( wraps, true, "updateTime" );
					result.setData( wraps );
					result.setCount( Long.parseLong( wraps.size() + "" ) );
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