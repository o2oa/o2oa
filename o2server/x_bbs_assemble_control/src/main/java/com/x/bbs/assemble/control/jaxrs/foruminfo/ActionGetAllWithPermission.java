package com.x.bbs.assemble.control.jaxrs.foruminfo;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.MethodExcuteResult;
import com.x.bbs.assemble.control.jaxrs.foruminfo.ActionGetAll.WoSectionInfo;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoProcess;
import com.x.bbs.entity.BBSForumInfo;


public class ActionGetAllWithPermission extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetAllWithPermission.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Boolean check = true;
		Boolean isBBSManager = false;
		
		if ( check ) {
			isBBSManager = ThisApplication.isBBSManager(effectivePerson);
		}
		
		if( check ) {
			Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), effectivePerson.getDistinguishedName(), isBBSManager);
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if( optional.isPresent() ){
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) optional.get();
				result.setData( result_cache.getData() );
				result.setCount( result_cache.getCount() );
			} else {
				//继续进行数据查询;
				result = getForumWithPermissionQueryResult( request, effectivePerson, isBBSManager );
				CacheManager.put( cacheCategory, cacheKey, result );
			}
		}		
		return result;
	}

	@SuppressWarnings("unchecked")
	private ActionResult<List<Wo>> getForumWithPermissionQueryResult(HttpServletRequest request, EffectivePerson effectivePerson, Boolean isBBSManager) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		Boolean check = true;
		List<BBSForumInfo> forumInfoList = null;
		List<String> forumIds = null;
		List<String> ids = new ArrayList<String>();
		MethodExcuteResult methodExcuteResult = null;
		
		if( check ){
			//查询公开的论坛
			try {
				forumIds = forumInfoServiceAdv.listAllPublicForumIds();
				if( ListTools.isNotEmpty( forumIds ) ){
					for( String id : forumIds ){
						ids.add( id );
					}
				}
			} catch (Exception e) {
				Exception exception = new ExceptionForumInfoProcess( e, "系统查询所有公开的论坛信息列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//查询有权限访问的论坛
			methodExcuteResult = UserPermissionService.getViewForumIdsFromUserPermission( effectivePerson );
			if( methodExcuteResult.getSuccess() ){
				if( methodExcuteResult.getBackObject() != null ){
					for( String id : ( List<String> )methodExcuteResult.getBackObject() ){
						if( !ids.contains( id )){
							ids.add( id );
						}
					}
				}
			}else{
				result.error( methodExcuteResult.getError() );
				logger.error( methodExcuteResult.getError(), effectivePerson, request, null);
			}
		}
		
		if( check ){//从数据库查询论坛列表
			try {
				forumInfoList = forumInfoServiceAdv.listAllViewAbleForumWithUserPermission( ids );
				if( forumInfoList == null ){
					forumInfoList = new ArrayList<BBSForumInfo>();
				}else {
					//排除“停用”
					ArrayList<BBSForumInfo> bbsForumInfoList = new ArrayList<BBSForumInfo>();
					Iterator<BBSForumInfo> it = forumInfoList.iterator();
					while (it.hasNext()) {
						BBSForumInfo forumInfo = it.next();
						if(!forumInfo.getForumStatus().equalsIgnoreCase("停用")) {
							bbsForumInfoList.add(forumInfo);
						}
					}
					forumInfoList = bbsForumInfoList;
				}
				
				
			} catch (Exception e) {
				Exception exception = new ExceptionForumInfoProcess( e, "根据ID列表查询论坛信息列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){//转换论坛列表为输出格式
			if( ListTools.isNotEmpty( forumInfoList ) ){
				try {
					wraps = Wo.copier.copy( forumInfoList );
				} catch (Exception e) {
					Exception exception = new ExceptionForumInfoProcess( e, "系统将论坛信息对象转换为输出数据时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		//TODO 为了不改变前端的逻辑，此处将人员List转为String进行输出，逗号分隔
		if( check ){
			if( ListTools.isNotEmpty( wraps ) ){
				for( Wo wo : wraps ) {
					wo.setForumVisibleResult( wo.getVisiblePermissionList() );
					wo.setForumManagerName( wo.transferStringListToString( wo.getForumManagerList() ));
				}
			}
		}
		result.setData( wraps );
		return result;
	}

	private String getCacheKey(EffectivePerson effectivePerson, Boolean isBBSManager) {
		StringBuffer sb = new StringBuffer();
		if( StringUtils.isNotEmpty( effectivePerson.getDistinguishedName() )) {
			sb.append( effectivePerson.getDistinguishedName() );
		}
		if( StringUtils.isNotEmpty( effectivePerson.getDistinguishedName() )) {
			sb.append( "#" );
			sb.append( isBBSManager );
		}
		sb.append( "#forum#view" );
		return sb.toString();
	}

	public static class Wo extends BBSForumInfo{
		
		@FieldDescribe("字符串形式输出的管理员信息，逗号(,)分隔.")
		private String forumManagerName = null;
	
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier< BBSForumInfo, Wo > copier = WrapCopierFactory.wo( BBSForumInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		@FieldDescribe("版块访问权限列表，用于接收参数.")
		private String forumVisibleResult ;
		
		//论坛版块列表
		@FieldDescribe("版块列表.")
		private List<WoSectionInfo> sections = null;

		public List<WoSectionInfo> getSections() {
			return sections;
		}

		public void setSections(List<WoSectionInfo> sections) {
			this.sections = sections;
		}

		public String getForumManagerName() {
			return forumManagerName;
		}

		public void setForumManagerName( String forumManagerName ) {
			this.forumManagerName = forumManagerName;
		}
		
		public String getForumVisibleResult() {
			return forumVisibleResult;
		}
		public void setForumVisibleResult(String forumVisibleResult) {
			this.forumVisibleResult = forumVisibleResult;
		}
		
		public void setForumVisibleResult( List<String> list ) {
			this.forumVisibleResult = transferStringListToString(list);
		}
		
		public String transferStringListToString( List<String> list ) {
			StringBuffer sb = new StringBuffer();
			if( ListTools.isNotEmpty( list )) {
				for( String str : list ) {
					if( StringUtils.isNotEmpty( sb.toString() )) {
						sb.append(",");
					}
					sb.append(str);
				}
			}
			return sb.toString();
		}
		
	}
}