package com.x.custom.index.assemble.control.jaxrs.reveal;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.custom.index.assemble.control.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.core.express.index.Directory;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.index.WoField;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDirectoryField extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDirectoryField.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		List<String> categories = Business.categories(wi.getDirectoryList());
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Pair<List<Application>, List<AppInfo>> pair = business.listApplicationAppInfo();
			pair = this.filterEditable(effectivePerson, business, pair);
			IndexReader[] indexReaders = Indexs.indexReaders(wi.getDirectoryList());
			if (indexReaders.length == 0) {
				return result;
			}
			try (MultiReader multiReader = new MultiReader(indexReaders)) {
				wo.setDynamicFieldList(Business.listDynamicField(categories, multiReader));
			}
			wo.setFixedFieldList(Business.listFixedField(categories));
			wo.setFacetFieldList(facetFields(categories));

		}
		result.setData(wo);
		return result;
	}

	private List<WoField> facetFields(List<String> categories) {
		List<WoField> list = new ArrayList<>();
		list.add(new WoField(Indexs.FIELD_CATEGORY, Indexs.FIELD_CATEGORY, Indexs.FIELD_TYPE_STRING));
		list.add(new WoField(Indexs.FIELD_CREATETIMEMONTH, Indexs.FIELD_CREATETIMEMONTH, Indexs.FIELD_TYPE_STRING));
		list.add(new WoField(Indexs.FIELD_UPDATETIMEMONTH, Indexs.FIELD_UPDATETIMEMONTH, Indexs.FIELD_TYPE_STRING));

		if (ListTools.contains(categories, Indexs.CATEGORY_PROCESSPLATFORM)) {
			list.add(new WoField(Indexs.FIELD_APPLICATIONNAME, Indexs.FIELD_APPLICATIONNAME, Indexs.FIELD_TYPE_STRING));
			list.add(new WoField(Indexs.FIELD_PROCESSNAME, Indexs.FIELD_PROCESSNAME, Indexs.FIELD_TYPE_STRING));
			list.add(new WoField(Indexs.FIELD_COMPLETED, Indexs.FIELD_COMPLETED, Indexs.FIELD_TYPE_BOOLEAN));
		}
		if (ListTools.contains(categories, Indexs.CATEGORY_CMS)) {
			list.add(new WoField(Indexs.FIELD_APPNAME, Indexs.FIELD_APPNAME, Indexs.FIELD_TYPE_STRING));
			list.add(new WoField(Indexs.FIELD_CATEGORYNAME, Indexs.FIELD_CATEGORYNAME, Indexs.FIELD_TYPE_STRING));
		}
		list.add(new WoField(Indexs.FIELD_CREATORPERSON, Indexs.FIELD_CREATORPERSON, Indexs.FIELD_TYPE_STRING));
		list.add(new WoField(Indexs.FIELD_CREATORUNIT, Indexs.FIELD_CREATORUNIT, Indexs.FIELD_TYPE_STRING));
		return list;
	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionDirectoryField$Wi")
	public class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -7247122401328873164L;

		@FieldDescribe("检索目录.")
		@Schema(description = "检索目录.")
		private List<Directory> directoryList = new ArrayList<>();

		public List<Directory> getDirectoryList() {
			return directoryList;
		}

		public void setDirectoryList(List<Directory> directoryList) {
			this.directoryList = directoryList;
		}

	}

	@Schema(name = "com.x.custom.index.assemble.control.jaxrs.reveal.ActionDirectoryField$Wo")
	public class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -7247122401328873164L;

		@FieldDescribe("维度字段.")
		@Schema(description = "维度字段.")
		private List<WoField> facetFieldList = new ArrayList<>();

		@FieldDescribe("固定字段.")
		@Schema(description = "固定字段.")
		private List<WoField> fixedFieldList = new ArrayList<>();

		@FieldDescribe("动态字段.")
		@Schema(description = "动态字段.")
		private List<WoField> dynamicFieldList = new ArrayList<>();

		public List<WoField> getFacetFieldList() {
			return facetFieldList;
		}

		public void setFacetFieldList(List<WoField> facetFieldList) {
			this.facetFieldList = facetFieldList;
		}

		public List<WoField> getFixedFieldList() {
			return fixedFieldList;
		}

		public void setFixedFieldList(List<WoField> fixedFieldList) {
			this.fixedFieldList = fixedFieldList;
		}

		public List<WoField> getDynamicFieldList() {
			return dynamicFieldList;
		}

		public void setDynamicFieldList(List<WoField> dynamicFieldList) {
			this.dynamicFieldList = dynamicFieldList;
		}

	}

}