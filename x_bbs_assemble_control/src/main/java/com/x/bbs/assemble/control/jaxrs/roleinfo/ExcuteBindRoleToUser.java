package com.x.bbs.assemble.control.jaxrs.roleinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.BindObjectNameEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.BindObjectTypeInvalidException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.CompanyNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.DepartmentNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.GroupNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.PersonNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;

public class ExcuteBindRoleToUser extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteBindRoleToUser.class );
	
	protected ActionResult<WrapOutBoolean> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilter wrapIn ) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		BindObject bindObject = null;
		Object object = null;
		wrap.setValue( false );
		Boolean check = true;
		if (check) {
			if (wrapIn.getBindObject() == null || wrapIn.getBindObject().getObjectName() == null || wrapIn.getBindObject().getObjectName().isEmpty()) {
				check = false;
				Exception exception = new BindObjectNameEmptyException();
				result.error( exception );
			} else {
				bindObject = wrapIn.getBindObject();
			}
		}
		if (check) {
			// 遍历所有的对象，检查对象是否真实存在
			if ("人员".equals(wrapIn.getBindObject().getObjectType())) {
				try {
					object = userManagerService.getPersonByName(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new PersonNotExistsException( bindObject.getObjectName() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "人员信息查询时发生异常！Person:" + bindObject.getObjectName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			} else if ("部门".equals(bindObject.getObjectType())) {
				try {
					object = userManagerService.getDepartmentByName(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new DepartmentNotExistsException( bindObject.getObjectName() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "部门信息查询时发生异常！Department:" + bindObject.getObjectName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			} else if ("公司".equals(bindObject.getObjectType())) {
				try {
					object = userManagerService.getCompanyByName(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new CompanyNotExistsException( bindObject.getObjectName() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "公司信息查询时发生异常！Company:" + bindObject.getObjectName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			} else if ("群组".equals(bindObject.getObjectType())) {
				try {
					object = userManagerService.getGroupByName(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new GroupNotExistsException( bindObject.getObjectName() );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "群组信息查询时发生异常！Group:" + bindObject.getObjectName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			} else {
				check = false;
				Exception exception = new BindObjectTypeInvalidException( bindObject.getObjectType() );
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfoService.bindRoleToUser(wrapIn.getBindObject(), wrapIn.getBindRoleCodes());
				wrap.setValue( true );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在根据人员姓名以及角色编码列表进行角色绑定时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				checkUserPermission( wrapIn.getBindObject() );
			} catch (Exception e) {
				logger.warn("system check user permission got an exception!");
				logger.error(e);
			}
		}
		return result;
	}

}