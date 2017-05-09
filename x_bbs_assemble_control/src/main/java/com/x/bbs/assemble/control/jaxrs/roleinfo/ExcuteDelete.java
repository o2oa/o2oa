package com.x.bbs.assemble.control.jaxrs.roleinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.entity.BBSRoleInfo;

public class ExcuteDelete extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		BBSRoleInfo roleInfo = null;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new RoleInfoIdEmptyException();
				result.error( exception );
			}
		}
		if (check) {
			// 查询角色信息是否存在
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
			if (roleInfo == null) {
				check = false;
				Exception exception = new RoleInfoNotExistsException( id );
				result.error( exception );
			}
		}

		if (check) {
			try {
				roleInfoService.delete(id);
				wrap = new WrapOutId(id);
				result.setData(wrap);
				operationRecordService.roleOperation(effectivePerson.getName(), roleInfo, "DELETE", hostIp, hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "根据ID删除BBS角色信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}