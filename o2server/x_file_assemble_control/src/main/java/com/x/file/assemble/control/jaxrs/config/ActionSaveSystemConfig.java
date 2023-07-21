package com.x.file.assemble.control.jaxrs.config;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.FileConfig;

public class ActionSaveSystemConfig extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			/* 判断当前用户是否有权限访问 */
			if(!business.controlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			FileConfig fileConfig = emc.firstEqual(FileConfig.class, FileConfig.person_FIELDNAME, Business.SYSTEM_CONFIG);
			emc.beginTransaction(FileConfig.class);
			if(fileConfig!=null){
				if(wi.getCapacity()==null || wi.getCapacity() < 1){
					fileConfig.setCapacity(0);
				}else{
					fileConfig.setCapacity(wi.getCapacity());
				}
				if(wi.getRecycleDays()==null || wi.getRecycleDays() < 1){
					fileConfig.setRecycleDays(FileConfig.DEFAULT_RECYCLE_DAYS);
				}else{
					fileConfig.setRecycleDays(wi.getRecycleDays());
				}
				fileConfig.getProperties().setFileTypeIncludes(wi.getFileTypeIncludes());
				fileConfig.getProperties().setFileTypeExcludes(wi.getFileTypeExcludes());
				emc.check(fileConfig, CheckPersistType.all);
			}else{
				fileConfig = Wi.copier.copy(wi);
				if(fileConfig.getCapacity()==null || fileConfig.getCapacity() < 1){
					fileConfig.setCapacity(0);
				}
				if(wi.getRecycleDays()==null || wi.getRecycleDays() < 1){
					fileConfig.setRecycleDays(FileConfig.DEFAULT_RECYCLE_DAYS);
				}
				fileConfig.setPerson(Business.SYSTEM_CONFIG);
				fileConfig.getProperties().setFileTypeIncludes(wi.getFileTypeIncludes());
				fileConfig.getProperties().setFileTypeExcludes(wi.getFileTypeExcludes());
				emc.persist(fileConfig, CheckPersistType.all);
			}
			emc.commit();
			CacheManager.notify(FileConfig.class);
			Wo wo = new Wo();
			wo.setId(fileConfig.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends FileConfig {

		private static final long serialVersionUID = 6275876832668367557L;

		static WrapCopier<Wi, FileConfig> copier = WrapCopierFactory.wi(Wi.class, FileConfig.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, FileConfig.properties_FIELDNAME));

		@FieldDescribe("只允许上传的文件后缀")
		private List<String> fileTypeIncludes = new ArrayList<>();

		@FieldDescribe("不允许上传的文件后缀")
		private List<String> fileTypeExcludes = new ArrayList<>();

		public List<String> getFileTypeIncludes() {
			return fileTypeIncludes;
		}

		public void setFileTypeIncludes(List<String> fileTypeIncludes) {
			this.fileTypeIncludes = fileTypeIncludes;
		}

		public List<String> getFileTypeExcludes() {
			return fileTypeExcludes;
		}

		public void setFileTypeExcludes(List<String> fileTypeExcludes) {
			this.fileTypeExcludes = fileTypeExcludes;
		}
	}

	public static class Wo extends WoId {

	}

}
