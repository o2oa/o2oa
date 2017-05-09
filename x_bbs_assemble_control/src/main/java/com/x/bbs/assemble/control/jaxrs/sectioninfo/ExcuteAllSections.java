package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionInfoProcessException;
import com.x.bbs.entity.BBSSectionInfo;

public class ExcuteAllSections extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteAllSections.class );
	
	protected ActionResult<List<WrapOutSectionInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutSectionInfo>> result = new ActionResult<>();
		List<WrapOutSectionInfo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		Boolean check = true;
		if( check ){
			//从数据库查询主版块列表
			try {
				sectionInfoList = sectionInfoServiceAdv.listAll();
				if (sectionInfoList == null) {
					sectionInfoList = new ArrayList<BBSSectionInfo>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionInfoProcessException( e, "查询所有的版块信息时发生异常." );
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