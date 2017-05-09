package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.WrapOutPermissionInfo;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSRoleInfo;

public class ExcuteGet extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutRoleInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutRoleInfo> result = new ActionResult<>();
		WrapOutRoleInfo wrap = null;
		List<WrapOutPermissionInfo> permissionWraps = null;
		BBSRoleInfo roleInfo = null;
		List<BBSPermissionInfo> permissionList = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new RoleInfoIdEmptyException();
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfo = roleInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在根据ID获取BBS角色信息时发生异常！ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if( roleInfo == null ){
				check = false;
				Exception exception = new RoleInfoNotExistsException( id );
				result.error( exception );
			}
		}
		if (check) {
			// 如果角色信息存在，然后再查询该角色所拥有的所有权限信息
			try {
				permissionList = permissionInfoService.listPermissionByRoleCode(roleInfo.getRoleCode());
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在根据角色编码查询权限信息列表时发生异常！Code:" + roleInfo.getRoleCode() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (permissionList != null && permissionList.size() > 0) {
				try {
					permissionWraps = WrapTools.permissionInfo_wrapout_copier.copy(permissionList);
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "系统在转换所有BBS权限信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			try {
				wrap = WrapTools.roleInfo_wrapout_copier.copy(roleInfo);
				wrap.setPermissions(permissionWraps);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在转换所有BBS角色信息为输出对象时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}