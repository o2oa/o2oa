package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.BindRoleCodeEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSUserRole;

public class ExcuteLisstBindObjectByRoleCode extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteLisstBindObjectByRoleCode.class );
	
	protected ActionResult<List<WrapOutUserRole>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilter wrapIn ) throws Exception {
		ActionResult<List<WrapOutUserRole>> result = new ActionResult<>();
		List<WrapOutUserRole> wraps = null;
		List<BBSUserRole> userRoleList = null;
		BBSRoleInfo roleInfo = null;
		Boolean check = true;
		if (check) {
			if (wrapIn.getBindRoleCode() == null || wrapIn.getBindRoleCode().isEmpty()) {
				check = false;
				Exception exception = new BindRoleCodeEmptyException();
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfo = roleInfoService.getByRoleCode( wrapIn.getBindRoleCode() );
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在根据编码获取BBS角色信息时发生异常！Code:" + wrapIn.getBindRoleCode() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (roleInfo == null) {
				check = false;
				Exception exception = new RoleInfoNotExistsException( wrapIn.getBindRoleCode() );
				result.error( exception );
			}
		}
		if (check) {
			try {
				userRoleList = roleInfoService.listUserRoleByRoleCode(roleInfo.getRoleCode());
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在根据角色编码查询角色绑定信息列表时发生异常.Code:" + wrapIn.getBindRoleCode() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (userRoleList != null) {
				try {
					wraps = WrapTools.userRole_wrapout_copier.copy(userRoleList);
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