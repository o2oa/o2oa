package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionVoteOptionEmpty;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

public class ActionSubjectSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSubjectSave.class);

	private static final String ANONYMOUS_NAME = "匿名";
	private static final String TYPE_CATEGORY = "投票";


	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		if(!effectivePerson.isAnonymous() && this.userManagerService.personHasShutup(effectivePerson.getDistinguishedName())){
			throw new ExceptionAccessDenied(effectivePerson);
		}

		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		SubjectPropertyValidator.baseValidate(request, wrapIn);

		// 查询版块信息是否存在
		BBSSectionInfo sectionInfo = sectionInfoServiceAdv.get(wrapIn.getSectionId());
		if(sectionInfo == null){
			throw new ExceptionEntityNotExist(wrapIn.getSectionId(), BBSSectionInfo.class);
		}

		if (StringUtils.isBlank(wrapIn.getTypeCategory())) {
			wrapIn.setTypeCategory("信息");
		} else {
			SubjectPropertyValidator.typeCategoryValidate(sectionInfo, wrapIn);
		}

		if (StringUtils.isBlank(wrapIn.getType())) {
			wrapIn.setType("未知类别");
		} else {
			SubjectPropertyValidator.subjectTypeValidate(sectionInfo, wrapIn);
		}

		if (TYPE_CATEGORY.equals(wrapIn.getTypeCategory())) {
			// 如果是投票贴，判断投票选项是否存在
			if (ListTools.isEmpty(wrapIn.getOptionGroups())) {
				throw  new ExceptionVoteOptionEmpty();
			}
		}

		BBSSubjectInfo subjectInfo = Wi.copier.copy(wrapIn);
		if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
			subjectInfo.setId(wrapIn.getId());
		}
		subjectInfo.setForumId(sectionInfo.getForumId());
		subjectInfo.setForumName(sectionInfo.getForumName());
		subjectInfo.setMainSectionId(sectionInfo.getMainSectionId());
		subjectInfo.setMainSectionName(sectionInfo.getMainSectionName());
		subjectInfo.setSectionId(sectionInfo.getId());
		subjectInfo.setSectionName(sectionInfo.getSectionName());
		subjectInfo.setCreatorName(effectivePerson.getDistinguishedName());
		subjectInfo.setLastUpdateUser(effectivePerson.getDistinguishedName());
		subjectInfo.setLatestReplyTime(new Date());
		subjectInfo.setTypeCategory(wrapIn.getTypeCategory());
		subjectInfo.setType(wrapIn.getType());
		subjectInfo.setTitle(subjectInfo.getTitle().trim());
		subjectInfo.setVoteLimitTime(wrapIn.getVoteLimitTime());
		subjectInfo.setVotePersonVisible(wrapIn.getVotePersonVisible());
		subjectInfo.setVoteResultVisible(wrapIn.getVoteResultVisible());
		subjectInfo.setGrade(wrapIn.getGrade());

		if(BooleanUtils.isTrue(wrapIn.getAnonymousSubject())){
			subjectInfo.setNickName(ANONYMOUS_NAME);
		}else {
			Business business = new Business(null);
			subjectInfo.setNickName(business.organization().person().getNickName(effectivePerson.getDistinguishedName()));
			subjectInfo.setAnonymousSubject(false);
		}

		subjectInfo.setMachineName(wrapIn.getSubjectMachineName());
		subjectInfo.setSystemType(wrapIn.getSubjectSystemName());
		subjectInfo = subjectInfoServiceAdv.save(subjectInfo, wrapIn.getContent());
		wo.setId(subjectInfo.getId());

		CacheManager.notify( BBSSubjectInfo.class );
		CacheManager.notify( BBSSectionInfo.class );
		CacheManager.notify( BBSForumInfo.class );

		if (TYPE_CATEGORY.equals(wrapIn.getTypeCategory())) {
			subjectVoteService.saveVoteOptions(subjectInfo, wrapIn.getOptionGroups());
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
