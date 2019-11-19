package com.x.cms.assemble.control.jaxrs.output;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryExt;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.File;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.wrap.*;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();

			List<Wo> wos = emc.fetchAll(AppInfo.class, Wo.copier);
			List<WrapCategoryInfo> categoryList = emc.fetchAll(CategoryInfo.class, categoryInfoCopier);
			List<WrapForm> formList = emc.fetchAll(Form.class, formCopier);
			List<WrapScript> scriptList = emc.fetchAll(Script.class, scriptCopier);
			List<WrapAppDict> appDictList = emc.fetchAll(AppDict.class, appDictCopier);
			List<WrapFile> fileList = emc.fetchAll(File.class, fileCopier);

			ListTools.groupStick(wos, categoryList, "id", "appId", "categoryInfoList");
			ListTools.groupStick(wos, formList, "id", "appId", "formList");
			ListTools.groupStick(wos, scriptList, "id", "appId", "scriptList");
			ListTools.groupStick(wos, appDictList, "id", "appId", "appDictList");
			ListTools.groupStick(wos, fileList, AppInfo.id_FIELDNAME, File.appId_FIELDNAME, "fileList");
			
			wos = wos.stream()
					.sorted(Comparator.comparing(Wo::getAppAlias, Comparator.nullsLast(String::compareTo))
							.thenComparing(Wo::getAppName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());

			result.setData(wos);
			return result;
		}
	}

	public static WrapCopier<File, WrapFile> fileCopier = WrapCopierFactory.wo(File.class, WrapFile.class,
			JpaObject.singularAttributeField(File.class, true, true), null);
	
	public static WrapCopier<CategoryInfo, WrapCategoryInfo> categoryInfoCopier = WrapCopierFactory.wo(
			CategoryInfo.class, WrapCategoryInfo.class,
			JpaObject.singularAttributeField(CategoryInfo.class, true, true), null);

	public static WrapCopier<Form, WrapForm> formCopier = WrapCopierFactory.wo(Form.class, WrapForm.class,
			JpaObject.singularAttributeField(Form.class, true, true), null);

	public static WrapCopier<Script, WrapScript> scriptCopier = WrapCopierFactory.wo(Script.class, WrapScript.class,
			JpaObject.singularAttributeField(Script.class, true, true), null);

	public static WrapCopier<AppDict, WrapAppDict> appDictCopier = WrapCopierFactory.wo(AppDict.class,
			WrapAppDict.class, JpaObject.singularAttributeField(AppDict.class, true, true), null);

	public static class Wo extends WrapCms {
		private static final long serialVersionUID = 474265667658465123L;
		public static WrapCopier<AppInfo, Wo> copier = WrapCopierFactory.wo(AppInfo.class, Wo.class,
				JpaObject.singularAttributeField(AppInfo.class, true, true), null);
	}
}