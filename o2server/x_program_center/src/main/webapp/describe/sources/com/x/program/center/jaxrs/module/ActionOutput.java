package com.x.program.center.jaxrs.module;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_portal_assemble_designer;
import com.x.base.core.project.x_processplatform_assemble_designer;
import com.x.base.core.project.x_query_assemble_designer;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.program.center.ThisApplication;
import com.x.program.center.WrapModule;
import com.x.program.center.core.entity.Structure;
import com.x.query.core.entity.wrap.WrapQuery;

import net.sf.ehcache.Element;

public class ActionOutput extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionOutput.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			wo.setName(wi.getName());
			for (WrapProcessPlatform wiProcessPlatform : wi.getProcessPlatformList()) {
				WrapProcessPlatform wrap = ThisApplication.context().applications()
						.putQuery(x_processplatform_assemble_designer.class,
								Applications.joinQueryUri("output", wiProcessPlatform.getId(), "select"),
								wiProcessPlatform)
						.getData(WrapProcessPlatform.class);
				wo.getProcessPlatformList().add(wrap);
			}
			for (WrapPortal wiPortal : wi.getPortalList()) {
				WrapPortal wrap = ThisApplication.context().applications()
						.putQuery(x_portal_assemble_designer.class,
								Applications.joinQueryUri("output", wiPortal.getId(), "select"), wiPortal)
						.getData(WrapPortal.class);
				wo.getPortalList().add(wrap);
			}
			for (WrapCms wiCms : wi.getCmsList()) {
				WrapCms wrap = ThisApplication.context().applications()
						.putQuery(x_cms_assemble_control.class,
								Applications.joinQueryUri("output", wiCms.getId(), "select"), wiCms)
						.getData(WrapCms.class);
				wo.getCmsList().add(wrap);
			}
			for (WrapQuery wiQuery : wi.getQueryList()) {
				WrapQuery wrap = ThisApplication.context().applications()
						.putQuery(x_query_assemble_designer.class,
								Applications.joinQueryUri("output", wiQuery.getId(), "select"), wiQuery)
						.getData(WrapQuery.class);
				wo.getQueryList().add(wrap);
			}
			wo.setFlag(StringTools.uniqueToken());
			CacheObject cacheObject = new CacheObject();
			cacheObject.setModule(wo);
			this.cache.put(new Element(wo.getFlag(), cacheObject));
			emc.beginTransaction(Structure.class);
			Structure structure = new Structure();
			if (StringUtils.isEmpty(wi.getStructure())) {
				structure = new Structure();
				structure.setName(wi.getName());
				structure.setDescription(wi.getDescription());
				structure.setData(gson.toJson(wi));
				emc.persist(structure, CheckPersistType.all);
			} else {
				structure = emc.find(wi.getStructure(), Structure.class);
				if (null == structure) {
					throw new ExceptionEntityNotExist(wi.getStructure(), Structure.class);
				}
				structure.setName(wi.getName());
				structure.setDescription(wi.getDescription());
				structure.setData(gson.toJson(wi));
				emc.check(structure, CheckPersistType.all);
			}
			emc.commit();
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends WrapModule {

		static WrapCopier<Wi, WrapModule> copier = WrapCopierFactory.wo(Wi.class, WrapModule.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, JpaObject.id_FIELDNAME, "category"));

		@FieldDescribe("结构标识")
		private String structure;

		public String getStructure() {
			return structure;
		}

		public void setStructure(String structure) {
			this.structure = structure;
		}

	}

	public static class Wo extends WrapModule {

		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

	}
}