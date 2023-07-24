package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectQueryById;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionAttachmentListBySubjectId extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAttachmentListBySubjectId.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		List<Wo> wrapOutSubjectAttachmentList = null;
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
				wrapOutSubjectAttachmentList = Wo.copier.copy( fileInfoList );
			} else {
				Exception exception = new ExceptionSubjectNotExists(id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
			if (wrapOutSubjectAttachmentList == null) {
				wrapOutSubjectAttachmentList = new ArrayList<Wo>();
			}
			result.setData(wrapOutSubjectAttachmentList);
		} catch (Throwable th) {
			Exception exception = new ExceptionSubjectQueryById(th, id);
			result.error(exception);
			logger.error(exception, effectivePerson, request, null);
		}
		return result;
	}

	public static class Wo extends BBSSubjectAttachment{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< BBSSubjectAttachment, Wo > copier = WrapCopierFactory.wo( BBSSubjectAttachment.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}