package com.x.bbs.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.MethodExcuteResult;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSUserInfo;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * 用户组织顶层组织信息管理服务类
 * 
 * @author LIYI
 *
 */
public class UserPermissionService {

	private static  Logger logger = LoggerFactory.getLogger(UserPermissionService.class);
	private Ehcache cache = ApplicationCache.instance().getCache(BBSUserInfo.class);

	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	private UserManagerService userManagerService = new UserManagerService();

	public Boolean hasPermission(String userName, String permissionCode) {
		RoleAndPermission roleAndPermission = null;
		List<String> permissionCodeList = null;
		try {
			roleAndPermission = getUserRoleAndPermission(userName);
			if (roleAndPermission != null) {
				permissionCodeList = roleAndPermission.getPermissionInfoList();
			}
			if ( ListTools.isNotEmpty( permissionCodeList )) {
				for (String _permissionCode : permissionCodeList) {
					if (permissionCode.equalsIgnoreCase(_permissionCode)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.warn("系统根据员工查询论坛用户权限角色信息列表时发生异常！");
			logger.error(e);
		}

		return false;
	}

	public List<BBSPermissionInfo> getUserPermissionInfoList(String userName) {
		RoleAndPermission roleAndPermission = null;
		List<BBSPermissionInfo> permissionList = null;
		List<String> permissionCodes = null;
		try {
			roleAndPermission = getUserRoleAndPermission(userName);
			if (roleAndPermission != null) {
				permissionCodes = roleAndPermission.getPermissionInfoList();
			}
			if ( ListTools.isNotEmpty( permissionCodes )) {
				permissionList = permissionInfoService.listPermissionByCodes(permissionCodes);
			}
		} catch (Exception e) {
			logger.warn("系统根据员工查询论坛用户权限角色信息列表时发生异常！");
			logger.error(e);
		}
		return permissionList;
	}

	/**
	 * 获取人员的权限信息，优先使用缓存的人员信息
	 * 
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public RoleAndPermission getUserRoleAndPermission(String userName) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String content = null;
		BBSUserInfo userInfo = null;
		RoleAndPermission roleAndPermission = null;

		if (userName == null) {
			throw new Exception("user name is null!");
		}

		userInfo = getUserInfoFromCache(userName);

		if (userInfo != null) {
			content = userInfo.getPermissionContent();
			if (content != null) {
				roleAndPermission = gson.fromJson(content, RoleAndPermission.class);
			}
		}

		return roleAndPermission;
	}

	/**
	 * 获取用户信息，优先使用缓存信息
	 * 
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public BBSUserInfo getUserInfoFromCache(String userName) throws Exception {
		BBSUserInfo userInfo = null;
		Boolean check = true;
		if (userName == null) {
			throw new Exception("user name is null!");
		}

		String cacheKey = ThisApplication.getRoleAndPermissionCacheKey(userName);
		Element element = cache.get(cacheKey);

		if ((null != element) && (null != element.getObjectValue())) {
			return (BBSUserInfo) element.getObjectValue();
		} else {
			if (check) {
				try {
					userInfo = userInfoService.getByUserName(userName);
				} catch (Exception e) {
					check = false;
					logger.warn("系统根据员工查询论坛用户信息时发生异常！");
					logger.error(e);
				}
				// 如果还为空，那么重新组成一个新的用户信息，扫描所有权限信息
				if (userInfo == null) {
					userInfo = composeUserRoleAndPermission(userName);
					return userInfo;
				}
			}
		}
		cache.put(new Element(cacheKey, userInfo));
		return userInfo;
	}

	public RoleAndPermission getUserRoleAndPermissionForLogin(String userName) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String content = null;
		BBSUserInfo userInfo = null;
		RoleAndPermission roleAndPermission = null;
		Boolean check = true;
		if (userName == null) {
			throw new Exception("user name is null!");
		}
		if (check) {
			userInfo = composeUserRoleAndPermission(userName);
		}
		if (check) {
			content = userInfo.getPermissionContent();
			if (content != null) {
				roleAndPermission = gson.fromJson(content, RoleAndPermission.class);
			}
		}
		return roleAndPermission;
	}

	private BBSUserInfo composeUserRoleAndPermission(String userName) throws Exception {
		Gson gson = null;
		String content = null;
		BBSUserInfo userInfo = null;
		RoleAndPermission roleAndPermission = null;
		List<String> roleCodes = null;
		List<String> permissionCodes = null;
		Boolean check = true;
		Boolean isBBSManager = false;

		if (userName == null) {
			throw new Exception("userName is null!");
		} else {
			if (check) {
				try {
					roleCodes = roleInfoService.listAllRoleCodesForUser(userName);
				} catch (Exception e) {
					check = false;
					logger.warn("系统根据员工查询所有的角色信息时发生异常！");
					logger.error(e);
				}
			}
			if (check) {
				if ( ListTools.isNotEmpty( roleCodes )) {
					try {
						permissionCodes = permissionInfoService.listPermissionCodesByRoleCodes(roleCodes);
					} catch (Exception e) {
						check = false;
						logger.warn("系统根据员工查询所有的权限信息时发生异常！");
						logger.error(e);
					}
				}
			}
			if (check) {
				isBBSManager = userManagerService.isHasPlatformRole(userName, ThisApplication.BBSMANAGER);
				roleAndPermission = new RoleAndPermission();
				roleAndPermission.setPerson(userName);
				roleAndPermission.setRoleInfoList(roleCodes);
				roleAndPermission.setPermissionInfoList(permissionCodes);
				roleAndPermission.setIsBBSManager(isBBSManager);
			}

			if (check) {
				if (roleAndPermission != null) {
					gson = XGsonBuilder.instance();
					content = gson.toJson(roleAndPermission);
				}
			}
			if (check) {
				try {
					userInfo = userInfoService.getByUserName(userName);
				} catch (Exception e) {
					check = false;
					logger.warn("系统根据员工查询论坛用户信息时发生异常！");
					logger.error(e);
				}

				if (userInfo == null) {
					userInfo = new BBSUserInfo();
					userInfo.setNickName(userName);
					userInfo.setUserName(userName);
					userInfo.setLastOperationTime(new Date());
					userInfo.setLastVisitTime(new Date());
					userInfo.setOnline(true);
				}
				userInfo.setPermissionContent(content);
				userInfo = userInfoService.save(userInfo);

				String cacheKey = ThisApplication.getRoleAndPermissionCacheKey(userName);
				cache.put(new Element(cacheKey, userInfo));
			}
		}
		return userInfo;
	}

	public void logout(String name) throws Exception {
		if (name == null || name.isEmpty()) {
			throw new Exception("name is null!");
		}
		userInfoService.logout(name);
	}

	public MethodExcuteResult getViewForumIdsFromUserPermission( EffectivePerson currentPerson ) {
		MethodExcuteResult methodExcuteResult = new MethodExcuteResult();
		List<String> ids = new ArrayList<String>();
		List<BBSPermissionInfo> permissionList = null;
		List<BBSPermissionInfo> forumViewPermissionList = null;

		// 如果不是匿名用户，则查询该用户所有能访问的论坛信息
		if ( currentPerson != null && !"anonymous".equalsIgnoreCase(currentPerson.getTokenType().name())) {
			if ( methodExcuteResult.getSuccess()) {// 获取用户拥有的所有权限列表
				try {
					permissionList = getUserPermissionInfoList( currentPerson.getDistinguishedName() );
				} catch (Exception e) {
					methodExcuteResult.setSuccess(false);
					methodExcuteResult.error(e);
					methodExcuteResult.setMessage("系统获取用户所拥有的权限列表时发生异常");
					logger.warn(
							"system get all permission list from ThisApplication.userPermissionInfoMap got an exception!");
					logger.error(e);
				}
			}
			if ( methodExcuteResult.getSuccess() ) {// 获取用户可以访问的论坛相关权限
				try {
					forumViewPermissionList = permissionInfoService.filterPermissionListByPermissionFunction("FORUM_VIEW", permissionList );
				} catch (Exception e) {
					methodExcuteResult.setSuccess(false);
					methodExcuteResult.error(e);
					methodExcuteResult.setMessage("系统从用户所拥有的权限里过滤论坛访问权限时发生异常");
					logger.warn("system filter FORUM_VIEW permission from user permission list got an exception!");
					logger.error(e);
				}
			}
			
			if ( methodExcuteResult.getSuccess() ) {// 获取可访问的论坛ID列表
				if ( ListTools.isNotEmpty( forumViewPermissionList )) {
					for (BBSPermissionInfo permission : forumViewPermissionList) {
						ids.add(permission.getForumId());
					}
				}
			}
		}

		methodExcuteResult.setBackObject( ids );

		return methodExcuteResult;
	}

	public MethodExcuteResult getViewSectionIdsFromUserPermission(EffectivePerson currentPerson) {
		MethodExcuteResult methodExcuteResult = new MethodExcuteResult();
		List<String> ids = new ArrayList<String>();
		List<BBSPermissionInfo> permissionList = null;
		List<BBSPermissionInfo> sectionViewPermissionList = null;
		// 如果不是匿名用户，则查询该用户所有能访问的论坛信息
		if (currentPerson != null && !"anonymous".equalsIgnoreCase(currentPerson.getTokenType().name())) {
			if (methodExcuteResult.getSuccess()) {// 获取用户拥有的所有权限列表
				try {
					permissionList = getUserPermissionInfoList(currentPerson.getDistinguishedName());
				} catch (Exception e) {
					methodExcuteResult.setSuccess(false);
					methodExcuteResult.error(e);
					methodExcuteResult.setMessage("系统获取用户所拥有的权限列表时发生异常");
					logger.warn(
							"system get all permission list from ThisApplication.userPermissionInfoMap got an exception!");
					logger.error(e);
				}
			}
			if (methodExcuteResult.getSuccess()) {// 获取用户可以访问的论坛相关权限
				try {
					sectionViewPermissionList = permissionInfoService
							.filterPermissionListByPermissionFunction("SECTION_VIEW", permissionList);
				} catch (Exception e) {
					methodExcuteResult.setSuccess(false);
					methodExcuteResult.error(e);
					methodExcuteResult.setMessage("系统从用户所拥有的权限里过滤论坛访问权限时发生异常");
					logger.warn("system filter FORUM_VIEW permission from user permission list got an exception!");
					logger.error(e);
				}
			}
			if (methodExcuteResult.getSuccess()) {// 获取可访问的论坛ID列表
				if ( ListTools.isNotEmpty( sectionViewPermissionList )) {
					for (BBSPermissionInfo permission : sectionViewPermissionList) {
						ids.add(permission.getSectionId());
					}
				}
			}
		}
		methodExcuteResult.setBackObject(ids);
		return methodExcuteResult;
	}

}