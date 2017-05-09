package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectOperationException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectQueryByIdException;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteSubjectSetRecommendToBBSIndex extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectSetRecommendToBBSIndex.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
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
			if( subjectInfo == null ){
				check = false;
				Exception exception = new SubjectNotExistsException( id );
				result.error( exception );
			}
		}
		
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.recommendToBBSIndex( id, true, effectivePerson.getName() );
				result.setData( new WrapOutId(id) );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectOperationException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}