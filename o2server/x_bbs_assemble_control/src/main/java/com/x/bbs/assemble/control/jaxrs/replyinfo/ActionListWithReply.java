package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.entity.BBSReplyInfo;

/**
 * @apiNote 根据回复内容ID 查询 针对该回帖的回复内容的列表
 * @Author O2LEE
 */
public class ActionListWithReply extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithReply.class);

	/**
	 * 根据回复的ID获取针对该回复的回复列表
	 * @param request
	 * @param effectivePerson
	 * @param replyId
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String replyId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Boolean check = true;

		if (check) {
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), replyId);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) optional.get();
				result.setData(result_cache.getData());
				result.setCount(result_cache.getCount());
			} else {
				result = getReplyQueryResult( request, effectivePerson, replyId );
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}
		return result;
	}

	/**
	 * 根据回复的ID获取针对该回复的回复列表
	 * @param request
	 * @param effectivePerson
	 * @param replyId
	 * @return
	 */
	public ActionResult<List<Wo>> getReplyQueryResult( HttpServletRequest request,
			EffectivePerson effectivePerson, String replyId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		Boolean check = true;
		String config_BBS_REPLY_SORTTYPE = configSettingService.getValueWithConfigCode("BBS_REPLY_SORTTYPE");

		if (check) {
			try {
				replyInfoList = replyInfoService.listRelysWithRelyId(replyId, config_BBS_REPLY_SORTTYPE );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e,"根据回复ID查询针对该回复所有的二级回复数量时发生异常。replyId:" + replyId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (ListTools.isNotEmpty(replyInfoList)) {
				try {
					wraps = Wo.copier.copy(replyInfoList);
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
				}
				result.setCount( Long.parseLong(wraps.size()+"") );
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wo extends BBSReplyInfo {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<BBSReplyInfo, Wo> copier = WrapCopierFactory.wo(BBSReplyInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("创建人姓名")
		private String creatorNameShort = "";

		@FieldDescribe("审核人姓名")
		private String auditorNameShort = "";

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
