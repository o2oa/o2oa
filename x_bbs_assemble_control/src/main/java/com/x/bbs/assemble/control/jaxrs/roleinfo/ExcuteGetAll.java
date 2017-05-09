package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.entity.BBSRoleInfo;

public class ExcuteGetAll extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGetAll.class );
	
	protected ActionResult<List<WrapOutRoleInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = new ArrayList<>();
		List<BBSRoleInfo> roleInfoList = null;
		Boolean check = true;

		if (check) {
			try {
				roleInfoList = roleInfoService.listAllRoleInfo();
				if (roleInfoList == null) {
					roleInfoList = new ArrayList<BBSRoleInfo>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在获取所有BBS角色信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (roleInfoList != null && roleInfoList.size() > 0) {
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