package com.x.portal.assemble.designer.jaxrs.output;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.wrap.WrapApplicationDict;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.*;
import com.x.portal.core.entity.wrap.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();

			List<Wo> wos = emc.fetchAll(Portal.class, Wo.copier);

			List<WrapPage> pageList = emc.fetchAll(Page.class, pageCopier);

			List<WrapScript> scriptList = emc.fetchAll(Script.class, scriptCopier);

			List<WrapFile> fileList = emc.fetchAll(File.class, fileCopier);

			List<WrapWidget> widgetList = emc.fetchAll(Widget.class, widgetCopier);

			List<WrapApplicationDict> applicationDictList = emc.fetchEqual(ApplicationDict.class, applicationDictCopier,
					ApplicationDict.project_FIELDNAME, ApplicationDict.PROJECT_PORTAL);

			ListTools.groupStick(wos, pageList, JpaObject.id_FIELDNAME, Page.portal_FIELDNAME, "pageList");
			ListTools.groupStick(wos, scriptList, JpaObject.id_FIELDNAME, Script.portal_FIELDNAME, "scriptList");
			ListTools.groupStick(wos, fileList, JpaObject.id_FIELDNAME, File.portal_FIELDNAME, "fileList");
			ListTools.groupStick(wos, widgetList, JpaObject.id_FIELDNAME, Widget.portal_FIELDNAME, "widgetList");
			ListTools.groupStick(wos, applicationDictList, JpaObject.id_FIELDNAME, ApplicationDict.application_FIELDNAME, "applicationDictList");
			wos = wos.stream()
					.sorted(Comparator.comparing(Wo::getAlias, Comparator.nullsLast(String::compareTo))
							.thenComparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static WrapCopier<Page, WrapPage> pageCopier = WrapCopierFactory.wo(Page.class, WrapPage.class,
			JpaObject.singularAttributeField(Page.class, true, true), null);

	public static WrapCopier<Script, WrapScript> scriptCopier = WrapCopierFactory.wo(Script.class, WrapScript.class,
			JpaObject.singularAttributeField(Script.class, true, true), null);

	public static WrapCopier<File, WrapFile> fileCopier = WrapCopierFactory.wo(File.class, WrapFile.class,
			JpaObject.singularAttributeField(File.class, true, true), null);

	public static WrapCopier<Widget, WrapWidget> widgetCopier = WrapCopierFactory.wo(Widget.class, WrapWidget.class,
			JpaObject.singularAttributeField(Widget.class, true, true), null);

	public static WrapCopier<ApplicationDict, WrapApplicationDict> applicationDictCopier = WrapCopierFactory.wo(
			ApplicationDict.class, WrapApplicationDict.class,
			JpaObject.singularAttributeField(ApplicationDict.class, true, true), null);

	public static class Wo extends WrapPortal {

		private static final long serialVersionUID = 474265667658465123L;

		public static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class,
				JpaObject.singularAttributeField(Portal.class, true, true), null);

	}

}
