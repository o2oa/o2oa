package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.MethodExcuteResult;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInfoProcess;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionViewWithForum extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionViewWithForum.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String forumId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Boolean check = true;
		Boolean isBBSManager = false;
		
		if ( check ) {
			isBBSManager = ThisApplication.isBBSManager(effectivePerson);
		}
		
		if( check ) {
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), isBBSManager, forumId);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) optional.get();
				result.setData( result_cache.getData() );
				result.setCount( result_cache.getCount() );
			} else {
				//继续进行数据查询
				result = getSectionQueryResult( request, effectivePerson, forumId, isBBSManager );
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}		
		return result;		
	}

	private String getCacheKey(EffectivePerson effectivePerson, Boolean isBBSManager, String forumId) {
		StringBuffer sb = new StringBuffer();
		if( StringUtils.isNotEmpty( effectivePerson.getDistinguishedName() )) {
			sb.append( effectivePerson.getDistinguishedName() );
		}
		if( StringUtils.isNotEmpty( effectivePerson.getDistinguishedName() )) {
			sb.append( "#" );
			sb.append( isBBSManager );
		}
		sb.append( "#forum#" );
		sb.append( forumId );
		sb.append( "#view" );
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private ActionResult<List<Wo>> getSectionQueryResult(HttpServletRequest request, EffectivePerson effectivePerson, String forumId, Boolean isBBSManager) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> viewableSectionIds = new ArrayList<String>();
		BBSForumInfo forumInfo = new BBSForumInfo();
		Boolean check = true;
		MethodExcuteResult methodExcuteResult = null;
		
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				Exception exception = new ExceptionForumIdEmpty();
				result.error( exception );
			}
		}
		if( check ){ //查询论坛信息是否存在
			try{
				forumInfo = forumInfoServiceAdv.get( forumId );
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionSectionInfoProcess( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + forumId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( forumInfo == null ){
				check = false;
				Exception exception = new ExceptionForumInfoNotExists( forumId );
				result.error( exception );
			}
		}
		//如果不是匿名用户，则查询该用户所有能访问的版块信息
		if( check ){
			methodExcuteResult = UserPermissionService.getViewSectionIdsFromUserPermission( effectivePerson );
			if( methodExcuteResult.getSuccess() ){
				if( methodExcuteResult.getBackObject() != null ){
					viewableSectionIds = (List<String>)methodExcuteResult.getBackObject();
				}else{
					viewableSectionIds = new ArrayList<String>();
				}
			}else{
				result.error( methodExcuteResult.getError() );
				logger.warn( methodExcuteResult.getMessage() );
			}
		}
		if( check ){//从数据库查询主版块列表
			try {
				sectionInfoList = sectionInfoServiceAdv.viewMainSectionByForumId( forumId, viewableSectionIds );
				if (sectionInfoList == null) {
					sectionInfoList = new ArrayList<BBSSectionInfo>();
				}
			} catch (Exception e) {
				result.error(e);
				Exception exception = new ExceptionSectionInfoProcess( e, "根据指定论坛分区ID查询所有主版块信息时发生异常.Forum:" + forumId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( sectionInfoList != null && sectionInfoList.size() > 0 ){
				try {
					wraps = Wo.copier.copy( sectionInfoList );
					result.setData(wraps);
				} catch (Exception e) {
					Exception exception = new ExceptionSectionInfoProcess( e, "系统在转换所有BBS版块信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}		
			}
		}
		return result;
	}

	public static class Wo extends BBSSectionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< BBSSectionInfo, Wo > copier = WrapCopierFactory.wo( BBSSectionInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		//版块的子版块信息列表
		private List<Wo> subSections = null;

		public List<Wo> getSubSections() {
			return subSections;
		}
		public void setSubSections(List<Wo> subSections) {
			this.subSections = subSections;
		}	
	}
}