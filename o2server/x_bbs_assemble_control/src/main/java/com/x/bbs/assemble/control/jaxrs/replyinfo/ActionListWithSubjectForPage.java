package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionCountEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionPageEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.entity.BBSReplyInfo;


/**
 * @apiNote 根据主帖的ID 返回针对主帖的回复列表（其中要包含一个标志 及说明针对该回复的回复内容条数 ）
 * @author O2LEE
 */
public class ActionListWithSubjectForPage extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithSubjectForPage.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, Integer page,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
			if( wrapIn.getShowSubReply() == null ){
				wrapIn.setShowSubReply(true);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionReplyInfoProcess(e,"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), wrapIn.getSubjectId(), page, count, wrapIn.getShowSubReply());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) optional.get();
				result.setData(result_cache.getData());
				result.setCount(result_cache.getCount());
			} else {
				result = getReplyQueryResult( wrapIn, request, effectivePerson, page, count );
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}
		return result;
	}

	public ActionResult<List<Wo>> getReplyQueryResult(Wi wrapIn, HttpServletRequest request,
			EffectivePerson effectivePerson, Integer page, Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<Wo> wrapSubReplies = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		Boolean check = true;
		String config_BBS_REPLY_SORTTYPE = configSettingService.getValueWithConfigCode("BBS_REPLY_SORTTYPE");

		if (check) {
			if (page == null) {
				check = false;
				Exception exception = new ExceptionPageEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (count == null) {
				check = false;
				Exception exception = new ExceptionCountEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				total = replyInfoService.countWithSubjectForPage(wrapIn.getSubjectId(), wrapIn.getShowSubReply() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e,"根据主题ID查询主题内所有的回复数量时发生异常。Subject:" + wrapIn.getSubjectId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (total > 0) {
				try {
					replyInfoList = replyInfoService.listWithSubjectForPage( wrapIn.getSubjectId(), wrapIn.getShowSubReply(), page * count, config_BBS_REPLY_SORTTYPE );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess(e,"根据主题ID查询主题内所有的回复列表时发生异常。Subject:" + wrapIn.getSubjectId());
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
			for ( int i = 0; replyInfoList != null && i < replyInfoList.size(); i++ ) {
				if (i < replyInfoList.size() && i >= startIndex && i < endIndex) {
					replyInfoList_out.add( replyInfoList.get(i) );
				}
			}
			if ( ListTools.isNotEmpty( replyInfoList_out )) {
				try {
					wraps = Wo.copier.copy(replyInfoList_out);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess(e, "将查询结果转换成可以输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if (ListTools.isNotEmpty(wraps)) {
				List<BBSReplyInfo> subReplies = null;
				for (Wo wo : wraps) {
					if(StringUtils.isBlank(wo.getNickName())){
						wo.setNickName(wo.getCreatorName());
					}
					if (StringUtils.isNotEmpty(wo.getCreatorName())) {
						wo.setCreatorNameShort(wo.getCreatorName().split("@")[0]);
					}
					if (StringUtils.isNotEmpty(wo.getAuditorName())) {
						wo.setAuditorNameShort(wo.getAuditorName().split("@")[0]);
					}

					//查询一下该回复是否存在下级回复，以及下级回复的数量，除了第一条，其他的都去掉内容，避免大量的网络传输
					subReplies = replyInfoService.listRelysWithRelyId( wo.getId(), config_BBS_REPLY_SORTTYPE );
					if( ListTools.isNotEmpty( subReplies )){
						wrapSubReplies = Wo.copier.copy( subReplies );
						for( int i=0; i<wrapSubReplies.size(); i++  ){
							if( i > 0 ){
								wrapSubReplies.get(i).setContent(null);
							}
						}
						wo.setSubReplyTotal( wrapSubReplies.size() );
						wo.setSubReplies( wrapSubReplies );
					}
				}
				result.setCount(total);

			}
		}
		result.setData(wraps);

		return result;
	}

	public static class Wi {

		@FieldDescribe("主题Id")
		private String subjectId = null;

		@FieldDescribe("是否平级显示所有的的回复, 如果为false则只显示第一层")
		private Boolean showSubReply = true;

		public Boolean getShowSubReply() { return showSubReply; }

		public void setShowSubReply(Boolean showSubReply) { this.showSubReply = showSubReply; }

		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		public String getSubjectId() {
			return subjectId;
		}

		public void setSubjectId(String subjectId) {
			this.subjectId = subjectId;
		}
	}

	public static class Wo extends BBSReplyInfo {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<BBSReplyInfo, Wo> copier = WrapCopierFactory.wo(BBSReplyInfo.class, Wo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("创建人姓名")
		private String creatorNameShort = "";

		@FieldDescribe("审核人姓名")
		private String auditorNameShort = "";

		@FieldDescribe("下级回复的数量，默认为0")
		private Integer subReplyTotal = 0;

		@FieldDescribe("下级回复的数量，默认为0")
		private List<Wo> subReplies;

		public List<Wo> getSubReplies() {
			return subReplies;
		}

		public void setSubReplies(List<Wo> subReplies) {
			this.subReplies = subReplies;
		}

		public Integer getSubReplyTotal() {
			return subReplyTotal;
		}

		public void setSubReplyTotal(Integer subReplyTotal) {
			this.subReplyTotal = subReplyTotal;
		}

		public String getCreatorNameShort() {
			return creatorNameShort;
		}

		public String getAuditorNameShort() {
			return auditorNameShort;
		}

		public void setCreatorNameShort(String creatorNameShort) {
			this.creatorNameShort = creatorNameShort;
		}

		public void setAuditorNameShort(String auditorNameShort) {
			this.auditorNameShort = auditorNameShort;
		}
	}
}
