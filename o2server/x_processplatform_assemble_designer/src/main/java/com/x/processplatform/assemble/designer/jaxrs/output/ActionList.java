package com.x.processplatform.assemble.designer.jaxrs.output;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.File;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.wrap.WrapApplicationDict;
import com.x.processplatform.core.entity.element.wrap.WrapFile;
import com.x.processplatform.core.entity.element.wrap.WrapForm;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.processplatform.core.entity.element.wrap.WrapScript;

class ActionList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);

			List<Wo> wos = emc.fetchAll(Application.class, Wo.copier);

			List<WrapProcess> processList = emc.fetchAll(Process.class, processCopier);
			processList.stream().forEach( o -> {
				if(StringUtils.isEmpty(o.getEdition())){
					o.setName(o.getName() + "_V1.0");
				}else{
					o.setName(o.getEditionName());
				}
			});
			processList = processList.stream().sorted(Comparator.comparing(WrapProcess::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());

			List<WrapForm> formList = emc.fetchAll(Form.class, formCopier);

			List<WrapScript> scriptList = emc.fetchAll(Script.class, scriptCopier);

			List<WrapFile> fileList = emc.fetchAll(File.class, fileCopier);

			List<WrapApplicationDict> applicationDictList = emc.fetchAll(ApplicationDict.class, applicationDictCopier);

			ListTools.groupStick(wos, processList, Application.id_FIELDNAME, Process.application_FIELDNAME,
					"processList");
			ListTools.groupStick(wos, formList, Application.id_FIELDNAME, Form.application_FIELDNAME, "formList");
			ListTools.groupStick(wos, scriptList, Application.id_FIELDNAME, Script.application_FIELDNAME, "scriptList");
			ListTools.groupStick(wos, fileList, Application.id_FIELDNAME, File.application_FIELDNAME, "fileList");
			ListTools.groupStick(wos, applicationDictList, Application.id_FIELDNAME,
					ApplicationDict.application_FIELDNAME, "applicationDictList");

			wos = wos.stream()
					.sorted(Comparator.comparing(Wo::getAlias, Comparator.nullsLast(String::compareTo))
							.thenComparing(Wo::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static WrapCopier<Process, WrapProcess> processCopier = WrapCopierFactory.wo(Process.class,
			WrapProcess.class, JpaObject.singularAttributeField(Process.class, true, true), null);

	public static WrapCopier<Form, WrapForm> formCopier = WrapCopierFactory.wo(Form.class, WrapForm.class,
			JpaObject.singularAttributeField(Form.class, true, true), null);

	public static WrapCopier<Script, WrapScript> scriptCopier = WrapCopierFactory.wo(Script.class, WrapScript.class,
			JpaObject.singularAttributeField(Script.class, true, true), null);

	public static WrapCopier<File, WrapFile> fileCopier = WrapCopierFactory.wo(File.class, WrapFile.class,
			JpaObject.singularAttributeField(File.class, true, true), null);

	public static WrapCopier<ApplicationDict, WrapApplicationDict> applicationDictCopier = WrapCopierFactory.wo(
			ApplicationDict.class, WrapApplicationDict.class,
			JpaObject.singularAttributeField(ApplicationDict.class, true, true), null);

	public static class Wo extends WrapProcessPlatform {

		private static final long serialVersionUID = 474265667658465123L;

		public static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class,
				JpaObject.singularAttributeField(Application.class, true, true), null);

	}

}