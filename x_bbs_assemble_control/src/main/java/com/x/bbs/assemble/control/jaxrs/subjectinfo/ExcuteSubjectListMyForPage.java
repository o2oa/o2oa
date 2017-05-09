package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectFilterException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectWrapOutException;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteSubjectListMyForPage extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectListMyForPage.class );
	
	protected ActionResult<List<WrapOutSubjectInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilter wrapIn, Integer page, Integer count ) throws Exception {
		ActionResult<List<WrapOutSubjectInfo>> result = new ActionResult<>();
		List<WrapOutSubjectInfo> wraps = new ArrayList<>();
		List<BBSSubjectInfo> subjectInfoList = null;
		List<BBSSubjectInfo> subjectInfoList_out = new ArrayList<BBSSubjectInfo>();
		Long total = 0L;
		Boolean check = true;

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
		if( check ){
			try{
				total = subjectInfoServiceAdv.countUserSubjectForPage( wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getNeedPicture(), wrapIn.getWithTopSubject(), effectivePerson.getName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectFilterException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( total > 0 ){
				try{
					subjectInfoList = subjectInfoServiceAdv.listUserSubjectForPage( wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getNeedPicture(), wrapIn.getWithTopSubject(), page*count, effectivePerson.getName() );
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
			for( int i=0; subjectInfoList != null && i< subjectInfoList.size(); i++ ){
				if( i < subjectInfoList.size() && i >= startIndex && i < endIndex ){
					subjectInfoList_out.add( subjectInfoList.get( i ) );
				}
			}
			if( subjectInfoList_out != null && !subjectInfoList_out.isEmpty() ){
				try {
					wraps = WrapTools.subjectInfo_wrapout_copier.copy( subjectInfoList_out );
					result.setData( wraps );
					result.setCount( total );
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