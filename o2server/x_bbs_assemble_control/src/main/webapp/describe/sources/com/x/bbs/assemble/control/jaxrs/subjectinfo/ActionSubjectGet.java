package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectContentQueryById;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectIdEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectQueryById;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectWrapOut;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionVoteOptionListById;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionGroup;

import net.sf.ehcache.Element;

public class ActionSubjectGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSubjectGet.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}

		if (check) {
			String cacheKey = "subject#get#" + id;
			Element element = cache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				ActionResult<Wo> result_cache = (ActionResult<Wo>) element.getObjectValue();
				result.setData(result_cache.getData());
				result.setCount(1L);
			} else {
				// 继续进行数据查询
				result = getSubjectGetQueryResult(id, request, effectivePerson);
				cache.put(new Element(cacheKey, result));
			}
		}

		return result;
	}

	private ActionResult<Wo> getSubjectGetQueryResult(String id, HttpServletRequest request,
			EffectivePerson effectivePerson) {
		ActionResult<Wo> result = new ActionResult<>();
		List<WoSubjectAttachment> wrapSubjectAttachmentList = null;
		List<BBSSubjectAttachment> subjectAttachmentList = null;
		List<BBSVoteOption> voteOptionList = null;
		List<BBSVoteOptionGroup> voteOptionGroupList = null;
		List<WoBBSVoteOptionGroup> wrapOutSubjectVoteOptionGroupList = null;
		List<WoBBSVoteOption> wrapOutSubjectVoteOptionList = null;
		Wo wrap = null;
		BBSSubjectInfo subjectInfo = null;
		String subjectContent = null;
		Boolean check = true;
		
		// 查询版块信息是否存在
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
				// logger.error( e, effectivePerson, request, null);
			} else {// 查到了主题信息
				try {
					wrap = Wo.copier.copy(subjectInfo);
					// 根据附件ID列表查询附件信息
					if ( ListTools.isNotEmpty( subjectInfo.getAttachmentList() )) {
						subjectAttachmentList = subjectInfoServiceAdv
								.listAttachmentByIds(subjectInfo.getAttachmentList());
						if (subjectAttachmentList != null && subjectAttachmentList.size() > 0) {
							wrapSubjectAttachmentList = WoSubjectAttachment.copier.copy(subjectAttachmentList);
							wrap.setSubjectAttachmentList(wrapSubjectAttachmentList);
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectWrapOut(e);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if (wrap != null) {
				// 填充主题的内容信息
				try {
					subjectContent = subjectInfoServiceAdv.getSubjectContent(id);
					if (subjectContent != null) {
						wrap.setContent(subjectContent);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectContentQueryById(e, id);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			if (wrap != null) {// 获取该主题的投票选项组
				try {
					voteOptionGroupList = subjectVoteService.listVoteOptionGroup(id);
					if ( ListTools.isNotEmpty( voteOptionGroupList )) {
						wrapOutSubjectVoteOptionGroupList = WoBBSVoteOptionGroup.copier.copy(voteOptionGroupList);
						for (WoBBSVoteOptionGroup group : wrapOutSubjectVoteOptionGroupList) {
							voteOptionList = subjectVoteService.listVoteOptionByGroupId(group.getId());
							if ( ListTools.isNotEmpty( voteOptionList )) {
								try {
									wrapOutSubjectVoteOptionList = WoBBSVoteOption.copier.copy(voteOptionList);
									group.setVoteOptions(wrapOutSubjectVoteOptionList);
								} catch (Exception e) {
									check = false;
									Exception exception = new ExceptionSubjectWrapOut(e);
									result.error(exception);
									logger.error(e, effectivePerson, request, null);
								}
							}
						}
						wrap.setVoteOptionGroupList(wrapOutSubjectVoteOptionGroupList);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionVoteOptionListById(e, id);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			// 将带@形式的人员标识修改为人员的姓名并且赋值到xxShort属性里
			if (wrap != null) {
				cutPersonNames(wrap);
			}
		}
		result.setData(wrap);
		return result;
	}

	/**
	 * 将带@形式的人员标识修改为人员的姓名并且赋值到xxShort属性里
	 * 
	 * latestReplyUserShort = ""; bBSIndexSetterNameShort = "";
	 * screamSetterNameShort = ""; originalSetterNameShort = ""; creatorNameShort =
	 * ""; auditorNameShort = "";
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

		public static WrapCopier<BBSSubjectInfo, Wo> copier = WrapCopierFactory.wo(BBSSubjectInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);

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