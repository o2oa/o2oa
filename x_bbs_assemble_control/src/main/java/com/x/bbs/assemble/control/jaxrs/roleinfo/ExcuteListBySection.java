package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.SectionNotExistsException;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ExcuteListBySection extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteListBySection.class );
	
	protected ActionResult<List<WrapOutRoleInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilter wrapIn ) throws Exception {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		BBSSectionInfo sectionInfo = null;
		Boolean check = true;
		if (check) {
			if (wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty()) {
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}
		if (check) {
			try {
				sectionInfo = sectionInfoServiceAdv.get(wrapIn.getSectionId());
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + wrapIn.getSectionId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (sectionInfo == null) {
				check = false;
				Exception exception = new SectionNotExistsException( wrapIn.getSectionId() );
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleBySectionId(wrapIn.getSectionId());
			} catch (Exception e) {
				check = false;
				result.error(e);
				Exception exception = new RoleInfoProcessException( e, "系统在根据版块ID查询角色信息列表时发生异常.Section:" + wrapIn.getSectionId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (roleInfoList != null) {
				try {
					wraps = WrapTools.roleInfo_wrapout_copier.copy(roleInfoList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "系统在转换所有BBS角色信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

}