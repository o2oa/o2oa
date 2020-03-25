package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.ActionSubjectListForPage.Wi;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectFilter;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectWrapOut;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionWrapInConvert;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionGroup;

public class ActionSubjectListMyForPage extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSubjectListMyForPage.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, Integer page,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSSubjectInfo> subjectInfoList = null;
		List<BBSSubjectInfo> subjectInfoList_out = new ArrayList<BBSSubjectInfo>();
		Long total = 0L;
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
			if (page == null) {
				page = 1;
			}
		}
		if (check) {
			if (count == null) {
				count = 20;
			}
		}
		if (check) {
			try {
				total = subjectInfoServiceAdv.countUserSubjectForPage(wrapIn.getForumId(), wrapIn.getMainSectionId(),
						wrapIn.getSectionId(), wrapIn.getNeedPicture(), wrapIn.getWithTopSubject(),
						effectivePerson.getDistinguishedName());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectFilter(e);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (total > 0) {
				try {
					subjectInfoList = subjectInfoServiceAdv.listUserSubjectForPage(wrapIn.getForumId(),
							wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getNeedPicture(),
							wrapIn.getWithTopSubject(), page * count, effectivePerson.getDistinguishedName());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectFilter(e);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if (page <= 0) {
				page = 1;
			}
			if (count <= 0) {
				count = 20;
			}
			int startIndex = (page - 1) * count;
			int endIndex = page * count;
			for (int i = 0; subjectInfoList != null && i < subjectInfoList.size(); i++) {
				if (i < subjectInfoList.size() && i >= startIndex && i < endIndex) {
					subjectInfoList_out.add(subjectInfoList.get(i));
				}
			}
			if ( ListTools.isNotEmpty( subjectInfoList_out )) {
				try {
					wraps = Wo.copier.copy(subjectInfoList_out);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectWrapOut(e);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			// 将带@形式的人员标识修改为人员的姓名并且赋值到xxShort属性里
			if ( ListTools.isNotEmpty( wraps )) {
				for (Wo wo : wraps) {
					cutPersonNames(wo);
				}
			}
		}
		result.setData(wraps);
		result.setCount(total);
		return result;
	}

	/**
	 * 将带@形式的人员标识修改为人员的姓名并且赋值到xxShort属性里
	 * 
	 * latestReplyUserShort = ""; bBSIndexSetterNameShort = "";
	 * screamSetterNameShort = ""; originalSetterNameShort = "";
	 * creatorNameShort = ""; auditorNameShort = "";
	 * 
	 * @param subject
	 */
	private void cutPersonNames(Wo subject) {
		if (subject != null) {
			if ( StringUtils.isNotEmpty( subject.getLatestReplyUser() )) {
				subject.setLatestReplyUserShort(subject.getLatestReplyUser().split("@")[0]);
			}
			if ( StringUtils.isNotEmpty( subject.getbBSIndexSetterName() )) {
				subject.setbBSIndexSetterNameShort(subject.getbBSIndexSetterName().split("@")[0]);
			}
			if ( StringUtils.isNotEmpty( subject.getScreamSetterName() )) {
				subject.setScreamSetterNameShort(subject.getScreamSetterName().split("@")[0]);
			}
			if ( StringUtils.isNotEmpty( subject.getOriginalSetterName() )) {
				subject.setOriginalSetterNameShort(subject.getOriginalSetterName().split("@")[0]);
			}
			if ( StringUtils.isNotEmpty( subject.getCreatorName() )) {
				subject.setCreatorNameShort(subject.getCreatorName().split("@")[0]);
			}
			if ( StringUtils.isNotEmpty( subject.getAuditorName() )) {
				subject.setAuditorNameShort(subject.getAuditorName().split("@")[0]);
			}
		}
	}

	public static class Wo extends BBSSubjectInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<BBSSubjectInfo, Wo> copier = WrapCopierFactory.wo(BBSSubjectInfo.class, Wo.class, null, JpaObject.FieldsInvisible );

		private List<WoSubjectAttachment> subjectAttachmentList;

		@FieldDescribe("投票主题的所有投票选项列表.")
		private List<WoBBSVoteOptionGroup> voteOptionGroupList;

		private String content = null;

		private Long voteCount = 0L;

		private String pictureBase64 = null;

		@FieldDescribe("最新回复用户")
		private String latestReplyUserShort = "";

		@FieldDescribe("首页推荐人姓名")
		private String bBSIndexSetterNameShort = "";

		@FieldDescribe("精华设置人姓名")
		private String screamSetterNameShort = "";

		@FieldDescribe("原创设置人姓名")
		private String originalSetterNameShort = "";

		@FieldDescribe("创建人姓名")
		private String creatorNameShort = "";

		@FieldDescribe("审核人姓名")
		private String auditorNameShort = "";

		@FieldDescribe("当前用户是否已经投票过.")
		private Boolean voted = false;

		public String getLatestReplyUserShort() {
			return latestReplyUserShort;
		}

		public String getbBSIndexSetterNameShort() {
			return bBSIndexSetterNameShort;
		}

		public String getScreamSetterNameShort() {
			return screamSetterNameShort;
		}

		public String getOriginalSetterNameShort() {
			return originalSetterNameShort;
		}

		public String getCreatorNameShort() {
			return creatorNameShort;
		}

		public String getAuditorNameShort() {
			return auditorNameShort;
		}

		public void setLatestReplyUserShort(String latestReplyUserShort) {
			this.latestReplyUserShort = latestReplyUserShort;
		}

		public void setbBSIndexSetterNameShort(String bBSIndexSetterNameShort) {
			this.bBSIndexSetterNameShort = bBSIndexSetterNameShort;
		}

		public void setScreamSetterNameShort(String screamSetterNameShort) {
			this.screamSetterNameShort = screamSetterNameShort;
		}

		public void setOriginalSetterNameShort(String originalSetterNameShort) {
			this.originalSetterNameShort = originalSetterNameShort;
		}

		public void setCreatorNameShort(String creatorNameShort) {
			this.creatorNameShort = creatorNameShort;
		}

		public void setAuditorNameShort(String auditorNameShort) {
			this.auditorNameShort = auditorNameShort;
		}

		public List<WoSubjectAttachment> getSubjectAttachmentList() {
			return subjectAttachmentList;
		}

		public void setSubjectAttachmentList(List<WoSubjectAttachment> subjectAttachmentList) {
			this.subjectAttachmentList = subjectAttachmentList;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getPictureBase64() {
			return pictureBase64;
		}

		public void setPictureBase64(String pictureBase64) {
			this.pictureBase64 = pictureBase64;
		}

		public List<WoBBSVoteOptionGroup> getVoteOptionGroupList() {
			return voteOptionGroupList;
		}

		public void setVoteOptionGroupList(List<WoBBSVoteOptionGroup> voteOptionGroupList) {
			this.voteOptionGroupList = voteOptionGroupList;
		}

		public Boolean getVoted() {
			return voted;
		}

		public void setVoted(Boolean voted) {
			this.voted = voted;
		}

		public Long getVoteCount() {
			return voteCount;
		}

		public void setVoteCount(Long voteCount) {
			this.voteCount = voteCount;
		}
	}

	public static class WoSubjectAttachment extends BBSSubjectAttachment {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<BBSSubjectAttachment, WoSubjectAttachment> copier = WrapCopierFactory
				.wo(BBSSubjectAttachment.class, WoSubjectAttachment.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoBBSVoteOptionGroup extends BBSVoteOptionGroup {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<BBSVoteOptionGroup, WoBBSVoteOptionGroup> copier = WrapCopierFactory
				.wo(BBSVoteOptionGroup.class, WoBBSVoteOptionGroup.class, null, JpaObject.FieldsInvisible);

		private List<WoBBSVoteOption> voteOptions = null;

		public List<WoBBSVoteOption> getVoteOptions() {
			return voteOptions;
		}

		public void setVoteOptions(List<WoBBSVoteOption> voteOptions) {
			this.voteOptions = voteOptions;
		}
	}

	public static class WoBBSVoteOption extends BBSVoteOption {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<BBSVoteOption, WoBBSVoteOption> copier = WrapCopierFactory.wo(BBSVoteOption.class,
				WoBBSVoteOption.class, null, JpaObject.FieldsInvisible);

		private Boolean voted = false;

		public Boolean getVoted() {
			return voted;
		}

		public void setVoted(Boolean voted) {
			this.voted = voted;
		}
	}
}