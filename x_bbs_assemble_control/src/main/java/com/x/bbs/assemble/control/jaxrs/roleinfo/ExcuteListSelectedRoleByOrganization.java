package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.BindOrganNameEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.OrganizationNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;

public class ExcuteListSelectedRoleByOrganization extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteListSelectedRoleByOrganization.class );
	
	protected ActionResult<List<WrapOutRoleInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilter wrapIn ) throws Exception {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		WrapCompany wrapCompany = null;
		WrapDepartment wrapDepartment = null;
		Boolean check = true;

		if (check) {
			if (wrapIn.getOrganizationName() == null || wrapIn.getOrganizationName().isEmpty()) {
				check = false;
				Exception exception = new BindOrganNameEmptyException();
				result.error( exception );
			}
		}
		if (check) {
			try {
				wrapCompany = userManagerService.getCompanyByName(wrapIn.getOrganizationName());
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "公司信息查询时发生异常！Company:" + wrapIn.getOrganizationName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			if (wrapCompany == null) {
				try {
					wrapDepartment = userManagerService.getDepartmentByName(wrapIn.getOrganizationName());
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "部门信息查询时发生异常！Department:" + wrapIn.getOrganizationName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			if ( wrapCompany == null || wrapDepartment == null) {
				check = false;
				Exception exception = new OrganizationNotExistsException( wrapIn.getOrganizationName() );
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleByObjectUniqueId( wrapIn.getOrganizationName(), "组织" );
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在根据被角色绑定对象的唯一标识查询角色信息列表时发生异常.Name:" + wrapIn.getOrganizationName() + ", Type:组织" );
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