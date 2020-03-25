package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectIdEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectOperation;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectQueryById;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionSubjectAcceptReply extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSubjectAcceptReply.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id, String replyId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		BBSSubjectInfo subjectInfo = null;
		Boolean check = true;
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectQueryById(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (subjectInfo == null) {
				check = false;
				Exception exception = new ExceptionSubjectNotExists(id);
				result.error(exception);
			}
		}
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.acceptReply(id, replyId, effectivePerson.getDistinguishedName());

				Wo wo = new Wo();
				wo.setId( id );
				result.setData( wo );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectOperation(e, "用户确认主题回复时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}