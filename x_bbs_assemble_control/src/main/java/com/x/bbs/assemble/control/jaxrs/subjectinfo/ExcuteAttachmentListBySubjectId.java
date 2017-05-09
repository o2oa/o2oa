package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectQueryByIdException;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteAttachmentListBySubjectId extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteAttachmentListBySubjectId.class );
	
	protected ActionResult<List<WrapOutSubjectAttachment>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<WrapOutSubjectAttachment>> result = new ActionResult<List<WrapOutSubjectAttachment>>();
		List<WrapOutSubjectAttachment> wrapOutSubjectAttachmentList = null;
		List<BBSSubjectAttachment> fileInfoList = null;
		BBSSubjectInfo subjectInfo = null;
		try {
			subjectInfo = subjectInfoServiceAdv.get(id);
			if (subjectInfo != null) {
				if ( subjectInfo.getAttachmentList() != null && subjectInfo.getAttachmentList().size() > 0 ) {
					fileInfoList = subjectInfoServiceAdv.listAttachmentByIds( subjectInfo.getAttachmentList() );
				} else {
					fileInfoList = new ArrayList<BBSSubjectAttachment>();
				}
				wrapOutSubjectAttachmentList = WrapTools.subjectAttachment_wrapout_copier.copy( fileInfoList );
			} else {
				Exception exception = new SubjectNotExistsException(id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
			if (wrapOutSubjectAttachmentList == null) {
				wrapOutSubjectAttachmentList = new ArrayList<WrapOutSubjectAttachment>();
			}
			result.setData(wrapOutSubjectAttachmentList);
		} catch (Throwable th) {
			Exception exception = new SubjectQueryByIdException(th, id);
			result.error(exception);
			logger.error(exception, effectivePerson, request, null);
		}
		return result;
	}

}