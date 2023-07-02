package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyIdEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyNotExists;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionWrapInConvert;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionAcceptReply extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionAcceptReply.class);

	protected ActionResult<WrapOutId> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
		BBSReplyInfo replyInfo = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (wrapIn.getId() == null) {
				check = false;
				Exception exception = new ExceptionReplyIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				replyInfo = replyInfoService.get(wrapIn.getId());
				if (replyInfo == null) {
					check = false;
					Exception exception = new ExceptionReplyNotExists(wrapIn.getId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "根据指定ID查询回复信息时发生异常.ID:" + wrapIn.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		// 查询关联的主题信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoService.get(replyInfo.getSubjectId());
				if (subjectInfo == null) {
					check = false;
					Exception exception = new ExceptionSubjectNotExists(wrapIn.getSubjectId());
					result.error(exception);
				} else {
					subjectInfoService.acceptReply(subjectInfo.getId(), replyInfo.getId());
					result.setData(new WrapOutId(replyInfo.getId()));

					CacheManager.notify( BBSSubjectInfo.class );

				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "根据指定ID查询主题信息时发生异常.ID:" + wrapIn.getSubjectId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends BBSReplyInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Wi, BBSReplyInfo> copier = WrapCopierFactory.wi(Wi.class, BBSReplyInfo.class, null,
				JpaObject.FieldsUnmodify);

		private String replyMachineName = "PC";

		private String replySystemName = "Windows";

		private String userHostIp = "";

		public String getReplyMachineName() {
			return replyMachineName;
		}

		public void setReplyMachineName(String replyMachineName) {
			this.replyMachineName = replyMachineName;
		}

		public String getReplySystemName() {
			return replySystemName;
		}

		public void setReplySystemName(String replySystemName) {
			this.replySystemName = replySystemName;
		}

		public String getUserHostIp() {
			return userHostIp;
		}

		public void setUserHostIp(String userHostIp) {
			this.userHostIp = userHostIp;
		}

	}
}