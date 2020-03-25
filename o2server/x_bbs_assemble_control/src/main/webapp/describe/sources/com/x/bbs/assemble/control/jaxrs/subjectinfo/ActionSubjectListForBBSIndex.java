package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.Date;
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
import com.x.base.core.project.tools.SortTools;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectFilter;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectInfoProcess;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectWrapOut;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionWrapInConvert;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

import net.sf.ehcache.Element;

/**
 * 论坛首页每个版块的贴子列表
 * @author O2LEE
 *
 */
public class ActionSubjectListForBBSIndex extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSubjectListForBBSIndex.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, Integer page, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wrapIn = null;
		Boolean isBBSManager = false;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if ( check ) {
			isBBSManager = ThisApplication.isBBSManager(effectivePerson);
		}
		
		if( check ) {
			String cacheKey = wrapIn.getCacheKey( effectivePerson, isBBSManager );
			Element element = cache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) element.getObjectValue();
				result.setData( result_cache.getData() );
				result.setCount( result_cache.getCount() );
			} else {
				//继续进行数据查询
				result = getSubjectQueryResult(wrapIn, request, effectivePerson, page, count);
				cache.put(new Element(cacheKey, result ));
			}
		}
		return result;
	}
	
	public ActionResult<List<Wo>> getSubjectQueryResult( Wi wrapIn, HttpServletRequest request, EffectivePerson effectivePerson, Integer page, Integer count ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps_out = new ArrayList<Wo>();
		BBSSectionInfo sectionInfo = null;
		List<BBSSubjectInfo> subjectInfoList = null;
		List<String> viewSectionIds = new ArrayList<String>();
		Integer selectTotal = 0;
		Long total = 0L;
		Boolean check = true;

		if( check && StringUtils.isNotEmpty( wrapIn.getSectionId() ) ){
			
			if (check) {
				try {
					sectionInfo = sectionInfoServiceAdv.get( wrapIn.getSectionId() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectInfoProcess( e, "根据指定ID查询版块信息时发生异常.ID:" + wrapIn.getSectionId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				if ( sectionInfo == null ) {
					check = false;
					Exception exception = new ExceptionSectionNotExists( wrapIn.getSectionId() );
					result.error( exception );
				}
			}
		}
		
		if( check ){
			try{
				viewSectionIds = getViewableSectionIds( request, effectivePerson );
			}catch( Exception e ){
				check = false;
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( page == null ){
				page = 1;
			}
		}
		
		if( check ){
			if( count == null ){
				count = 20;
			}
		}

		//查询的最大条目数
		selectTotal = page * count;
		if( check ){
			if( selectTotal > 0 ){
				try{
					total = subjectInfoServiceAdv.countSubjectInSectionForPage( wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getCreatorName(), wrapIn.getNeedPicture(), null, viewSectionIds );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectFilter( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			if( selectTotal > 0 && total > 0 ){
				try{
					subjectInfoList = subjectInfoServiceAdv.listSubjectInSectionForPage( wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getCreatorName(), wrapIn.getNeedPicture(), null, selectTotal, viewSectionIds );
					if( subjectInfoList != null ){
						try {
							wraps_out = Wo.copier.copy( subjectInfoList );
							for( Wo wo : wraps_out ) {
								cutPersonNames( wo );
							}
							SortTools.desc( wraps_out, "latestReplyTime" );
							result.setData( wraps_out );
							result.setCount( total );
						} catch (Exception e) {
							Exception exception = new ExceptionSubjectWrapOut( e );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectFilter( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
	/**
	 *  将带@形式的人员标识修改为人员的姓名并且赋值到xxShort属性里
	 *  
	 * @param subject
	 */
	private void cutPersonNames( Wo subject ) {
		if( subject != null ) {
			if( StringUtils.isNotEmpty( subject.getLatestReplyUser() ) ) {
				subject.setLatestReplyUserShort( subject.getLatestReplyUser().split( "@" )[0]);
			}
			if( StringUtils.isNotEmpty( subject.getCreatorName() ) ) {
				subject.setCreatorNameShort( subject.getCreatorName().split( "@" )[0]);
			}
		}
	}
	
	public static class Wi{
		
		@FieldDescribe( "贴子ID." )
		private String subjectId = null;
		
		@FieldDescribe( "投标选项ID." )
		private String voteOptionId = null;
		
		@FieldDescribe( "贴子所属论坛ID." )
		private String forumId = null;
		
		@FieldDescribe( "贴子所属主版块ID." )
		private String mainSectionId = null;
		
		@FieldDescribe( "贴子所属版块ID." )
		private String sectionId = null;
		
		@FieldDescribe( "搜索内容." )
		private String searchContent = null;
		
		@FieldDescribe( "创建者名称." )
		private String creatorName = null;
		
		@FieldDescribe( "是否只查询有大图的贴子." )
		private Boolean needPicture = false;
		
		@FieldDescribe( "是否包含置顶贴." )
		private Boolean withTopSubject = false; // 是否包含置顶贴
		
		public static List<String> Excludes = new ArrayList<String>( JpaObject.FieldsUnmodify );
	
		
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
		public void setSearchContent( String searchContent ) {
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
		
		public String getCacheKey( EffectivePerson effectivePerson, Boolean isBBSManager ) {
			StringBuffer sb = new StringBuffer();
			if( StringUtils.isNotEmpty( effectivePerson.getDistinguishedName() )) {
				sb.append( effectivePerson.getDistinguishedName() );
			}
			if( StringUtils.isNotEmpty( effectivePerson.getDistinguishedName() )) {
				sb.append( "#" );
				sb.append( isBBSManager );
			}
			if( StringUtils.isNotEmpty( subjectId )) {
				sb.append( "#" );
				sb.append( subjectId );
			}
			if( StringUtils.isNotEmpty( voteOptionId )) {
				sb.append( "#" );
				sb.append( voteOptionId );
			}
			if( StringUtils.isNotEmpty( forumId )) {
				sb.append( "#" );
				sb.append( forumId );
			}
			if( StringUtils.isNotEmpty( mainSectionId )) {
				sb.append( "#" );
				sb.append( mainSectionId );
			}
			if( StringUtils.isNotEmpty( sectionId )) {
				sb.append( "#" );
				sb.append( sectionId );
			}
			if( StringUtils.isNotEmpty( searchContent )) {
				sb.append( "#" );
				sb.append( searchContent );
			}
			if( StringUtils.isNotEmpty( creatorName )) {
				sb.append( "#" );
				sb.append( creatorName );
			}
			sb.append( "#" );
			sb.append( needPicture );
			sb.append( "#" );
			sb.append( withTopSubject );
			sb.append( "#index" );
			return sb.toString();
		}
	}
	
	public static class Wo{
		
		public static WrapCopier< BBSSubjectInfo, Wo > copier = WrapCopierFactory.wo( BBSSubjectInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		@FieldDescribe("ID")
		private String id = "";
		
		@FieldDescribe("论坛ID")
		private String forumId = "";
		
		@FieldDescribe("版块ID")
		private String sectionId = "";
		
		@FieldDescribe("主版块ID")
		private String mainSectionId = "";

		@FieldDescribe("首页图片ID")
		private String picId = "";
		
		@FieldDescribe("主题名称：标题")
		private String title = "";

		@FieldDescribe("主题类别：讨论，新闻等等,根据版块设置")
		private String type = "新闻";

		@FieldDescribe("主题的类别,不同的类别有不同的操作:信息|问题|投票")
		private String typeCategory = "信息";

		@FieldDescribe("主题摘要")
		private String summary = null;

		@FieldDescribe("最新回复时间")
		private Date latestReplyTime = null;
		
		@FieldDescribe("发贴用户")
		private String creatorName = null;

		@FieldDescribe("最新回复用户")
		private String latestReplyUser = null;

		@FieldDescribe("最新回复ID")
		private String latestReplyId = null;

		@FieldDescribe("回复数量")
		private Long replyTotal = 0L;

		@FieldDescribe("查看数量")
		private Long viewTotal = 0L;

		@FieldDescribe("主题热度")
		private Long hot = 0L;		

		@FieldDescribe("是否为置顶主题")
		private Boolean isTopSubject = false;

		@FieldDescribe("精华主题")
		private Boolean isCreamSubject = false;

		@FieldDescribe("是否已解决:为问题贴准备")
		private Boolean isCompleted = false;

		@FieldDescribe("版主推荐主题")
		private Boolean isRecommendSubject = false;
		
		@FieldDescribe( "最新回复用户" )
		private String latestReplyUserShort = "";
		
		@FieldDescribe( "创建人姓名" )
		private String creatorNameShort = "";
		
		@FieldDescribe( "当前用户是否已经投票过." )
		private Boolean voted = false;
		
		public String getCreatorName() {
			return creatorName;
		}

		public void setCreatorName(String creatorName) {
			this.creatorName = creatorName;
		}

		public String getLatestReplyUserShort() {
			return latestReplyUserShort;
		}
		
		public String getCreatorNameShort() {
			return creatorNameShort;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setLatestReplyUserShort(String latestReplyUserShort) {
			this.latestReplyUserShort = latestReplyUserShort;
		}
		
		public void setCreatorNameShort(String creatorNameShort) {
			this.creatorNameShort = creatorNameShort;
		}

		public Boolean getVoted() {
			return voted;
		}

		public void setVoted(Boolean voted) {
			this.voted = voted;
		}

		public String getForumId() {
			return forumId;
		}

		public String getSectionId() {
			return sectionId;
		}

		public String getMainSectionId() {
			return mainSectionId;
		}

		public String getPicId() {
			return picId;
		}

		public String getTitle() {
			return title;
		}

		public String getType() {
			return type;
		}

		public String getTypeCategory() {
			return typeCategory;
		}

		public String getSummary() {
			return summary;
		}

		public Date getLatestReplyTime() {
			return latestReplyTime;
		}

		public String getLatestReplyUser() {
			return latestReplyUser;
		}

		public String getLatestReplyId() {
			return latestReplyId;
		}

		public Long getReplyTotal() {
			return replyTotal;
		}

		public Long getViewTotal() {
			return viewTotal;
		}

		public Long getHot() {
			return hot;
		}

		public Boolean getIsTopSubject() {
			return isTopSubject;
		}

		public Boolean getIsCreamSubject() {
			return isCreamSubject;
		}

		public Boolean getIsCompleted() {
			return isCompleted;
		}

		public Boolean getIsRecommendSubject() {
			return isRecommendSubject;
		}

		public void setForumId(String forumId) {
			this.forumId = forumId;
		}

		public void setSectionId(String sectionId) {
			this.sectionId = sectionId;
		}

		public void setMainSectionId(String mainSectionId) {
			this.mainSectionId = mainSectionId;
		}

		public void setPicId(String picId) {
			this.picId = picId;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setTypeCategory(String typeCategory) {
			this.typeCategory = typeCategory;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public void setLatestReplyTime(Date latestReplyTime) {
			this.latestReplyTime = latestReplyTime;
		}

		public void setLatestReplyUser(String latestReplyUser) {
			this.latestReplyUser = latestReplyUser;
		}

		public void setLatestReplyId(String latestReplyId) {
			this.latestReplyId = latestReplyId;
		}

		public void setReplyTotal(Long replyTotal) {
			this.replyTotal = replyTotal;
		}

		public void setViewTotal(Long viewTotal) {
			this.viewTotal = viewTotal;
		}

		public void setHot(Long hot) {
			this.hot = hot;
		}

		public void setIsTopSubject(Boolean isTopSubject) {
			this.isTopSubject = isTopSubject;
		}

		public void setIsCreamSubject(Boolean isCreamSubject) {
			this.isCreamSubject = isCreamSubject;
		}

		public void setIsCompleted(Boolean isCompleted) {
			this.isCompleted = isCompleted;
		}

		public void setIsRecommendSubject(Boolean isRecommendSubject) {
			this.isRecommendSubject = isRecommendSubject;
		}
	}
}