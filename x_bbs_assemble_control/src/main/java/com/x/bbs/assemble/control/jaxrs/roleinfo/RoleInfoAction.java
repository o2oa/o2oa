package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.WrapOutPermissionInfo;
import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSUserRole;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapPerson;

@Path("user/role")
public class RoleInfoAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(RoleInfoAction.class);
	private BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	private BeanCopyTools<BBSPermissionInfo, WrapOutPermissionInfo> permission_wrapout_copier = BeanCopyToolsBuilder
			.create(BBSPermissionInfo.class, WrapOutPermissionInfo.class, null, WrapOutPermissionInfo.Excludes);
	private BeanCopyTools<BBSRoleInfo, WrapOutRoleInfo> wrapout_copier = BeanCopyToolsBuilder.create(BBSRoleInfo.class,
			WrapOutRoleInfo.class, null, WrapOutRoleInfo.Excludes);
	private BeanCopyTools<WrapInRoleInfo, BBSRoleInfo> wrapin_copier = BeanCopyToolsBuilder.create(WrapInRoleInfo.class,
			BBSRoleInfo.class, null, WrapInRoleInfo.Excludes);
	private BeanCopyTools<BBSUserRole, WrapOutUserRole> userRole_wrapout_copier = BeanCopyToolsBuilder
			.create(BBSUserRole.class, WrapOutUserRole.class, null, WrapOutUserRole.Excludes);

	@HttpMethodDescribe(value = "获取所有RoleInfo的信息列表.", response = WrapOutRoleInfo.class)
	@GET
	@Path("all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
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
				Exception exception = new RoleInfoListAllException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}

		if (check) {
			if (roleInfoList != null && roleInfoList.size() > 0) {
				try {
					wraps = wrapout_copier.copy(roleInfoList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据论坛ID查询论坛的角色列表.", response = WrapOutRoleInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutRoleInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
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
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				roleInfo = roleInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}

		if (check) {
			if( roleInfo == null ){
				check = false;
				Exception exception = new RoleInfoNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			// 如果角色信息存在，然后再查询该角色所拥有的所有权限信息
			try {
				permissionList = permissionInfoService.listPermissionByRoleCode(roleInfo.getRoleCode());
			} catch (Exception e) {
				check = false;
				Exception exception = new PermissionListByRoleCodeException( e, roleInfo.getRoleCode() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}

		if (check) {
			if (permissionList != null && permissionList.size() > 0) {
				try {
					permissionWraps = permission_wrapout_copier.copy(permissionList);
				} catch (Exception e) {
					check = false;
					Exception exception = new PermissinWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}

		if (check) {
			try {
				wrap = wrapout_copier.copy(roleInfo);
				wrap.setPermissions(permissionWraps);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoWrapOutException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据论坛ID查询论坛的角色列表.", request = JsonElement.class, response = WrapOutRoleInfo.class)
	@PUT
	@Path("forum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByForum(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		BBSForumInfo forumInfo = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		if (check) {
			if (wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty()) {
				check = false;
				Exception exception = new ForumIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get(wrapIn.getForumId());
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoQueryByIdException( e, wrapIn.getForumId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (forumInfo == null) {
				check = false;
				Exception exception = new ForumInfoNotExistsException( wrapIn.getForumId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleByForumId(wrapIn.getForumId());
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoListByForumIdException( e, wrapIn.getForumId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (roleInfoList != null) {
				try {
					wraps = wrapout_copier.copy(roleInfoList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "把一个或者多个角色CODE绑定到一个对象（对象名称#对象类型）上.", request = JsonElement.class, response = WrapOutBoolean.class)
	@PUT
	@Path("bind/object")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response bindRoleToUser(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		EffectivePerson currentPerson = this.effectivePerson(request);
		BindObject bindObject = null;
		Object object = null;
		wrap.setValue( false );
		
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		if (check) {
			if (wrapIn.getBindObject() == null || wrapIn.getBindObject().getObjectName() == null || wrapIn.getBindObject().getObjectName().isEmpty()) {
				check = false;
				Exception exception = new BindObjectNameEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
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
						logger.error( exception, currentPerson, request, null);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new PersonQueryException( e, bindObject.getObjectName() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} else if ("部门".equals(bindObject.getObjectType())) {
				try {
					object = userManagerService.getDepartmentByName(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new DepartmentNotExistsException( bindObject.getObjectName() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentQueryException( e, bindObject.getObjectName() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} else if ("公司".equals(bindObject.getObjectType())) {
				try {
					object = userManagerService.getCompanyByName(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new CompanyNotExistsException( bindObject.getObjectName() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new CompanyQueryException( e, bindObject.getObjectName() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} else if ("群组".equals(bindObject.getObjectType())) {
				try {
					object = userManagerService.getGroupByName(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new GroupNotExistsException( bindObject.getObjectName() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new GroupQueryException( e, bindObject.getObjectName() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} else {
				check = false;
				Exception exception = new BindObjectTypeInvalidException( bindObject.getObjectType() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				roleInfoService.bindRoleToUser(wrapIn.getBindObject(), wrapIn.getBindRoleCodes());
				wrap.setValue( true );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoBindException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				checkUserPermission(wrapIn.getBindObject());
			} catch (Exception e) {
				logger.warn("system check user permission got an exception!");
				logger.error(e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	public void checkUserPermission( BindObject bindObject ) throws Exception {
		// 把wrapIn.getBindObject()解析成人员列表，对每一个人员进行权限分析
		List<String> userNames = new ArrayList<>();
		if (bindObject != null) {
			if ("组织".equals(bindObject.getObjectType())) {
				try {
					userNames = userManagerService.listUserNamesByOrganization(bindObject.getObjectName());
				} catch (Exception e) {
					throw e;
				}
			} else if ("群组".equals(bindObject.getObjectType())) {
				try {
					userNames = userManagerService.listUserNamesByGroupName(bindObject.getObjectName());
				} catch (Exception e) {
					throw e;
				}
			} else {
				// 当它是人员
				userNames.add(bindObject.getObjectName());
			}
		}
		if (userNames != null && !userNames.isEmpty()) {
			for (String name : userNames) {
				checkUserPermission(name);
			}
		}
	}

	public void checkUserPermission( String userName ) {
		Gson gson = null;
		List<String> roleCodes = null;
		List<String> permissionCodes = null;
		RoleAndPermission roleAndPermission = null;
		String permissionContent = null;

		roleAndPermission = new RoleAndPermission();
		roleAndPermission.setPerson(userName);
		// 检查该员的角色和权限信息
		try {
			roleCodes = roleInfoService.listAllRoleCodesForUser(userName);
			roleAndPermission.setRoleInfoList(roleCodes);
		} catch (Exception e) {
			logger.warn("system list all role for user got an exception." );
			logger.error(e);
		}
		if (roleCodes != null && !roleCodes.isEmpty()) {
			try {
				permissionCodes = permissionInfoService.listPermissionCodesByRoleCodes(roleCodes);
				roleAndPermission.setPermissionInfoList(permissionCodes);
			} catch (Exception e) {
				logger.warn("system list all permission for user got an exception." );
				logger.error(e);
			}
		}
		try {
			gson = XGsonBuilder.pureGsonDateFormated();
			permissionContent = gson.toJson(roleAndPermission);
		} catch (Exception e) {
			logger.warn("system translate object to json got an exception." );
			logger.error(e);
		}
		try {// 从数据库中查询出人员信息，进行信息更新
			userInfoService.updatePermission(userName, permissionContent);
		} catch (Exception e) {
			logger.warn("system save user info got an exception. username:" + userName );
			logger.error(e);
		}
	}

	@HttpMethodDescribe(value = "把一个或者多个对象（对象名称#对象类型）绑定到一个角色CODE上.", request = JsonElement.class, response = WrapOutBoolean.class)
	@PUT
	@Path("bind/role")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response bindUserToRole(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutBoolean wrap = new WrapOutBoolean();
		Object object = null;
		wrap.setValue( false );
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		if (check) {
			if (wrapIn.getBindRoleCode() == null || wrapIn.getBindRoleCode().isEmpty()) {
				check = false;
				Exception exception = new BindRoleCodeEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (wrapIn.getBindObjectArray() != null && !wrapIn.getBindObjectArray().isEmpty()) {
				// 遍历所有的对象，检查对象是否真实存在
				for (BindObject bindObject : wrapIn.getBindObjectArray()) {
					if ("人员".equals(bindObject.getObjectType())) {
						try {
							object = userManagerService.getPersonByName(bindObject.getObjectName());
							if (object == null) {
								check = false;
								Exception exception = new PersonNotExistsException( bindObject.getObjectName() );
								result.error( exception );
								logger.error( exception, currentPerson, request, null);
							}
						} catch (Exception e) {
							check = false;
							Exception exception = new PersonQueryException( e, bindObject.getObjectName() );
							result.error( exception );
							logger.error( exception, currentPerson, request, null);
						}
					} else if ("部门".equals(bindObject.getObjectType())) {
						try {
							object = userManagerService.getDepartmentByName(bindObject.getObjectName());
							if (object == null) {
								check = false;
								Exception exception = new DepartmentNotExistsException( bindObject.getObjectName() );
								result.error( exception );
								logger.error( exception, currentPerson, request, null);
							}
						} catch (Exception e) {
							check = false;
							Exception exception = new DepartmentQueryException( e, bindObject.getObjectName() );
							result.error( exception );
							logger.error( exception, currentPerson, request, null);
						}
					} else if ("公司".equals(bindObject.getObjectType())) {
						try {
							object = userManagerService.getCompanyByName(bindObject.getObjectName());
							if (object == null) {
								check = false;
								Exception exception = new CompanyNotExistsException( bindObject.getObjectName() );
								result.error( exception );
								logger.error( exception, currentPerson, request, null);
							}
						} catch (Exception e) {
							check = false;
							Exception exception = new CompanyQueryException( e, bindObject.getObjectName() );
							result.error( exception );
							logger.error( exception, currentPerson, request, null);
						}
					} else if ("群组".equals(bindObject.getObjectType())) {
						try {
							object = userManagerService.getGroupByName(bindObject.getObjectName());
							if (object == null) {
								check = false;
								Exception exception = new GroupNotExistsException( bindObject.getObjectName() );
								result.error( exception );
								logger.error( exception, currentPerson, request, null);
							}
						} catch (Exception e) {
							check = false;
							Exception exception = new GroupQueryException( e, bindObject.getObjectName() );
							result.error( exception );
							logger.error( exception, currentPerson, request, null);
						}
					} else {
						check = false;
						Exception exception = new BindObjectTypeInvalidException( bindObject.getObjectType() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				}
			}
		}

		// 校验人员和角色编码的合法性
		if (check) {
			try {
				roleInfoService.bindUserToRole(wrapIn.getBindRoleCode(), wrapIn.getBindObjectArray());
				wrap.setValue( true );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoBindException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据版块ID查询版块的角色列表.", request = JsonElement.class, response = WrapOutRoleInfo.class)
	@PUT
	@Path("section/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listBySection(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		BBSSectionInfo sectionInfo = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		if (check) {
			if (wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty()) {
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				sectionInfo = sectionInfoServiceAdv.get(wrapIn.getSectionId());
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionQueryByIdException( e, wrapIn.getSectionId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (sectionInfo == null) {
				check = false;
				Exception exception = new SectionNotExistsException( wrapIn.getSectionId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleBySectionId(wrapIn.getSectionId());
			} catch (Exception e) {
				check = false;
				result.error(e);
				Exception exception = new RoleInfoListBySectionIdException( e, wrapIn.getSectionId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (roleInfoList != null) {
				try {
					wraps = wrapout_copier.copy(roleInfoList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据组织名称查询角色列表.", request = JsonElement.class, response = WrapOutRoleInfo.class)
	@PUT
	@Path("organization/selected")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSelectedRoleByOrganization(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		BBSSectionInfo sectionInfo = null;
		WrapCompany wrapCompany = null;
		WrapDepartment wrapDepartment = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		if (check) {
			if (wrapIn.getOrganizationName() == null || wrapIn.getOrganizationName().isEmpty()) {
				check = false;
				Exception exception = new BindOrganNameEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				wrapCompany = userManagerService.getCompanyByName(wrapIn.getOrganizationName());
			} catch (Exception e) {
				check = false;
				Exception exception = new CompanyQueryException( e, wrapIn.getOrganizationName() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}

		if (check) {
			if (wrapCompany == null) {
				try {
					wrapDepartment = userManagerService.getDepartmentByName(wrapIn.getOrganizationName());
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentQueryException( e, wrapIn.getOrganizationName() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}

		if (check) {
			if ( wrapCompany == null || wrapDepartment == null) {
				check = false;
				Exception exception = new OrganizationNotExistsException( wrapIn.getOrganizationName() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleByObjectUniqueId( wrapIn.getOrganizationName(), "组织" );
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoListByObjectException( e, wrapIn.getOrganizationName(),"组织" );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (roleInfoList != null) {
				try {
					wraps = wrapout_copier.copy(roleInfoList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据用户姓名查询角色列表.", request = JsonElement.class, response = WrapOutRoleInfo.class)
	@PUT
	@Path("user/selected")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSelectedRoleByUser(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		WrapPerson wrapPerson = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}

		if (check) {
			if (wrapIn.getUserName() == null || wrapIn.getUserName().isEmpty()) {
				check = false;
				Exception exception = new BindUserNameEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				wrapPerson = userManagerService.getPersonByName( wrapIn.getUserName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new PersonQueryException( e, wrapIn.getUserName() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if ( wrapPerson == null) {
				check = false;
				Exception exception = new PersonNotExistsException( wrapIn.getUserName() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleByObjectUniqueId(wrapIn.getUserName(), "人员");
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoListByObjectException( e, wrapIn.getUserName(), "人员" );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (roleInfoList != null) {
				try {
					wraps = wrapout_copier.copy(roleInfoList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据角色编码查询绑定的对象列表.", request = JsonElement.class, response = WrapOutUserRole.class)
	@PUT
	@Path("rolecode/selected")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listBindObjectByRoleCode(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<List<WrapOutUserRole>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutUserRole> wraps = null;
		List<BBSUserRole> userRoleList = null;
		BBSRoleInfo roleInfo = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		if (check) {
			if (wrapIn.getBindRoleCode() == null || wrapIn.getBindRoleCode().isEmpty()) {
				check = false;
				Exception exception = new BindRoleCodeEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				roleInfo = roleInfoService.getByRoleCode( wrapIn.getBindRoleCode() );
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoQueryByCodeException( e, wrapIn.getBindRoleCode() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (roleInfo == null) {
				check = false;
				Exception exception = new RoleInfoNotExistsException( wrapIn.getBindRoleCode() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				userRoleList = roleInfoService.listUserRoleByRoleCode(roleInfo.getRoleCode());
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoListByRoleCodeException( e, wrapIn.getBindRoleCode() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (userRoleList != null) {
				try {
					wraps = userRole_wrapout_copier.copy(userRoleList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoWrapOutException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建新的角色信息或者更新角色信息.", request = WrapInRoleInfo.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInRoleInfo wrapIn = null;
		WrapOutId wrap = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSRoleInfo roleInfo_old = null;
		BBSRoleInfo roleInfo = new BBSRoleInfo();
		List<String> permissionCodes = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		String uniCode = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInRoleInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		if (check) {
			if (wrapIn.getRoleName() == null || wrapIn.getRoleName().isEmpty()) {
				check = false;
				Exception exception = new RoleINameEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		// 校验角色类别
		if (check) {
			if (wrapIn.getRoleType() == null || wrapIn.getRoleType().isEmpty()) {
				check = false;
				Exception exception = new RoleITypeEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		// 检验信息，如果是论坛角色，那么论坛ID为必须填写，如果是版块角色那么版块ID必须填写
		if (check) {
			if ("论坛角色".equals(wrapIn.getRoleType())) {
				if (wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty()) {
					check = false;
					Exception exception = new ForumIdEmptyException();
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} else if ("版块角色".equals(wrapIn.getRoleType())) {
				if (wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty()) {
					check = false;
					Exception exception = new SectionIdEmptyException();
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} else {
				check = false;
				Exception exception = new RoleITypeInvalidException( wrapIn.getRoleType() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		// 检验传入的版块ID和论坛ID是否合法
		if (check) {
			if ("论坛角色".equals(wrapIn.getRoleType())) {
				try {
					forumInfo = forumInfoServiceAdv.get(wrapIn.getForumId());
					if (forumInfo == null) {
						check = false;
						Exception exception = new ForumInfoNotExistsException( wrapIn.getForumId() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					} else {
						uniCode = forumInfo.getId();
						wrapIn.setForumId(forumInfo.getId());
						wrapIn.setForumName(forumInfo.getForumName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ForumInfoQueryByIdException( e, wrapIn.getForumId() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} else if ("版块角色".equals(wrapIn.getRoleType())) {
				try {
					sectionInfo = sectionInfoServiceAdv.get(wrapIn.getSectionId());
					if (sectionInfo == null) {
						check = false;
						Exception exception = new SectionNotExistsException( wrapIn.getSectionId() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					} else {
						uniCode = sectionInfo.getId();
						wrapIn.setSectionId(sectionInfo.getId());
						wrapIn.setSectionName(sectionInfo.getSectionName());
						wrapIn.setForumId(sectionInfo.getForumId());
						wrapIn.setForumName(sectionInfo.getForumName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new SectionQueryByIdException( e, wrapIn.getSectionId() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if (check) {
			permissionCodes = wrapIn.getPermissionCodes();
			wrapIn.setCreatorName(currentPerson.getName());
		}
		if (check) {
			if (wrapIn.getRoleCode() == null || wrapIn.getRoleCode().isEmpty()) {
				try {
					// 自动组织一个角色编码：BBS + 角色名称的拼音 + ID
					wrapIn.setRoleCode( "BBS_" + PinyinHelper.getShortPinyin( wrapIn.getRoleName()).toUpperCase() + "_" + uniCode );
				} catch (Exception e) {
					check = false;
					Exception exception = new SectionQueryByIdException( e, wrapIn.getRoleName() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if (check) {
			try {
				wrapin_copier.copy(wrapIn, roleInfo);
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoWrapInException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			if (roleInfo.getRoleCode() != null && !roleInfo.getRoleCode().isEmpty()) {
				try {
					roleInfo_old = roleInfoService.getByRoleCode(roleInfo.getRoleCode());
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoQueryByCodeException( e, roleInfo.getRoleCode() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if (check) {
			try {
				roleInfo = roleInfoService.save(roleInfo, permissionCodes);
				wrap = new WrapOutId(roleInfo.getId());
				result.setData(wrap);
				if (roleInfo_old != null) {
					operationRecordService.roleOperation(currentPerson.getName(), roleInfo, "MODIFY", hostIp, hostName);
				} else {
					operationRecordService.roleOperation(currentPerson.getName(), roleInfo, "CREATE", hostIp, hostName);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoSaveException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}

		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除指定的角色信息.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		BBSRoleInfo roleInfo = null;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new RoleInfoIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if (check) {
			// 查询角色信息是否存在
			try {
				roleInfo = roleInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}

		if (check) {
			if (roleInfo == null) {
				check = false;
				Exception exception = new RoleInfoNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}

		if (check) {
			try {
				roleInfoService.delete(id);
				wrap = new WrapOutId(id);
				result.setData(wrap);
				operationRecordService.roleOperation(currentPerson.getName(), roleInfo, "DELETE", hostIp, hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoDeleteException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}

		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}