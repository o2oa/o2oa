package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectIsNotVoteSubject;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectOperation;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectPropertyEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectQueryById;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionWrapInConvert;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionSubjectSubmitVoteResult extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSubjectSubmitVoteResult.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
			wrapIn.setHostIp(request.getRemoteHost());
			if ( wrapIn.getId() == null ) {
				check = false;
				Exception exception = new ExceptionSubjectPropertyEmpty("主题ID");
				result.error(exception);
			}
			if ( wrapIn.getOptionGroups() == null || wrapIn.getOptionGroups().isEmpty() ) {
				check = false;
				Exception exception = new ExceptionSubjectPropertyEmpty("用户投票选择");
				result.error(exception);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get(wrapIn.getId());
				if (subjectInfo == null) {
					check = false;
					Exception exception = new ExceptionSubjectNotExists(wrapIn.getId());
					result.error(exception);
				} else {
					Wo wo = new Wo();
					wo.setId(subjectInfo.getId());
					result.setData(wo);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectQueryById(e, wrapIn.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if ( "投票".equals(subjectInfo.getTypeCategory()) ) {
				try {
					subjectVoteService.submitVoteResult(effectivePerson, subjectInfo, wrapIn.getOptionGroups());
					ApplicationCache.notify( BBSSubjectInfo.class );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectOperation(e, "系统在保存投票选项信息时发生异常!");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			} else {
				check = false;
				Exception exception = new ExceptionSubjectIsNotVoteSubject(wrapIn.getId());
				result.error(exception);
			}
		}

		return result;
	}

	public static class Wi extends BBSSubjectInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Wi, BBSSubjectInfo> copier = WrapCopierFactory.wi(Wi.class, BBSSubjectInfo.class, null,
				JpaObject.FieldsUnmodify);

		private String subjectMachineName = "PC";

		private String subjectSystemName = "Windows";

		private String userHostIp = "";

		private String content = "";

		@FieldDescribe("投票选项组集合")
		private List<WiVoteOptionGroup> optionGroups = null;

		public String getSubjectMachineName() {
			return subjectMachineName;
		}

		public void setSubjectMachineName(String subjectMachineName) {
			this.subjectMachineName = subjectMachineName;
		}

		public String getSubjectSystemName() {
			return subjectSystemName;
		}

		public void setSubjectSystemName(String subjectSystemName) {
			this.subjectSystemName = subjectSystemName;
		}

		public String getUserHostIp() {
			return userHostIp;
		}

		public void setUserHostIp(String userHostIp) {
			this.userHostIp = userHostIp;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public List<WiVoteOptionGroup> getOptionGroups() {
			return optionGroups;
		}

		public void setOptionGroups(List<WiVoteOptionGroup> optionGroups) {
			this.optionGroups = optionGroups;
		}

	}

	public static class Wo extends WoId {

	}
}