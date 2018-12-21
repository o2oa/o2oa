package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionAllListSubSectionByMainSectionId extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAllListSubSectionByMainSectionId.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String sectionId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSSectionInfo> sectionInfoList = null;
		BBSSectionInfo sectionInfo = new BBSSectionInfo();
		Boolean check = true;	
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
		if( check ){
			try {
				sectionInfoList = sectionInfoServiceAdv.listSubSectionByMainSectionId( sectionId );
				if (sectionInfoList == null) {
					sectionInfoList = new ArrayList<BBSSectionInfo>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess( e, "根据指定主版ID查询子版块信息时发生异常.MainId:" + sectionId );
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