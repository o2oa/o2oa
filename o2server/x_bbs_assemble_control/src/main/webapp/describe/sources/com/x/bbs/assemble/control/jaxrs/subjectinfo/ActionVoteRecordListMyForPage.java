package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectFilter;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectWrapOut;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionWrapInConvert;
import com.x.bbs.entity.BBSVoteRecord;

public class ActionVoteRecordListMyForPage extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionVoteRecordListMyForPage.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, Integer page,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSVoteRecord> recordInfoList = null;
		List<BBSVoteRecord> recordInfoList_out = new ArrayList<BBSVoteRecord>();
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
				total = subjectVoteService.countVoteRecordForSubject(wrapIn.getSubjectId(), wrapIn.getVoteOptionId());
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
					recordInfoList = subjectVoteService.listVoteRecordForPage(wrapIn.getSubjectId(),
							wrapIn.getVoteOptionId(), page * count);
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
			for (int i = 0; recordInfoList != null && i < recordInfoList.size(); i++) {
				if (i < recordInfoList.size() && i >= startIndex && i < endIndex) {
					recordInfoList_out.add(recordInfoList.get(i));
				}
			}
			if ( ListTools.isNotEmpty( recordInfoList_out )) {
				try {
					wraps = Wo.copier.copy(recordInfoList_out);
					result.setData(wraps);
					result.setCount(total);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectWrapOut(e);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wi {

		private Boolean getBBSTopSubject = true;

		private Boolean getForumTopSubject = true;

		private Boolean getSectionTopSubject = true;

		private String subjectId = null;

		private String voteOptionId = null;

		private String forumId = null;

		private String mainSectionId = null;

		private String sectionId = null;

		private String searchContent = null;

		private String creatorName = null;

		private Boolean needPicture = false;

		private Boolean withTopSubject = null; // 是否包含置顶贴

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		public Boolean getGetBBSTopSubject() {
			return getBBSTopSubject;
		}

		public void setGetBBSTopSubject(Boolean getBBSTopSubject) {
			this.getBBSTopSubject = getBBSTopSubject;
		}

		public Boolean getGetForumTopSubject() {
			return getForumTopSubject;
		}

		public void setGetForumTopSubject(Boolean getForumTopSubject) {
			this.getForumTopSubject = getForumTopSubject;
		}

		public Boolean getGetSectionTopSubject() {
			return getSectionTopSubject;
		}

		public void setGetSectionTopSubject(Boolean getSectionTopSubject) {
			this.getSectionTopSubject = getSectionTopSubject;
		}

		public String getForumId() {
			return forumId;
		}

		public void setForumId(String forumId) {
			this.forumId = forumId;
		}

		public String getSectionId() {
			return sectionId;
		}

		public void setSectionId(String sectionId) {
			this.sectionId = sectionId;
		}

		public String getMainSectionId() {
			return mainSectionId;
		}

		public void setMainSectionId(String mainSectionId) {
			this.mainSectionId = mainSectionId;
		}

		public Boolean getNeedPicture() {
			return needPicture;
		}

		public void setNeedPicture(Boolean needPicture) {
			this.needPicture = needPicture;
		}

		public Boolean getWithTopSubject() {
			return withTopSubject;
		}

		public void setWithTopSubject(Boolean withTopSubject) {
			this.withTopSubject = withTopSubject;
		}

		public String getSearchContent() {
			return searchContent;
		}

		public void setSearchContent(String searchContent) {
			this.searchContent = searchContent;
		}

		public String getCreatorName() {
			return creatorName;
		}

		public void setCreatorName(String creatorName) {
			this.creatorName = creatorName;
		}

		public String getSubjectId() {
			return subjectId;
		}

		public void setSubjectId(String subjectId) {
			this.subjectId = subjectId;
		}

		public String getVoteOptionId() {
			return voteOptionId;
		}

		public void setVoteOptionId(String voteOptionId) {
			this.voteOptionId = voteOptionId;
		}

	}

	public static class Wo extends BBSVoteRecord {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<BBSVoteRecord, Wo> copier = WrapCopierFactory.wo(BBSVoteRecord.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}