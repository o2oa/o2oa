package com.x.cms.assemble.control.jaxrs.document;

import com.google.gson.JsonElement;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class ActionPersistViewRecord extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistViewRecord.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, String id, JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		Document document  = documentQueryService.get(id);
		if (null == document) {
			throw new ExceptionDocumentNotExists(id);
		}

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class );
		if(ListTools.isNotEmpty(wi.getPersonList())){
			for (String flag : wi.getPersonList()){
				Person person = this.userManagerService.getPerson(flag);
				if (person != null) {
					documentViewRecordServiceAdv.addViewRecord(id, person.getDistinguishedName());
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

		private List<String> personList;

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}
	}
}
