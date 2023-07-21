package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;

public class ActionPersistViewRecord extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistViewRecord.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		Document document  = documentQueryService.get(id);
		if (null == document) {
			throw new ExceptionDocumentNotExists(id);
		}

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class );
		if(ListTools.isNotEmpty(wi.getRecordList())){
			for (ViewRecordWi viewRecordWi : wi.getRecordList()){
				Person person = this.userManagerService.getPerson(viewRecordWi.getPerson());
				if (person != null) {
					documentViewRecordServiceAdv.addViewRecord(id, person.getDistinguishedName(), DateTools.parse(viewRecordWi.getViewTime()));
				}
			}
		}
		Wo wo = new Wo();
		wo.setValue(true);

		return result;
	}

	public static class Wo extends WrapBoolean {

	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("阅读记录列表")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "Module", fieldValue = "{\"person\": \"阅读人员\", \"viewTime\": \"阅读时间（格式：2020-08-08）\"}")
		private List<ViewRecordWi> recordList;

		public List<ViewRecordWi> getRecordList() {
			return recordList;
		}

		public void setRecordList(List<ViewRecordWi> recordList) {
			this.recordList = recordList;
		}
	}

	public static class ViewRecordWi extends GsonPropertyObject {

		@FieldDescribe("阅读人员")
		private String person;
		@FieldDescribe("阅读时间（格式：2020-08-08）")
		private String viewTime;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getViewTime() {
			return viewTime;
		}

		public void setViewTime(String viewTime) {
			this.viewTime = viewTime;
		}
	}


}
