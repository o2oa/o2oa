package com.x.bbs.assemble.control.jaxrs.permissioninfo;

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
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionPermissionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionListPermissionBySection extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListPermissionBySection.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String sectionId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSPermissionInfo> permissionInfoList = null;
		Boolean check = true;
		
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				permissionInfoList = permissionInfoService.listPermissionBySection( sectionId );
				if( permissionInfoList == null ){
					permissionInfoList = new ArrayList<BBSPermissionInfo>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionPermissionInfoProcess( e, "根据指定的版块列示所有的权限信息时时发生异常.Section:" + sectionId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			try {
				wraps = Wo.copier.copy( permissionInfoList );
				result.setData( wraps );
			} catch (Exception e) {
				Exception exception = new ExceptionPermissionInfoProcess( e, "将查询结果转换为可输出的数据信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

public static class Wo extends BBSPermissionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< BBSPermissionInfo, Wo > copier = WrapCopierFactory.wo( BBSPermissionInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		//论坛版块列表
		private List<WoSectionInfo> sections = null;

		public List<WoSectionInfo> getSections() {
			return sections;
		}

		public void setSections(List<WoSectionInfo> sections) {
			this.sections = sections;
		}
	}

	public static class WoSectionInfo extends BBSSectionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
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