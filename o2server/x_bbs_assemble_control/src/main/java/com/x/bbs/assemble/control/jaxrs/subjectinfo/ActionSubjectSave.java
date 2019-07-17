package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectInfoProcess;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectOperation;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectSave;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectWrapIn;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionVoteOptionEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionWrapInConvert;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionSubjectSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSubjectSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		BBSSectionInfo sectionInfo = null;
		BBSSubjectInfo subjectInfo = null;
		Wi wrapIn = null;
		Wo wo = new Wo();
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
			// 对需要保存的信息进行基础的信息验证，如果验证不通过，则抛出PromptException
			try {
				SubjectPropertyValidator.baseValidate(request, wrapIn);
			} catch (Exception e) {
				check = false;
				result.error(e);
			}
		}

		// 查询版块信息是否存在
		if (check) {
			try {
				sectionInfo = sectionInfoServiceAdv.get(wrapIn.getSectionId());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectInfoProcess(e,
						"根据指定ID查询版块信息时发生异常.ID:" + wrapIn.getSectionId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (wrapIn.getTypeCategory() == null || wrapIn.getTypeCategory().isEmpty()) {
				wrapIn.setTypeCategory("信息");
			} else {
				try {
					SubjectPropertyValidator.typeCategoryValidate(sectionInfo, wrapIn);
				} catch (Exception e) {
					check = false;
					result.error(e);
				}
			}
		}
		if (check) {
			if (wrapIn.getType() == null || wrapIn.getType().isEmpty()) {
				wrapIn.setType("未知类别");
			} else {
				try {
					SubjectPropertyValidator.subjectTypeValidate(sectionInfo, wrapIn);
				} catch (Exception e) {
					check = false;
					result.error(e);
				}
			}
		}

		if (check) {
			if ("投票".equals(wrapIn.getTypeCategory())) {
				// 如果是投票贴，判断投票选项是否存在
				if (wrapIn.getOptionGroups() == null || wrapIn.getOptionGroups().isEmpty()) {
					check = false;
					Exception exception = new ExceptionVoteOptionEmpty();
					result.error(exception);
				}
			}
		}

		if (check) {
			try {
				subjectInfo = Wi.copier.copy(wrapIn);
				if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
					subjectInfo.setId(wrapIn.getId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectWrapIn(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			subjectInfo.setForumId(sectionInfo.getForumId());
			subjectInfo.setForumName(sectionInfo.getForumName());
			subjectInfo.setMainSectionId(sectionInfo.getMainSectionId());
			subjectInfo.setMainSectionName(sectionInfo.getMainSectionName());
			subjectInfo.setSectionId(sectionInfo.getId());
			subjectInfo.setSectionName(sectionInfo.getSectionName());
			subjectInfo.setCreatorName(effectivePerson.getDistinguishedName());
			subjectInfo.setLatestReplyTime(new Date());
			subjectInfo.setTypeCategory(wrapIn.getTypeCategory());
			subjectInfo.setType(wrapIn.getType());
			subjectInfo.setTitle(subjectInfo.getTitle().trim());
			subjectInfo.setVoteLimitTime(wrapIn.getVoteLimitTime());
			subjectInfo.setVotePersonVisible(wrapIn.getVotePersonVisible());
			subjectInfo.setVoteResultVisible(wrapIn.getVoteResultVisible());
		}

		if (check) {
			subjectInfo.setMachineName(wrapIn.getSubjectMachineName());
			subjectInfo.setSystemType(wrapIn.getSubjectSystemName());
			try {
				subjectInfo = subjectInfoServiceAdv.save(subjectInfo, wrapIn.getContent());
				wo.setId(subjectInfo.getId());
				
				ApplicationCache.notify( BBSSubjectInfo.class );
				ApplicationCache.notify( BBSSectionInfo.class );
				ApplicationCache.notify( BBSForumInfo.class );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectSave(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if ("投票".equals(wrapIn.getTypeCategory())) {
				try {
					subjectVoteService.saveVoteOptions(subjectInfo, wrapIn.getOptionGroups());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectOperation(e, "系统在保存投票选项信息时发生异常");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends BBSSubjectInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, BBSSubjectInfo> copier = WrapCopierFactory.wi(Wi.class, BBSSubjectInfo.class, null,
				Wi.FieldsUnmodify);

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