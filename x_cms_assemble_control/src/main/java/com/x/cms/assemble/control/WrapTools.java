package com.x.cms.assemble.control;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.WrapInAppCategoryAdmin;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.WrapOutAppCategoryAdmin;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.WrapInAppCategoryPermission;
import com.x.cms.assemble.control.jaxrs.appcategorypermission.WrapOutAppCategoryPermission;
import com.x.cms.assemble.control.jaxrs.appinfo.WrapInAppInfo;
import com.x.cms.assemble.control.jaxrs.appinfo.WrapOutAppInfo;
import com.x.cms.assemble.control.jaxrs.categoryinfo.WrapInCategoryInfo;
import com.x.cms.assemble.control.jaxrs.categoryinfo.WrapOutCategoryInfo;
import com.x.cms.assemble.control.jaxrs.document.WrapInDocument;
import com.x.cms.assemble.control.jaxrs.document.WrapInDocumentPictureInfo;
import com.x.cms.assemble.control.jaxrs.document.WrapOutDocument;
import com.x.cms.assemble.control.jaxrs.document.WrapOutDocumentComplexFile;
import com.x.cms.assemble.control.jaxrs.document.WrapOutDocumentPictureInfo;
import com.x.cms.assemble.control.jaxrs.documentpermission.WrapInDocumentPermission;
import com.x.cms.assemble.control.jaxrs.documentpermission.WrapOutDocumentPermission;
import com.x.cms.assemble.control.jaxrs.documentviewrecord.WrapOutDocumentViewRecord;
import com.x.cms.assemble.control.jaxrs.fileinfo.WrapOutFileInfo;
import com.x.cms.assemble.control.jaxrs.form.WrapInForm;
import com.x.cms.assemble.control.jaxrs.form.WrapOutForm;
import com.x.cms.assemble.control.jaxrs.form.WrapOutSimpleForm;
import com.x.cms.assemble.control.jaxrs.script.WrapOutScript;
import com.x.cms.assemble.control.jaxrs.view.WrapInView;
import com.x.cms.assemble.control.jaxrs.view.WrapOutView;
import com.x.cms.assemble.control.jaxrs.viewcategory.WrapOutViewCategory;
import com.x.cms.assemble.control.jaxrs.viewfieldconfig.WrapInViewFieldConfig;
import com.x.cms.assemble.control.jaxrs.viewfieldconfig.WrapOutViewFieldConfig;
import com.x.cms.core.entity.AppCategoryAdmin;
import com.x.cms.core.entity.AppCategoryPermission;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPermission;
import com.x.cms.core.entity.DocumentPictureInfo;
import com.x.cms.core.entity.DocumentViewRecord;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class WrapTools {
	
	public static BeanCopyTools<Document, WrapOutDocument> document_wrapout_copier = BeanCopyToolsBuilder.create(Document.class, WrapOutDocument.class, null, WrapOutDocument.Excludes);
	public static BeanCopyTools<WrapInDocument, Document> document_wrapin_copier = BeanCopyToolsBuilder.create( WrapInDocument.class, Document.class, null, WrapInDocument.Excludes );
	
	public static BeanCopyTools<WrapInDocumentPictureInfo, DocumentPictureInfo> picture_wrapin_copier = BeanCopyToolsBuilder.create( WrapInDocumentPictureInfo.class, DocumentPictureInfo.class, null, WrapInDocumentPictureInfo.Excludes );
	public static BeanCopyTools<DocumentPictureInfo, WrapOutDocumentPictureInfo> picture_wrapout_copier = BeanCopyToolsBuilder.create(DocumentPictureInfo.class, WrapOutDocumentPictureInfo.class, null, WrapOutDocumentPictureInfo.Excludes);
	
	public static BeanCopyTools<FileInfo, WrapOutDocumentComplexFile> fileForDoc_wrapout_copier = BeanCopyToolsBuilder.create(FileInfo.class, WrapOutDocumentComplexFile.class, null, WrapOutDocumentComplexFile.Excludes);
	public static BeanCopyTools<FileInfo, WrapOutFileInfo> file_wrapout_copier = BeanCopyToolsBuilder.create( FileInfo.class, WrapOutFileInfo.class, null, WrapOutFileInfo.Excludes);
	
	public static BeanCopyTools<AppCategoryAdmin, WrapOutAppCategoryAdmin> appCategoryAdmin_wrapout_copier = BeanCopyToolsBuilder.create(AppCategoryAdmin.class, WrapOutAppCategoryAdmin.class, null, WrapOutAppCategoryAdmin.Excludes);
	public static BeanCopyTools<WrapInAppCategoryAdmin, AppCategoryAdmin> appCategoryAdmin_wrapin_copier = BeanCopyToolsBuilder.create(WrapInAppCategoryAdmin.class, AppCategoryAdmin.class, null, WrapInAppCategoryAdmin.Excludes);
	
	public static BeanCopyTools<AppCategoryPermission, WrapOutAppCategoryPermission> appCategoryPermission_wrapout_copier = BeanCopyToolsBuilder.create( AppCategoryPermission.class, WrapOutAppCategoryPermission.class, null, WrapOutAppCategoryPermission.Excludes);
	public static BeanCopyTools<WrapInAppCategoryPermission, AppCategoryPermission> appCategoryPermission_wrapoin_copier = BeanCopyToolsBuilder.create( WrapInAppCategoryPermission.class, AppCategoryPermission.class, null, WrapInAppCategoryPermission.Excludes );
	
	public static BeanCopyTools<WrapInAppInfo, AppInfo> appInfo_wrapin_copier = BeanCopyToolsBuilder.create(WrapInAppInfo.class, AppInfo.class, null, WrapInAppInfo.Excludes);
	public static BeanCopyTools<AppInfo, WrapOutAppInfo> appInfo_wrapout_copier = BeanCopyToolsBuilder.create(AppInfo.class, WrapOutAppInfo.class, null, WrapOutAppInfo.Excludes);
	
	public static BeanCopyTools<CategoryInfo, WrapOutCategoryInfo> category_wrapout_copier = BeanCopyToolsBuilder.create(CategoryInfo.class, WrapOutCategoryInfo.class, null, WrapOutCategoryInfo.Excludes);
	public static BeanCopyTools<WrapInCategoryInfo, CategoryInfo> category_wrapin_copier = BeanCopyToolsBuilder.create( WrapInCategoryInfo.class, CategoryInfo.class, null, WrapInCategoryInfo.Excludes );
	
	public static BeanCopyTools<DocumentPermission, WrapOutDocumentPermission> documentPermission_wrapout_copier = BeanCopyToolsBuilder.create( DocumentPermission.class, WrapOutDocumentPermission.class, null, WrapOutDocumentPermission.Excludes);
	public static BeanCopyTools<WrapInDocumentPermission, DocumentPermission> documentPermission_wrapin_copier = BeanCopyToolsBuilder.create( WrapInDocumentPermission.class, DocumentPermission.class, null, WrapInDocumentPermission.Excludes );
	
	public static BeanCopyTools<DocumentViewRecord, WrapOutDocumentViewRecord> documentViewRecord_wrapout_copier = BeanCopyToolsBuilder.create( DocumentViewRecord.class, WrapOutDocumentViewRecord.class, null, WrapOutDocumentViewRecord.Excludes);
	
	public static BeanCopyTools<Form, WrapOutSimpleForm> formsimple_wrapout_copier = BeanCopyToolsBuilder.create(Form.class, WrapOutSimpleForm.class, null, WrapOutSimpleForm.Excludes);
	public static BeanCopyTools<Form, WrapOutForm> form_wrapout_copier = BeanCopyToolsBuilder.create(Form.class, WrapOutForm.class, null, WrapOutForm.Excludes);
	public static BeanCopyTools<WrapInForm, Form> form_wrapin_copier_in = BeanCopyToolsBuilder.create(WrapInForm.class, Form.class, null, WrapInForm.Excludes);
	
	public static BeanCopyTools<WrapInView, View> view_wrapin_copier = BeanCopyToolsBuilder.create( WrapInView.class, View.class, null, WrapInView.Excludes );
	public static BeanCopyTools<View, WrapOutView> view_wrapout_copier = BeanCopyToolsBuilder.create( View.class, WrapOutView.class, null, WrapOutView.Excludes);
	
	public static BeanCopyTools<ViewCategory, WrapOutViewCategory> viewCategory_wrapout_copier = BeanCopyToolsBuilder.create( ViewCategory.class, WrapOutViewCategory.class, null, WrapOutViewCategory.Excludes);
	
	public static BeanCopyTools<ViewFieldConfig, WrapOutViewFieldConfig> viewFieldConfig_wrapout_copier = BeanCopyToolsBuilder.create( ViewFieldConfig.class, WrapOutViewFieldConfig.class, null, WrapOutViewFieldConfig.Excludes);
	public static BeanCopyTools<WrapInViewFieldConfig, ViewFieldConfig> viewFieldConfig_wrapin_copier = BeanCopyToolsBuilder.create( WrapInViewFieldConfig.class, ViewFieldConfig.class, null, WrapInViewFieldConfig.Excludes );
	
	public static BeanCopyTools<Script, WrapOutScript> script_wrapout_copier = BeanCopyToolsBuilder.create(Script.class, WrapOutScript.class);
}