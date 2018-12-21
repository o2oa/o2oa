package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.RescissoryClass_AppCategoryAdminServiceAdv;
import com.x.cms.assemble.control.service.RescissoryClass_AppCategoryPermissionServiceAdv;
import com.x.cms.assemble.control.service.RescissoryClass_DocumentPermissionServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;

public class BaseAction extends StandardJaxrsAction {
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	
	//暂用于数据转换
	protected RescissoryClass_AppCategoryAdminServiceAdv appCategoryAdminServiceAdv = new RescissoryClass_AppCategoryAdminServiceAdv();
	protected RescissoryClass_AppCategoryPermissionServiceAdv appCategoryPermissionServiceAdv = new RescissoryClass_AppCategoryPermissionServiceAdv();
	protected RescissoryClass_DocumentPermissionServiceAdv documentPermissionServiceAdv = new RescissoryClass_DocumentPermissionServiceAdv();
	protected DocumentInfoServiceAdv documentServiceAdv = new DocumentInfoServiceAdv();

}
