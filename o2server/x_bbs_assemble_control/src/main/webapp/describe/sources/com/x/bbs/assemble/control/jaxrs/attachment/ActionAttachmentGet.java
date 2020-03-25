package com.x.bbs.assemble.control.jaxrs.attachment;

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
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionAttachmentIdEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionAttachmentNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectInfoProcess;
import com.x.bbs.entity.BBSSubjectAttachment;

public class ActionAttachmentGet extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAttachmentGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		BBSSubjectAttachment attachmentInfo = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionAttachmentIdEmpty();
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				attachmentInfo = subjectInfoServiceAdv.getAttachment(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectInfoProcess(e, "根据指定ID查询附件信息时发生异常.ID:" + id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}

		if (check) {
			if (attachmentInfo != null) {
				try {
					wrap = Wo.copier.copy(attachmentInfo);
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectInfoProcess( e, "将查询结果转换为可以输出的数据信息时发生异常." );
					result.error(exception);
					logger.error(exception, effectivePerson, request, null);
				}
			} else {
				Exception exception = new ExceptionAttachmentNotExists(id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends BBSSubjectAttachment{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< BBSSubjectAttachment, Wo > copier = WrapCopierFactory.wo( BBSSubjectAttachment.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}