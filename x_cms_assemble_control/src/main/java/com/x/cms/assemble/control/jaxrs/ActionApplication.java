package com.x.cms.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.application.jaxrs.AbstractActionApplication;
import com.x.cms.assemble.control.jaxrs.appcatagoryadmin.AppCatagoryAdminAction;
import com.x.cms.assemble.control.jaxrs.appcatagorypermission.AppCatagoryPermissionAction;
import com.x.cms.assemble.control.jaxrs.appdict.AppDictAction;
import com.x.cms.assemble.control.jaxrs.appdictitem.AppDictItemAction;
import com.x.cms.assemble.control.jaxrs.appinfo.AppInfoAction;
import com.x.cms.assemble.control.jaxrs.catagoryinfo.CatagoryInfoAction;
import com.x.cms.assemble.control.jaxrs.data.DataAction;
import com.x.cms.assemble.control.jaxrs.document.DocumentAction;
import com.x.cms.assemble.control.jaxrs.fileinfo.FileInfoAction;
import com.x.cms.assemble.control.jaxrs.form.FormAction;
import com.x.cms.assemble.control.jaxrs.image.ImageBase64Action;
import com.x.cms.assemble.control.jaxrs.log.LogAction;
import com.x.cms.assemble.control.jaxrs.queryview.QueryViewAction;
import com.x.cms.assemble.control.jaxrs.queryviewdesign.QueryViewDesignAction;
import com.x.cms.assemble.control.jaxrs.script.ScriptAction;
import com.x.cms.assemble.control.jaxrs.search.SearchFilterAction;
import com.x.cms.assemble.control.jaxrs.uuid.UUIDAction;
import com.x.cms.assemble.control.jaxrs.view.ViewAction;
import com.x.cms.assemble.control.jaxrs.viewcatagory.ViewCatagoryAction;
import com.x.cms.assemble.control.jaxrs.viewfieldconfig.ViewFieldConfigAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {

		this.classes.add(AppCatagoryAdminAction.class);
		this.classes.add(AppCatagoryPermissionAction.class);
		this.classes.add(AppInfoAction.class);
		this.classes.add(CatagoryInfoAction.class);
		this.classes.add(DataAction.class);
		this.classes.add(DocumentAction.class);
		this.classes.add(FileInfoAction.class);
		this.classes.add(FormAction.class);
		this.classes.add(ViewAction.class);
		this.classes.add(QueryViewDesignAction.class);
		this.classes.add(QueryViewAction.class);
		this.classes.add(ViewCatagoryAction.class);
		this.classes.add(ViewFieldConfigAction.class);
		this.classes.add(ImageBase64Action.class);
		this.classes.add(LogAction.class);
		this.classes.add(UUIDAction.class);
		this.classes.add(AppDictAction.class);
		this.classes.add(AppDictItemAction.class);
		this.classes.add(ScriptAction.class);
		this.classes.add(SearchFilterAction.class);

		return this.classes;
	}

}