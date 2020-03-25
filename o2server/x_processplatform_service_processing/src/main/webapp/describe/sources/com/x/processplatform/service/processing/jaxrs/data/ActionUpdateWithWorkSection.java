//package com.x.processplatform.service.processing.jaxrs.data;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map.Entry;
//import java.util.concurrent.Callable;
//
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.math.NumberUtils;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.project.annotation.ActionLogger;
//import com.x.base.core.project.exception.ExceptionEntityNotExist;
//import com.x.base.core.project.executor.ProcessPlatformExecutorFactory;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.jaxrs.WoId;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.base.core.project.tools.ListTools;
//import com.x.processplatform.core.entity.content.Work;
//import com.x.processplatform.service.processing.Business;
//import com.x.processplatform.service.processing.jaxrs.data.ActionUpdateWithWorkPath7.Wo;
//
//class ActionUpdateWithWorkSection extends BaseAction {
//
//	@ActionLogger
//	private static Logger logger = LoggerFactory.getLogger(ActionUpdateWithWorkSection.class);
//
//	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
//
//		ActionResult<Wo> result = new ActionResult<>();
//		Wo wo = new Wo();
//		String executorSeed = null;
//
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			Work work = emc.fetch(id, Work.class, ListTools.toList(Work.job_FIELDNAME));
//			if (null == work) {
//				throw new ExceptionEntityNotExist(id, Work.class);
//			}
//			executorSeed = work.getJob();
//		}
//		Callable<String> callable = new Callable<String>() {
//			public String call() throws Exception {
//				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//					/* 防止提交空数据清空data */
//					if (null == jsonElement || (!jsonElement.isJsonObject())) {
//						throw new ExceptionNotJsonObject();
//					}
//					if (jsonElement.getAsJsonObject().entrySet().isEmpty()) {
//						throw new ExceptionEmptyData();
//					}
//					Business business = new Business(emc);
//					Work work = emc.find(id, Work.class);
//					if (null == work) {
//						throw new ExceptionEntityNotExist(id, Work.class);
//					}
//					wo.setId(work.getId());
//					/* 进行区段数据合并 */
//					SectionData sectionData = gson.fromJson(jsonElement, SectionData.class);
//					JsonElement merged;
//					if (sectionData.hasSection()) {
//						JsonElement data = sectionData.getData();
//						JsonElement source = getData(business, work.getJob());
//						for (Section section : sectionData.getSectionList()) {
//							if (StringUtils.isNotEmpty(section.getPath())) {
//								data = mergeSection(data, section.paths(), section.getKey(), source);
//							}
//						}
//						merged = data;
//					} else {
//						merged = sectionData.getData();
//					}
//					/** 先更新title和serial,再更新DataItem,因为旧的DataItem中也有title和serial数据. */
//					updateTitleSerial(business, work, merged);
//					updateData(business, work, merged);
//					/* updateTitleSerial 和 updateData 方法内进行了提交 */
//				}
//				return "";
//			}
//		};
//		ProcessPlatformExecutorFactory.get(executorSeed).submit(callable).get();
//
//		result.setData(wo);
//		return result;
//	}
//
//	private JsonElement mergeSection(JsonElement data, String[] paths, String key, JsonElement source)
//			throws Exception {
//		JsonObject data_part_object = this.navigateElseEmptyObject(data, paths).getAsJsonObject();
//		JsonObject source_part_object = this.navigateElseEmptyObject(source, paths).getAsJsonObject();
//		for (Entry<String, JsonElement> entry : source_part_object.entrySet()) {
//			if (!StringUtils.equals(key, entry.getKey())) {
//				data_part_object.add(entry.getKey(), entry.getValue());
//			}
//		}
//		return data;
//	}
//
//	private JsonElement navigateElseEmptyObject(JsonElement jsonElement, String[] paths) throws Exception {
//		/* 判断在前 */
//		if (null == jsonElement || jsonElement.isJsonPrimitive() || jsonElement.isJsonNull()) {
//			return new JsonObject();
//		}
//		if (paths.length == 0) {
//			return jsonElement;
//		}
//
//		if (jsonElement.isJsonArray()) {
//			return navigateElseEmptyObject(jsonElement.getAsJsonArray().get(NumberUtils.toInt(paths[0])),
//					ArrayUtils.remove(paths, 0));
//		}
//		return navigateElseEmptyObject(jsonElement.getAsJsonObject().get(paths[0]), ArrayUtils.remove(paths, 0));
//	}
//
//	public static class SectionData {
//
//		private JsonElement data;
//
//		private List<Section> sectionList = new ArrayList<>();
//
//		public boolean hasSection() {
//			if (ListTools.isNotEmpty(this.sectionList)) {
//				for (Section section : this.sectionList) {
//					if (StringUtils.isNotEmpty(section.getPath())) {
//						return true;
//					}
//				}
//			}
//			return false;
//		}
//
//		public JsonElement getData() {
//			return data;
//		}
//
//		public void setData(JsonElement data) {
//			this.data = data;
//		}
//
//		public List<Section> getSectionList() {
//			return sectionList;
//		}
//
//		public void setSectionList(List<Section> sectionList) {
//			this.sectionList = sectionList;
//		}
//	}
//
//	public static class Section {
//
//		public String[] paths() {
//			return StringUtils.split(path, ".");
//		}
//
//		private String path;
//
//		private String key;
//
//		public String getPath() {
//			return path;
//		}
//
//		public void setPath(String path) {
//			this.path = path;
//		}
//
//		public String getKey() {
//			return key;
//		}
//
//		public void setKey(String key) {
//			this.key = key;
//		}
//	}
//
//	public static class Wo extends WoId {
//
//	}
//
//}
