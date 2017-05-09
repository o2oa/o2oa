package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.AttachmentIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.AttachmentNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectInfoProcessException;
import com.x.bbs.entity.BBSSubjectAttachment;

public class ExcuteAttachmentGet extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteAttachmentGet.class );
	
	protected ActionResult<WrapOutSubjectAttachment> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutSubjectAttachment> result = new ActionResult<>();
		WrapOutSubjectAttachment wrap = null;
		BBSSubjectAttachment attachmentInfo = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new AttachmentIdEmptyException();
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				attachmentInfo = subjectInfoServiceAdv.getAttachment(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectInfoProcessException(e, "根据指定ID查询附件信息时发生异常.ID:" + id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}

		if (check) {
			if (attachmentInfo != null) {
				try {
					wrap = WrapTools.subjectAttachment_wrapout_copier.copy(attachmentInfo);
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					Exception exception = new SubjectInfoProcessException( e, "将查询结果转换为可以输出的数据信息时发生异常." );
					result.error(exception);
					logger.error(exception, effectivePerson, request, null);
				}
			} else {
				Exception exception = new AttachmentNotExistsException(id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		return result;
	}

}