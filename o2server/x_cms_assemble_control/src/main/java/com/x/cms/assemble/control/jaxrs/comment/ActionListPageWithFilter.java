package com.x.cms.assemble.control.jaxrs.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.cms.core.express.tools.filter.QueryFilter;

/**
 * 文档评论分页查询
 * @author sword
 */
public class ActionListPageWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPageWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, Integer pageNum, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> wos = new ArrayList<>();
		ResultObject resultObject = null;

		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);

		QueryFilter queryFilter = wrapIn.getQueryFilter();

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), effectivePerson.getDistinguishedName(),
				pageNum, count, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter.getContentSHA1() );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			resultObject = (ResultObject)optional.get();
			result.setCount( resultObject.getTotal() );
			result.setData( resultObject.getWos() );
		} else {
			try {
				Long total = documentCommentInfoQueryService.countWithFilter(effectivePerson, queryFilter );
				List<DocumentCommentInfo> documentCommentInfoList = documentCommentInfoQueryService.listWithFilter( effectivePerson, count, pageNum, wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter);

				if( ListTools.isNotEmpty( documentCommentInfoList )) {
					for( DocumentCommentInfo documentCommentInfo : documentCommentInfoList ) {
						Wo wo = Wo.copier.copy(documentCommentInfo);
						wo.setContent( documentCommentInfoQueryService.getCommentContent(documentCommentInfo.getId()));
						if(ListTools.isNotEmpty(docCommendQueryService.listByCommentAndPerson(documentCommentInfo.getId(), effectivePerson.getDistinguishedName(), 1))){
							wo.setIsCommend(true);
						}else{
							wo.setIsCommend(false);
						}
						wos.add( wo );
					}
				}

				resultObject = new ResultObject( total, wos );
				CacheManager.put(cacheCategory, cacheKey, resultObject );
				result.setCount( resultObject.getTotal() );
				result.setData( resultObject.getWos() );
			} catch (Exception e) {
				logger.warn("系统查询评论信息列表时发生异常!");
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends WrapInQueryDocumentCommentInfo{

	}

	public static class Wo extends DocumentCommentInfo {
		private static final long serialVersionUID = -5076990764713538973L;

		static WrapCopier<DocumentCommentInfo, Wo> copier = WrapCopierFactory.wo( DocumentCommentInfo.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

		private Long rank;

		@FieldDescribe("内容")
		private String content = "";

		@FieldDescribe( "评论是否已被当前用户点赞." )
		private Boolean isCommend = false;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Boolean getIsCommend() {
			return isCommend;
		}

		public void setIsCommend(Boolean isCommend) {
			this.isCommend = isCommend;
		}

	}

	public static class ResultObject {

		private Long total;

		private List<Wo> wos;

		public ResultObject() {}

		public ResultObject(Long count, List<Wo> data) {
			this.total = count;
			this.wos = data;
		}

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public List<Wo> getWos() {
			return wos;
		}

		public void setWos(List<Wo> wos) {
			this.wos = wos;
		}
	}
}
