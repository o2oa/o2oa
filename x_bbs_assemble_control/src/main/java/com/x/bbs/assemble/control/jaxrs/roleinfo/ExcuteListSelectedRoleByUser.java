package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.BindUserNameEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.PersonNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.organization.core.express.wrap.WrapPerson;

public class ExcuteListSelectedRoleByUser extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteListSelectedRoleByUser.class );
	
	protected ActionResult<List<WrapOutRoleInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilter wrapIn ) throws Exception {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		WrapPerson wrapPerson = null;
		Boolean check = true;
		if (check) {
			if (wrapIn.getUserName() == null || wrapIn.getUserName().isEmpty()) {
				check = false;
				Exception exception = new BindUserNameEmptyException();
				result.error( exception );
			}
		}
		if (check) {
			try {
				wrapPerson = userManagerService.getPersonByName( wrapIn.getUserName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "人员信息查询时发生异常！Person:" + wrapIn.getUserName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if ( wrapPerson == null) {
				check = false;
				Exception exception = new PersonNotExistsException( wrapIn.getUserName() );
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleByObjectUniqueId(wrapIn.getUserName(), "人员");
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在根据被角色绑定对象的唯一标识查询角色信息列表时发生异常.Name:" + wrapIn.getUserName() + ", Type:人员" );
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