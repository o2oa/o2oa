package com.x.cms.assemble.control.jaxrs.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.JsonArrayOperator;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import org.apache.commons.lang3.StringUtils;

class ActionUpdateArrayDataWithDocument extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateArrayDataWithDocument.class);
	private static final String OPERATE_ADD = "add";
	private static final String OPERATE_DELETE = "delete";
	private static final String OPERATE_MOVE = "move";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> id);
		ActionResult<Wo> result = new ActionResult<>();
		if (null == jsonElement || (!jsonElement.isJsonObject())) {
			throw new ExceptionNotJsonObject();
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(wi.getData() != null && (!jsonElement.isJsonObject())){
			throw new ExceptionNotJsonObject();
		}
		if(StringUtils.isEmpty(wi.getPath())){
			throw new ExceptionFieldEmpty("path");
		}
		if(StringUtils.isEmpty(wi.getMethod())){
			throw new ExceptionFieldEmpty("method");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = emc.find(id, Document.class);
			if (null == document) {
				throw new ExceptionDocumentNotExists(id);
			}
			if (!business.isDocumentEditor(effectivePerson, null, null, document)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			JsonElement source = getData(business, id);
			JsonElement data = this.convert(source, wi);
			JsonElement merge = XGsonBuilder.merge(data, source);
			this.updateData(business, document, merge);
			Wo wo = new Wo();
			wo.setId(document.getId());
			result.setData(wo);
		}
		CacheManager.notify( Document.class );
		return result;
	}

	private JsonElement convert(JsonElement oldData, Wi wi) throws Exception {
		if(oldData!=null && !oldData.isJsonArray()){
			throw new ExceptionCustom("指定的path路径数据不是数组对象");
		}
		JsonArray arrayData = oldData == null ? new JsonArray() : oldData.getAsJsonArray();
		switch (wi.getMethod()) {
		case OPERATE_ADD:
			arrayData = JsonArrayOperator.insert(arrayData, wi.getIndex(), wi.getData().getAsJsonObject());
			break;
		case OPERATE_DELETE:
			arrayData = JsonArrayOperator.remove(arrayData, wi.getIndex());
			break;
		case OPERATE_MOVE:
			arrayData = JsonArrayOperator.move(arrayData, wi.getIndex(), wi.getToIndex());
			break;
		default:
			throw new ExceptionCustom("操作类型不匹配：" + wi.getMethod());
		}
		JsonObject newData = new JsonObject();
		this.addJsonToPath(newData, wi.getPath(), arrayData);
		return newData;
	}

	private void addJsonToPath(JsonObject root, String path, JsonElement value) {
		String[] pathParts = StringUtils.split(path, PATH_DOT);
		JsonObject current = root;

		for (int i = 0; i < pathParts.length - 1; i++) {
			String part = pathParts[i];
			if (!current.has(part)) {
				current.add(part, new JsonObject());
			}
			current = current.getAsJsonObject(part);
		}

		current.add(pathParts[pathParts.length - 1], value);
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("数组操作方法：add|delete|move")
		private String method;
		@FieldDescribe("新增的数组下标或删除的数组下标或者移动的起始下标")
		private Integer index;
		@FieldDescribe("移动的目标下标")
		private Integer toIndex;
		@FieldDescribe("数据对象在业务数据中的path路径，如datatable.data")
		private String path;
		@FieldDescribe("json数据对象")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "JsonElement", fieldValue = "{}")
		private JsonElement data;

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public Integer getIndex() {
			return index == null ? 0 : index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public Integer getToIndex() {
			return toIndex == null ? 0 : toIndex;
		}

		public void setToIndex(Integer toIndex) {
			this.toIndex = toIndex;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2942168134266650614L;

	}

}
