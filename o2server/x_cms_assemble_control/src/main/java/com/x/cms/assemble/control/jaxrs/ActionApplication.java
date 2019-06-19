package com.x.cms.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.cms.assemble.control.jaxrs.appdict.AppDictAction;
import com.x.cms.assemble.control.jaxrs.appdict.AppDictAnonymousAction;
import com.x.cms.assemble.control.jaxrs.appdictdesign.AppDictDesignAction;
import com.x.cms.assemble.control.jaxrs.appinfo.AppInfoAction;
import com.x.cms.assemble.control.jaxrs.appinfo.AppInfoAnonymousAction;
import com.x.cms.assemble.control.jaxrs.categoryinfo.CategoryInfoAction;
import com.x.cms.assemble.control.jaxrs.categoryinfo.CategoryInfoAnonymousAction;
import com.x.cms.assemble.control.jaxrs.comment.DocumentCommentInfoAction;
import com.x.cms.assemble.control.jaxrs.data.DataAction;
import com.x.cms.assemble.control.jaxrs.document.DocumentAction;
import com.x.cms.assemble.control.jaxrs.document.DocumentAnonymousAction;
import com.x.cms.assemble.control.jaxrs.document.DocumentCipherAction;
import com.x.cms.assemble.control.jaxrs.document.DocumentViewRecordAction;
import com.x.cms.assemble.control.jaxrs.file.FileAction;
import com.x.cms.assemble.control.jaxrs.fileinfo.FileInfoAction;
import com.x.cms.assemble.control.jaxrs.fileinfo.FileInfoAnonymousAction;
import com.x.cms.assemble.control.jaxrs.form.FormAction;
import com.x.cms.assemble.control.jaxrs.form.FormAnonymousAction;
import com.x.cms.assemble.control.jaxrs.image.ImageBase64Action;
import com.x.cms.assemble.control.jaxrs.input.InputAction;
import com.x.cms.assemble.control.jaxrs.log.LogAction;
import com.x.cms.assemble.control.jaxrs.output.OutputAction;
import com.x.cms.assemble.control.jaxrs.permission.PermissionAction;
import com.x.cms.assemble.control.jaxrs.permission.PermissionForDocumentAction;
import com.x.cms.assemble.control.jaxrs.queryview.QueryViewAction;
import com.x.cms.assemble.control.jaxrs.queryviewdesign.QueryViewDesignAction;
import com.x.cms.assemble.control.jaxrs.script.ScriptAction;
import com.x.cms.assemble.control.jaxrs.script.ScriptAnonymousAction;
import com.x.cms.assemble.control.jaxrs.search.SearchFilterAction;
import com.x.cms.assemble.control.jaxrs.templateform.TemplateFormAction;
import com.x.cms.assemble.control.jaxrs.uuid.UUIDAction;
import com.x.cms.assemble.control.jaxrs.view.ViewAction;
import com.x.cms.assemble.control.jaxrs.viewcategory.ViewCategoryAction;
import com.x.cms.assemble.control.jaxrs.viewfieldconfig.ViewFieldConfigAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		
		this.classes.add(TemplateFormAction.class);
		this.classes.add(AppInfoAction.class);
		this.classes.add(CategoryInfoAction.class);
		this.classes.add(DataAction.class);
		this.classes.add(DocumentAction.class);
		this.classes.add(DocumentCipherAction.class);
		this.classes.add(PermissionForDocumentAction.class);
		this.classes.add(DocumentViewRecordAction.class);
		this.classes.add(FileInfoAction.class);
		this.classes.add(FileAction.class);
		this.classes.add(FormAction.class);
		this.classes.add(ViewAction.class);
		this.classes.add(QueryViewDesignAction.class);
		this.classes.add(QueryViewAction.class);
		this.classes.add(ViewCategoryAction.class);
		this.classes.add(ViewFieldConfigAction.class);
		this.classes.add(ImageBase64Action.class);
		this.classes.add(LogAction.class);
		this.classes.add(UUIDAction.class);
		this.classes.add(AppDictAction.class);
		this.classes.add(AppDictDesignAction.class);
		this.classes.add(ScriptAction.class);
		this.classes.add(SearchFilterAction.class);
		this.classes.add(InputAction.class);
		this.classes.add(OutputAction.class);
		this.classes.add(PermissionAction.class);
		
		this.classes.add(AppInfoAnonymousAction.class);
		this.classes.add(AppDictAnonymousAction.class);
		this.classes.add(CategoryInfoAnonymousAction.class);
		this.classes.add(DocumentAnonymousAction.class);
		this.classes.add(FileInfoAnonymousAction.class);
		this.classes.add(FormAnonymousAction.class);
		this.classes.add(ScriptAnonymousAction.class);
		this.classes.add(DocumentCommentInfoAction.class);

		return this.classes;
	}

}