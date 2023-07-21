package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.x_bbs_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.MD5Tool;
import com.x.base.core.project.tools.SortTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionNameEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectFilter;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectInfoProcess;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectWrapOut;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjecttypeEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionWrapInConvert;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionGroup;

public class ActionSubjectListWithSubjectTypeForPage extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSubjectListWithSubjectTypeForPage.class );

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
		if (check) {
			if (StringUtils.isEmpty(wrapIn.getSectionName())) {
				check = false;
				Exception exception =  new ExceptionSectionNameEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (StringUtils.isEmpty(wrapIn.getSubjectType())) {
				check = false;
				Exception exception =  new ExceptionSubjecttypeEmpty();
				result.error(exception);
			}
		}

		if( check ) {
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), effectivePerson.getDistinguishedName(), MD5Tool.getMD5Str(gson.toJson(wrapIn)) ,isBBSManager,page,count);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) optional.get();
				result.setData( result_cache.getData() );
				result.setCount( result_cache.getCount() );
			} else {
				//继续进行数据查询
				result = getSubjectQueryResult(wrapIn, request, effectivePerson, page, count);
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}
		return result;
	}

	public ActionResult<List<Wo>> getSubjectQueryResult( Wi wrapIn, HttpServletRequest request, EffectivePerson effectivePerson, Integer page, Integer count ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps_nonTop = new ArrayList<>();
		List<Wo> wraps_top = new ArrayList<>();
		List<Wo> wraps_out = new ArrayList<Wo>();
		BBSSectionInfo sectionInfo = null;
		List<BBSSubjectInfo> subjectInfoList = null;
		List<BBSSubjectInfo> subjectInfoList_top = null;
		List<String> viewSectionIds = new ArrayList<String>();
		Integer selectTotal = 0;
		Long total = 0L;
		Integer topTotal = 0;
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
		Boolean selectTopInSection = null;//默认是将版块内所有的置顶和非置顶贴全部查出

		//查询出所有的置顶贴
		if ( check && wrapIn != null && wrapIn.getWithTopSubject() != null && wrapIn.getWithTopSubject() ) {
			try {
				//subjectInfoList_top = subjectInfoServiceAdv.listAllTopSubject( sectionInfo, wrapIn.getCreatorName(), viewSectionIds ,wrapIn.getStartTime() ,  wrapIn.getEndTime());
				subjectInfoList_top = subjectInfoServiceAdv.listTopSubjectByType( sectionInfo,wrapIn.getSubjectType(), wrapIn.getCreatorName(), viewSectionIds ,wrapIn.getStartTime() ,  wrapIn.getEndTime());
				if( subjectInfoList_top != null ){
					topTotal = subjectInfoList_top.size();
					try {
						wraps_top = Wo.copier.copy( subjectInfoList_top );
						SortTools.desc( wraps_top, "latestReplyTime" );
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

		if( check ){
			//置顶贴会占用分页的每页条目数
			if( wraps_top != null ){
				if( selectTotal < wraps_top.size() ){
					selectTotal = 0;
				}else{
					selectTotal = selectTotal - wraps_top.size();
				}
			}
		}

		if( check ){
			selectTopInSection = false; //置顶贴的处理已经在前面处理过了，置顶贴已经放到一个List里，不需要再次查询出来了，后续的查询过滤置顶贴
			if( selectTotal > 0 ){
				try{
					total = subjectInfoServiceAdv.countSubjectWithSubjectTypeForPage(wrapIn.getSectionName() ,wrapIn.getSubjectType(),wrapIn.getSearchContent(), wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(),
							wrapIn.getCreatorName(), wrapIn.getNeedPicture(), selectTopInSection, viewSectionIds ,wrapIn.getStartTime() ,  wrapIn.getEndTime());
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
					subjectInfoList = subjectInfoServiceAdv.listSubjectWithSubjectTypeForPage(wrapIn.getSectionName() ,wrapIn.getSubjectType(), wrapIn.getSearchContent(), wrapIn.getForumId(), wrapIn.getMainSectionId(), wrapIn.getSectionId(), wrapIn.getCreatorName(), wrapIn.getNeedPicture(), selectTopInSection, selectTotal, viewSectionIds ,  wrapIn.getStartTime() ,  wrapIn.getEndTime() );
					if( subjectInfoList != null ){
						try {
							wraps_nonTop = Wo.copier.copy( subjectInfoList );
							SortTools.desc( wraps_nonTop, "latestReplyTime" );
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

		if( check ){
			if( page <= 0 ){
				page = 1;
			}
			if( count <= 0 ){
				count = 20;
			}
			int startIndex = ( page - 1 ) * count;
			int endIndex = page * count;
			int i = 0;
			for( ; wraps_top != null && i< wraps_top.size(); i++ ){
				if( i >= startIndex && i < endIndex ){
					wraps_out.add( wraps_top.get( i ) );
				}
			}
			for( int j=0; wraps_nonTop != null && j< wraps_nonTop.size(); j++ ){
				if( i+j >= startIndex && i+j < endIndex ){
					wraps_out.add( wraps_nonTop.get( j ) );
				}
			}
			if( ListTools.isNotEmpty( wraps_out ) ){
				for( Wo wo : wraps_out ) {
					//将带@形式的人员标识修改为人员的姓名并且赋值到xxShort属性里
					cutPersonNames( wo );
					//填充subjectCoent添加到结果集中
					addSubjectCoent( wo );
					//填充回复信息到结果集中
					addReplyInfo(wo);
				}
				try {
					result.setData( wraps_out );
					result.setCount( total + topTotal );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSubjectWrapOut( e );
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
	 *  latestReplyUserShort = "";
		bBSIndexSetterNameShort = "";
		screamSetterNameShort = "";
		originalSetterNameShort = "";
		creatorNameShort = "";
		auditorNameShort = "";

	 * @param subject
	 */
	private void cutPersonNames( Wo subject ) {
		if( subject != null ) {
			if(StringUtils.isBlank(subject.getNickName())){
				subject.setNickName(subject.getCreatorName());
			}
			if ( StringUtils.isNotEmpty( subject.getLatestReplyUser() )) {
				subject.setLatestReplyUserNickName(subject.getLatestReplyUser().split("@")[0]);
				try {
					if(configSettingService.useNickName()) {
						Business business = new Business(null);
						subject.setLatestReplyUserNickName(business.organization().person().getNickName(subject.getLatestReplyUser()));
					}
				} catch (Exception e) {
					logger.debug(e.getMessage());
				}
			}
			if( StringUtils.isNotEmpty( subject.getbBSIndexSetterName() ) ) {
				subject.setbBSIndexSetterNameShort( subject.getbBSIndexSetterName().split( "@" )[0]);
			}
			if( StringUtils.isNotEmpty( subject.getScreamSetterName() ) ) {
				subject.setScreamSetterNameShort( subject.getScreamSetterName().split( "@" )[0]);
			}
			if( StringUtils.isNotEmpty( subject.getOriginalSetterName() ) ) {
				subject.setOriginalSetterNameShort( subject.getOriginalSetterName().split( "@" )[0]);
			}
			if( StringUtils.isNotEmpty( subject.getCreatorName() ) ) {
				subject.setCreatorNameShort( subject.getCreatorName().split( "@" )[0]);
			}
			if( StringUtils.isNotEmpty( subject.getAuditorName() ) ) {
				subject.setAuditorNameShort( subject.getAuditorName().split( "@" )[0]);
			}
		}
	}

	private void addSubjectCoent( Wo conent ) throws Exception {
		if( conent != null ) {
			String subjectContent = subjectInfoServiceAdv.getSubjectContent(conent.getId());
			conent.setContent(subjectContent);
		}
	}

	private void addReplyInfo(Wo subjectInfo) throws Exception {
		List<WoBBSReplyInfo> bbsReplyInfoList = new ArrayList<>();
		String parm = "{\"subjectId\":\""+subjectInfo.getId()+"\",\"showSubReply\":\"fasle\"}";
		ActionResponse resp =  ThisApplication.context().applications()
				.putQuery(x_bbs_assemble_control.class, "reply/filter/list/page/1/count/1000", parm);
		if(StringUtils.isNotEmpty(resp.toString())){
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd HH:mm:ss")
					.create();
			JsonObject resJson =  gson.fromJson(resp.toJson(),JsonObject.class);
			if("success".equals(resJson.get("type").getAsString())){
				JsonArray bbsReplyInfoArr= resJson.get("data").getAsJsonArray();
				for(int i=0;i<bbsReplyInfoArr.size();i++){
					JsonObject bbsReplyInfoJson = bbsReplyInfoArr.get(i).getAsJsonObject();
					WoBBSReplyInfo bbsReplyInfo = gson.fromJson(bbsReplyInfoJson, WoBBSReplyInfo.class);
					bbsReplyInfoList.add(bbsReplyInfo);
				}
			}
			subjectInfo.setWoBBSReplyInfo(bbsReplyInfoList);
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

		@FieldDescribe( "贴子所属版块名称.必填" )
		private String sectionName = null;

		@FieldDescribe( "标题类别.必填" )
		private String subjectType = null;

		@FieldDescribe( "标题模糊搜索关键词" )
		private String searchContent = null;

		@FieldDescribe( "创建者名称." )
		private String creatorName = null;

		@FieldDescribe( "是否只查询有大图的贴子." )
		private Boolean needPicture = false;

		@FieldDescribe( "是否包含置顶贴." )
		private Boolean withTopSubject = false; // 是否包含置顶贴

		@FieldDescribe( "创建日期开始." )
		private Date startTime = null;

		@FieldDescribe( "创建日期结束." )
		private Date endTime = null;

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
			if( StringUtils.isNotEmpty( this.searchContent ) && this.searchContent.indexOf( "%" ) < 0 ){
				return "%" + searchContent + "%";
			}
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
		public String getSectionName() {
			return sectionName;
		}
		public void setSectionName(String sectionName) {
			this.sectionName = sectionName;
		}
		public String getSubjectType() {
			return subjectType;
		}
		public void setSubjectType(String subjectType) {
			this.subjectType = subjectType;
		}
		public String getVoteOptionId() {
			return voteOptionId;
		}
		public void setVoteOptionId(String voteOptionId) {
			this.voteOptionId = voteOptionId;
		}

		public String getCacheKey(EffectivePerson effectivePerson, Boolean isBBSManager) {
			StringBuffer sb = new StringBuffer();
			String pattern = "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
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
			if( StringUtils.isNotEmpty( subjectType )) {
				sb.append( "#" );
				sb.append( subjectType );
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

			if(  startTime != null ) {
				sb.append( "#" );
				sb.append( formatter.format(startTime));
			}
			if(  endTime != null ) {
				sb.append( "#" );
				sb.append( formatter.format(endTime));
			}

			sb.append( "#" );
			sb.append( needPicture );
			sb.append( "#" );
			sb.append( withTopSubject );
			return sb.toString();
		}

		public Date getStartTime() {
			return startTime;
		}
		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}
		public Date getEndTime() {
			return endTime;
		}
		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}



	}

	public static class Wo extends BBSSubjectInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier< BBSSubjectInfo, Wo > copier = WrapCopierFactory.wo( BBSSubjectInfo.class, Wo.class, null, JpaObject.FieldsInvisible);

		private  List<WoBBSReplyInfo> bbsReplyInfo = null;

		private List<WoSubjectAttachment> subjectAttachmentList;

		@FieldDescribe( "投票主题的所有投票选项列表." )
		private List<WoBBSVoteOptionGroup> voteOptionGroupList;

		private String content = null;

		private Long voteCount = 0L;

		private String pictureBase64 = null;

		@FieldDescribe("最新回复用户昵称")
		private String latestReplyUserNickName = "";

		@FieldDescribe( "首页推荐人姓名" )
		private String bBSIndexSetterNameShort = "";

		@FieldDescribe( "精华设置人姓名" )
		private String screamSetterNameShort = "";

		@FieldDescribe( "原创设置人姓名" )
		private String originalSetterNameShort = "";

		@FieldDescribe( "创建人姓名" )
		private String creatorNameShort = "";

		@FieldDescribe( "审核人姓名" )
		private String auditorNameShort = "";

		@FieldDescribe( "当前用户是否已经投票过." )
		private Boolean voted = false;

		public List<WoBBSReplyInfo> getWoBBSReplyInfo() {
			return bbsReplyInfo;
		}

		public void setWoBBSReplyInfo(List<WoBBSReplyInfo> bbsReplyInfo) {
			this.bbsReplyInfo = bbsReplyInfo;
		}

		public String getLatestReplyUserNickName() {
			return latestReplyUserNickName;
		}

		public void setLatestReplyUserNickName(String latestReplyUserNickName) {
			this.latestReplyUserNickName = latestReplyUserNickName;
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

	public static class WoSubjectAttachment extends BBSSubjectAttachment{

		private static final long serialVersionUID = -5076990764713538973L;


		public static WrapCopier< BBSSubjectAttachment, WoSubjectAttachment > copier = WrapCopierFactory.wo( BBSSubjectAttachment.class, WoSubjectAttachment.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoBBSVoteOptionGroup extends BBSVoteOptionGroup{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier< BBSVoteOptionGroup, WoBBSVoteOptionGroup > copier = WrapCopierFactory.wo( BBSVoteOptionGroup.class, WoBBSVoteOptionGroup.class, null, JpaObject.FieldsInvisible);

		private List<WoBBSVoteOption> voteOptions = null;

		public List<WoBBSVoteOption> getVoteOptions() {
			return voteOptions;
		}

		public void setVoteOptions(List<WoBBSVoteOption> voteOptions) {
			this.voteOptions = voteOptions;
		}
	}

	public static class WoBBSVoteOption extends BBSVoteOption{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier< BBSVoteOption, WoBBSVoteOption > copier = WrapCopierFactory.wo( BBSVoteOption.class, WoBBSVoteOption.class, null, JpaObject.FieldsInvisible);

		private Boolean voted = false;

		public Boolean getVoted() {
			return voted;
		}

		public void setVoted(Boolean voted) {
			this.voted = voted;
		}
	}

	public static class WoBBSReplyInfo extends BBSReplyInfo {
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<BBSReplyInfo, WoBBSReplyInfo> copier = WrapCopierFactory.wo(BBSReplyInfo.class, WoBBSReplyInfo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("创建人姓名")
		private String creatorNameShort = "";

		@FieldDescribe("审核人姓名")
		private String auditorNameShort = "";

		@FieldDescribe("下级回复的数量，默认为0")
		private Integer subReplyTotal = 0;

		@FieldDescribe("下级回复的数量，默认为0")
		private List<WoBBSReplyInfo> subReplies;

		public List<WoBBSReplyInfo> getSubReplies() {
			return subReplies;
		}

		public void setSubReplies(List<WoBBSReplyInfo> subReplies) {
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
