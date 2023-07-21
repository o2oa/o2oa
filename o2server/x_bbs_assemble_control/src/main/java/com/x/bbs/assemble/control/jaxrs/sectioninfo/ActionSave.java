package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumCanNotCreateSection;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionPersonNotExists;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInsufficientPermission;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionNameEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionTypeCategoryInvalid;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Boolean check = true;
		List<String> names = null;
		String[] typeCategory = null;
		String personName = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo_sub = null;
		BBSSectionInfo sectionInfo = new BBSSectionInfo();
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Wi wrapIn = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionSectionInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		// 校验版块名称
		if (check) {
			if (wrapIn.getSectionName() == null || wrapIn.getSectionName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionSectionNameEmpty();
				result.error(exception);
			}
		}
		// 校验论坛信息是否有效，并且补充论坛名称
		if (check) {
			if (wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty()) {
				check = false;
				Exception exception = new ExceptionForumIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get(wrapIn.getForumId());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e,
						"系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + wrapIn.getForumId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (forumInfo == null) {
				// 论坛信息不存在
				check = false;
				Exception exception = new ExceptionForumInfoNotExists(wrapIn.getForumId());
				result.error(exception);
			} else {
				// 补充论坛名称
				wrapIn.setForumName(forumInfo.getForumName());
			}
		}
		
		// 判断论坛是否允许用户创建版块
		if (check) {
			if (!forumInfo.getSectionCreateAble()) {
				check = false;
				Exception exception = new ExceptionForumCanNotCreateSection(forumInfo.getForumName());
				result.error(exception);
			}
		}
		
		// 判断用户是否有权限进行论坛信息新增或者更新：
		// 1、系统管理员 2、论坛设置的管理员
		if ( check ) {
			if ( !ThisApplication.isForumManager( effectivePerson, forumInfo ) ) {//无权限进行版块删除操作
				check = false;
				String forumName = "论坛不存在或者已删除";
				if( forumInfo != null ) {
					forumName = forumInfo.getForumName();
				}
				Exception exception = new ExceptionSectionInsufficientPermission( effectivePerson.getDistinguishedName(), forumName );
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if (wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty()) {
				wrapIn.setSubjectType(forumInfo.getSubjectType());
			}
			if (wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty()) {
				wrapIn.setSubjectType("新闻|讨论");
			}
		}
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getTypeCategory() )) {
				typeCategory = wrapIn.getTypeCategory().split("\\|");
				if (typeCategory != null && typeCategory.length > 0) {
					for (String category : typeCategory) {
						if (!"信息".equals(category) && !"问题".equals(category) && !"投票".equals(category)) {
							check = false;
							Exception exception = new ExceptionSectionTypeCategoryInvalid(category);
							result.error(exception);
						}
					}
				}
			} else {
				wrapIn.setTypeCategory(forumInfo.getTypeCategory());
			}
		}
		// 校验版块管理员（版主）是否存在
		if (check) {
			if ( ListTools.isNotEmpty( wrapIn.getModeratorNames() )) {
				// 判断指定的用户是否存在
				names = wrapIn.getModeratorNames();
				for (String name : names) {
					try {
						personName = userManagerService.getPersonNameByFlag( name );
						if ( personName == null ) {
							check = false;
							Exception exception = new ExceptionPersonNotExists(name);
							result.error(exception);
							break;
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionSectionInfoProcess(e, "系统根据人员唯一标识查询人员信息时发生异常.Name:" + name);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
						break;
					}
				}
			} else {
				wrapIn.addModeratorName( effectivePerson.getDistinguishedName() );
			}
		}
		if (check) {
			wrapIn.setCreatorName(effectivePerson.getDistinguishedName());
		}
		if (check) {
			try {
				sectionInfo = Wi.copier.copy( wrapIn );
				if( StringUtils.isNotEmpty( wrapIn.getId() )) {
					sectionInfo.setId( wrapIn.getId() );
				}
				if (StringUtils.isEmpty( sectionInfo.getId() )) {
					sectionInfo.setId(BBSSectionInfo.createId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "将用户传入的信息转换为一个版块信息对象时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			// 如果主版的ID为空，或者主版ID与当前ID一致，则版块为一级版块，主版块
			if (sectionInfo.getMainSectionId() == null || sectionInfo.getMainSectionId().isEmpty() || sectionInfo.getId().equals(sectionInfo.getMainSectionId())) {
				sectionInfo.setMainSectionId(sectionInfo.getId());
				sectionInfo.setMainSectionName(sectionInfo.getSectionName());
				sectionInfo.setSectionLevel("主版块");
			} else {
				try {
					sectionInfo_sub = sectionInfoServiceAdv.get(sectionInfo.getMainSectionId());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSectionInfoProcess(e, "根据指定主版ID查询子版块信息时发生异常.MainId:" + sectionInfo.getMainSectionId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
				if (check) {
					if (sectionInfo_sub != null) {
						sectionInfo.setMainSectionId(sectionInfo_sub.getId());
						sectionInfo.setMainSectionName(sectionInfo_sub.getSectionName());
						sectionInfo.setSectionLevel("子版块");
					} else {
						check = false;
						Exception exception = new ExceptionSectionNotExists(sectionInfo.getMainSectionId());
						result.error(exception);
					}
				}
			}
		}

		if (check) {
			List<String> arrayList = new ArrayList<>(); 
			if( StringUtils.equals( wrapIn.getSectionVisible(), "根据权限" )) {
				if( StringUtils.isNotEmpty( wrapIn.getSectionVisibleResult() )) {
					arrayList.clear();
					Collections.addAll(arrayList, wrapIn.getSectionVisibleResult().split( "," ));
					sectionInfo.setVisiblePermissionList( arrayList );
				}
			}else {
				sectionInfo.setVisiblePermissionList( new ArrayList<>() );
			}
			
			if( StringUtils.equals( wrapIn.getReplyPublishAble(), "根据权限" )) {
				if( StringUtils.isNotEmpty( wrapIn.getReplyPublishResult() )) {
					arrayList.clear();
					Collections.addAll(arrayList, wrapIn.getReplyPublishResult().split( "," ));
					sectionInfo.setReplyPermissionList( arrayList );
				}
			}else {
				sectionInfo.setReplyPermissionList( new ArrayList<>() );
			}

			if( StringUtils.equals( wrapIn.getSubjectPublishAble(), "根据权限" )) {
				if( StringUtils.isNotEmpty( wrapIn.getSubjectPublishResult() )) {
					arrayList.clear();
					Collections.addAll(arrayList, wrapIn.getSubjectPublishResult().split( "," ));
					sectionInfo.setPublishPermissionList( arrayList );
				}
			}else {
				sectionInfo.setPublishPermissionList( new ArrayList<>() );
			}
		}
		
		if (check) {
			try {
				if( sectionInfo.getCreateTime() == null ) {
					sectionInfo.setCreateTime( new Date() );
				}
				if( sectionInfo.getUpdateTime() == null ) {
					sectionInfo.setUpdateTime( sectionInfo.getCreateTime() );
				}
				sectionInfo = sectionInfoServiceAdv.save( sectionInfo );				
				wo.setId( sectionInfo.getId() );

				CacheManager.notify( BBSForumInfo.class );
				CacheManager.notify( BBSSectionInfo.class );
				CacheManager.notify( BBSSubjectInfo.class );
				
				if ( sectionInfo.getCreateTime().compareTo( sectionInfo.getUpdateTime() ) == 0 ) {
					operationRecordService.sectionOperation(effectivePerson.getDistinguishedName(), sectionInfo, "CREATE", hostIp, hostName);
				} else {
					operationRecordService.sectionOperation(effectivePerson.getDistinguishedName(), sectionInfo, "MODIFY", hostIp, hostName);	
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "保存版块信息时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				// 论坛信息添加成功，继续添加权限和角色信息
				permissionInfoService.createSectionPermission(sectionInfo);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system create forum permission info got an exception!");
				logger.error(e);

			}
		}

		if (check) {
			try {
				// 论坛信息添加成功，继续添加权限和角色信息
				roleInfoService.createSectionRole( effectivePerson.getDistinguishedName(), sectionInfo);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system create section role info got an exception!");
				logger.error(e);
			}
		}
		if (check) {
			if (sectionInfo.getMainSectionId() != null
					&& !sectionInfo.getMainSectionId().equalsIgnoreCase(sectionInfo.getId())) {
				try {
					// 如果该版块是子版块，那么主版块的角色也需要重新维护
					roleInfoService.createSectionRole( effectivePerson.getDistinguishedName(), sectionInfo.getMainSectionId());
				} catch (Exception e) {
					check = false;
					result.error(e);
					logger.warn("system create main section role info got an exception!");
					logger.error(e);
				}
			}
		}
		if (check) {
			try {
				// 该版块所处的论坛的角色需要重新维护
				roleInfoService.createForumRole( effectivePerson, sectionInfo.getForumId());
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system create forum role info got an exception!");
				logger.error(e);
			}
		}
		if (check) {// 检查版主权限的设置
			try {
				sectionInfoServiceAdv.checkSectionManager(sectionInfo);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system bind role for section manager got an exception!");
				logger.error(e);
			}
		}
		if (check) {
			if (sectionInfo.getMainSectionId() != null
					&& !sectionInfo.getMainSectionId().equalsIgnoreCase(sectionInfo.getId())) {
				try {
					sectionInfoServiceAdv.checkSectionManager(sectionInfo.getMainSectionId());
				} catch (Exception e) {
					check = false;
					result.error(e);
					logger.warn("system bind role for section manager got an exception!");
					logger.error(e);
				}
			}
		}
		if (check) {
			try {
				forumInfoServiceAdv.checkForumManager(forumInfo);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system bind role for section manager got an exception!");
				logger.error(e);
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wi extends BBSSectionInfo{

		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< Wi, BBSSectionInfo > copier = WrapCopierFactory.wi( Wi.class, BBSSectionInfo.class, null, JpaObject.FieldsUnmodify);
		
		@FieldDescribe("版块访问权限列表，用于接收参数.")
		private String sectionVisibleResult ;
		
		@FieldDescribe("版块访问权限列表，用于接收参数.")
		private String replyPublishResult ;
		
		@FieldDescribe("版块访问权限列表，用于接收参数.")
		private String subjectPublishResult ;

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
	}

	
	public static class Wo extends WoId {

	}
}