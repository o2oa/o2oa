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
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionNotExistsException;
import com.x.bbs.entity.BBSSectionInfo;

public class ExcuteListSubSectionByMainSectionId extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteListSubSectionByMainSectionId.class );
	
	protected ActionResult<List<WrapOutSectionInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String sectionId ) throws Exception {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		List<WrapOutSectionInfo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		List<String> viewableSectionIds = new ArrayList<String>();
		BBSSectionInfo sectionInfo = new BBSSectionInfo();
		Boolean check = true;
		MethodExcuteResult methodExcuteResult = null;		
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}		
		if( check ){
			try{
				sectionInfo = sectionInfoServiceAdv.get( sectionId );
			}catch( Exception e ){
				check = false;
				Exception exception = new SectionInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + sectionId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				Exception exception = new SectionNotExistsException( sectionId );
				result.error( exception );
			}
		}
		//如果不是匿名用户，则查询该用户所有能访问的版块信息
		if (check) {
			methodExcuteResult = userManagerService.getViewSectionIdsFromUserPermission( effectivePerson );
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
				Exception exception = new SectionInfoProcessException( e, "根据指定主版ID查询子版块信息时发生异常.MainId:" + sectionId );
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