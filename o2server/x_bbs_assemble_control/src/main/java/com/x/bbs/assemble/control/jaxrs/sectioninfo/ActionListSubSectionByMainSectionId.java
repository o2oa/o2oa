package com.x.bbs.assemble.control.jaxrs.sectioninfo;

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
import com.x.bbs.assemble.control.jaxrs.MethodExcuteResult;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionListSubSectionByMainSectionId extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListSubSectionByMainSectionId.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String sectionId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> viewableSectionIds = new ArrayList<String>();
		BBSSectionInfo sectionInfo = new BBSSectionInfo();
		Boolean check = true;
		MethodExcuteResult methodExcuteResult = null;		
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error( exception );
			}
		}		
		if( check ){
			try{
				sectionInfo = sectionInfoServiceAdv.get( sectionId );
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionSectionInfoProcess( e, "根据指定ID查询版块信息时发生异常.ID:" + sectionId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				Exception exception = new ExceptionSectionNotExists( sectionId );
				result.error( exception );
			}
		}
		//如果不是匿名用户，则查询该用户所有能访问的版块信息
		if (check) {
			methodExcuteResult = UserPermissionService.getViewSectionIdsFromUserPermission( effectivePerson );
			if (methodExcuteResult.getSuccess()) {
				if (methodExcuteResult.getBackObject() != null) {
					viewableSectionIds = (List<String>) methodExcuteResult.getBackObject();
				} else {
					viewableSectionIds = new ArrayList<String>();
				}
			} else {
				result.error(methodExcuteResult.getError());
				logger.warn(methodExcuteResult.getMessage());
			}
		}
		if( check ){
			try {
				sectionInfoList = sectionInfoServiceAdv.viewSubSectionByMainSectionId( sectionId, viewableSectionIds );
			} catch (Exception e) {
				result.error(e);
				Exception exception = new ExceptionSectionInfoProcess( e, "根据指定主版ID查询子版块信息时发生异常.MainId:" + sectionId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ){
			if( ListTools.isNotEmpty( sectionInfoList ) ){
				try {
					wraps = Wo.copier.copy( sectionInfoList );
					if( ListTools.isNotEmpty( wraps )) {
						for( Wo wrap : wraps ) {
							wrap.setSectionVisibleResult( wrap.getVisiblePermissionList() );
							wrap.setSubjectPublishResult( wrap.getPublishPermissionList() );
							wrap.setReplyPublishResult( wrap.getReplyPermissionList() );
						}	
					}
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
		
		@FieldDescribe("版块访问权限列表，用于接收参数.")
		private String sectionVisibleResult ;
		
		@FieldDescribe("版块访问权限列表，用于接收参数.")
		private String replyPublishResult ;
		
		@FieldDescribe("版块访问权限列表，用于接收参数.")
		private String subjectPublishResult ;
		
		//版块的子版块信息列表
		@FieldDescribe("子版块列表.")
		private List<Wo> subSections = null;

		public List<Wo> getSubSections() {
			return subSections;
		}
		public void setSubSections(List<Wo> subSections) {
			this.subSections = subSections;
		}
		public String getSectionVisibleResult() {
			return sectionVisibleResult;
		}
		public void setSectionVisibleResult(String sectionVisibleResult) {
			this.sectionVisibleResult = sectionVisibleResult;
		}
		public String getReplyPublishResult() {
			return replyPublishResult;
		}
		public void setReplyPublishResult(String replyPublishResult) {
			this.replyPublishResult = replyPublishResult;
		}
		public String getSubjectPublishResult() {
			return subjectPublishResult;
		}
		public void setSubjectPublishResult(String subjectPublishResult) {
			this.subjectPublishResult = subjectPublishResult;
		}
		
		public void setSubjectPublishResult(List<String> list) {
			this.subjectPublishResult = transferStringListToString(list);
		}
		public void setReplyPublishResult(List<String> list) {
			this.replyPublishResult = transferStringListToString(list);
		}
		public void setSectionVisibleResult(List<String> list) {
			this.sectionVisibleResult = transferStringListToString(list);
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