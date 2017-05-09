package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionNotExistsException;
import com.x.bbs.entity.BBSSectionInfo;

public class ExcuteGet extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutSectionInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutSectionInfo> result = new ActionResult<>();
		WrapOutSectionInfo wrap = null;
		BBSSectionInfo sectionInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			try {
				sectionInfo = sectionInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( sectionInfo != null ){
				try {
					wrap = WrapTools.sectionInfo_wrapout_copier.copy( sectionInfo );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					Exception exception = new SectionInfoProcessException( e, "系统在转换所有BBS版块信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				Exception exception = new SectionNotExistsException( id );
				result.error( exception );
			}
		}
		return result;
	}

}