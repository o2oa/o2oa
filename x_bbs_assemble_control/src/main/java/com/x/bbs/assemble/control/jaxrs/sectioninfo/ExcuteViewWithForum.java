package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.MethodExcuteResult;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ForumIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ForumInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionInfoProcessException;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ExcuteViewWithForum extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteViewWithForum.class );
	
	protected ActionResult<List<WrapOutSectionInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String forumId ) throws Exception {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		List<WrapOutSectionInfo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> viewableSectionIds = new ArrayList<String>();
		BBSForumInfo forumInfo = new BBSForumInfo();
		Boolean check = true;
		MethodExcuteResult methodExcuteResult = null;
		
		if( check ){
			if( forumId == null || forumId.isEmpty() ){
				check = false;
				Exception exception = new ForumIdEmptyException();
				result.error( exception );
			}
		}
		if( check ){ //查询论坛信息是否存在
			try{
				forumInfo = forumInfoServiceAdv.get( forumId );
			}catch( Exception e ){
				check = false;
				Exception exception = new SectionInfoProcessException( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + forumId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( forumInfo == null ){
				check = false;
				Exception exception = new ForumInfoNotExistsException( forumId );
				result.error( exception );
			}
		}
		//如果不是匿名用户，则查询该用户所有能访问的版块信息
		if( check ){
			methodExcuteResult = userManagerService.getViewSectionIdsFromUserPermission( effectivePerson );
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
				Exception exception = new SectionInfoProcessException( e, "根据指定论坛分区ID查询所有主版块信息时发生异常.Forum:" + forumId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( sectionInfoList != null && sectionInfoList.size() > 0 ){
				try {
					wraps = WrapTools.sectionInfo_wrapout_copier.copy( sectionInfoList );
					result.setData(wraps);
				} catch (Exception e) {
					Exception exception = new SectionInfoProcessException( e, "系统在转换所有BBS版块信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}		
			}
		}
		return result;
	}

}