package com.x.mind.assemble.control.jaxrs.mind;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.mind.assemble.control.service.MindFolderInfoService;
import com.x.mind.assemble.control.service.MindInfoService;
import com.x.mind.assemble.control.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction{
	/**
	 * 脑图sss信息操作服务器
	 */
	protected MindInfoService mindInfoService = new MindInfoService();
	
	/**
	 * 脑图文件夹信息操作服务
	 */
	protected MindFolderInfoService mindFolderInfoService = new MindFolderInfoService();
	
	/**
	 * 用户信息管理服务
	 */
	protected UserManagerService userManagerService = new UserManagerService();
}
