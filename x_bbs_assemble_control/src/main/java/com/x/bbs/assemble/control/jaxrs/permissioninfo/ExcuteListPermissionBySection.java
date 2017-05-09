package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.PermissionInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.SectionIdEmptyException;
import com.x.bbs.entity.BBSPermissionInfo;

public class ExcuteListPermissionBySection extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteListPermissionBySection.class );
	
	protected ActionResult<List<WrapOutPermissionInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String sectionId ) throws Exception {
		ActionResult<List<WrapOutPermissionInfo>> result = new ActionResult<>();
		List<WrapOutPermissionInfo> wraps = new ArrayList<>();
		List<BBSPermissionInfo> permissionInfoList = null;
		Boolean check = true;
		
		if( check ){
			if( sectionId == null || sectionId.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
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
				Exception exception = new PermissionInfoProcessException( e, "根据指定的版块列示所有的权限信息时时发生异常.Section:" + sectionId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			try {
				wraps = WrapTools.permissionInfo_wrapout_copier.copy( permissionInfoList );
				result.setData( wraps );
			} catch (Exception e) {
				Exception exception = new PermissionInfoProcessException( e, "将查询结果转换为可输出的数据信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}