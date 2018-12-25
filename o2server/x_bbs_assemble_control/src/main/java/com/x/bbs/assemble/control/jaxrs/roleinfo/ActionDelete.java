package com.x.bbs.assemble.control.jaxrs.roleinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoIdEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.entity.BBSRoleInfo;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		BBSRoleInfo roleInfo = null;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionRoleInfoIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			// 查询角色信息是否存在
			try {
				roleInfo = roleInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess(e, "系统在根据ID获取BBS角色信息时发生异常！ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (roleInfo == null) {
				check = false;
				Exception exception = new ExceptionRoleInfoNotExists(id);
				result.error(exception);
			}
		}

		if (check) {
			try {
				roleInfoService.delete(id);
				wrap = new Wo();
				wrap.setId( id );
				result.setData(wrap);
				operationRecordService.roleOperation(effectivePerson.getDistinguishedName(), roleInfo, "DELETE", hostIp,
						hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess(e, "根据ID删除BBS角色信息时发生异常.ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}