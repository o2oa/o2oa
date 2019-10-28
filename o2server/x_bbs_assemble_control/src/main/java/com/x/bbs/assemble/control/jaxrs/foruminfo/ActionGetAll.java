package com.x.bbs.assemble.control.jaxrs.foruminfo;

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
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoProcess;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

import net.sf.ehcache.Element;

public class ActionGetAll extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetAll.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			String cacheKey = "forum#all";
			Element element = cache.get( cacheKey );
			if ((null != element) && (null != element.getObjectValue())) {
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) element.getObjectValue();
				result.setData( result_cache.getData() );
				result.setCount( 1L);
			} else {
				//继续进行数据查询
				result = getForumAllQueryResult( request, effectivePerson );
				cache.put(new Element(cacheKey, result ));
			}
		}
		return result;
	}

	private ActionResult<List<Wo>> getForumAllQueryResult(HttpServletRequest request, EffectivePerson effectivePerson) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSForumInfo> forumInfoList = null;
		Boolean check = true;
		
		if( check ){
			//从数据库查询论坛列表
			try {
				forumInfoList = forumInfoServiceAdv.listAll();
			} catch (Exception e) {
				Exception exception = new ExceptionForumInfoProcess( e, "系统在获取所有BBS论坛分区信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			if( ListTools.isNotEmpty( forumInfoList ) ){
				try {
					wraps = Wo.copier.copy( forumInfoList );
				} catch (Exception e) {
					Exception exception = new ExceptionForumInfoProcess( e, "系统在转换所有BBS论坛分区信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				
			}
		}
		
		//TODO 为了不改变前端的逻辑，此处将List转为String进行输出，逗号分隔
		if( check ){
			if( ListTools.isNotEmpty( wraps ) ){
				for( Wo wo : wraps ) {
					wo.setForumVisibleResult( wo.getVisiblePermissionList() );
					wo.setForumManagerName( wo.transferStringListToString( wo.getForumManagerList() ));
				}
			}
		}
		result.setData( wraps );
		result.setCount( Long.parseLong( wraps.size() + "") );
		return result;
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
	
	public static class WoSectionInfo extends BBSSectionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		//版块的子版块信息列表
		private List<WoSectionInfo> subSections = null;

		public List<WoSectionInfo> getSubSections() {
			return subSections;
		}
		public void setSubSections(List<WoSectionInfo> subSections) {
			this.subSections = subSections;
		}	
	}
}